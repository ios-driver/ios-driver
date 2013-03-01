/*
 * Copyright 2012 ios-driver committers.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.uiautomation.ios.server.grid;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.Device;
import org.uiautomation.ios.server.IOSServerConfiguration;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.application.IOSApplication;

import java.net.URL;
import java.util.*;

public class RegistrationRequest {

  private String hubURL;
  private String nodeHost;
  private List<Map<String, Object>> capabilities = new ArrayList<Map<String, Object>>();
  private Map<String, Object> configuration = new HashMap<String, Object>();


  public RegistrationRequest(IOSServerConfiguration config, IOSServerManager driver) {
    this.hubURL = config.getRegistrationURL();
    this.nodeHost = config.getHost();
    int port = config.getPort();

    Set<IOSApplication> supportedApps = driver.getSupportedApplications();

    for (IOSApplication app : supportedApps) {
      IOSCapabilities cap = IOSCapabilities.ipad(app.getMetadata("CFBundleDisplayName"));
      cap.setCapability("browserName", "IOS Simulator");
      if (cap.getBundleName().equals("Safari")) {
        cap.setSDKVersion(app.getMetadata("DTSDKName").replace("iphonesimulator", ""));
        addCapabilityForDevices(cap);
      } else {
        addCapabilityForEachSDKAndDevice(driver, cap);
      }
    }

    configuration.put("remoteHost", "http://" + nodeHost + ":" + port);
    configuration.put("maxSession", 1);
  }

  private void addCapabilityForEachSDKAndDevice(IOSServerManager driver, IOSCapabilities appCapabilities) {
    for (String availableSDK : driver.getHostInfo().getInstalledSDKs()) {
      appCapabilities.setSDKVersion(availableSDK);
      addCapabilityForDevices(appCapabilities);
    }
  }

  private void addCapabilityForDevices(IOSCapabilities appCapabilities) {
    appCapabilities.setDevice(Device.ipad);
    capabilities.add(new HashMap(appCapabilities.getRawCapabilities()));

    appCapabilities.setDevice(Device.iphone);
    capabilities.add(new HashMap(appCapabilities.getRawCapabilities()));
  }

  public void registerToHub() {
    HttpClient client = new DefaultHttpClient();
    try {
      URL registration = new URL(hubURL);

      BasicHttpEntityEnclosingRequest r =
          new BasicHttpEntityEnclosingRequest("POST", registration.toExternalForm());

      String json = getJSONRequest().toString();

      r.setEntity(new StringEntity(json));

      HttpHost host = new HttpHost(registration.getHost(), registration.getPort());
      HttpResponse response = client.execute(host, r);
      if (response.getStatusLine().getStatusCode() != 200) {
        throw new RuntimeException("Error sending the registration request.");
      }
    } catch (Exception e) {
      throw new WebDriverException("Error sending the registration request.", e);
    }

  }

  private JSONObject getJSONRequest() {
    JSONObject res = new JSONObject();
    try {
      res.put("class", getClass().getCanonicalName());
      res.put("configuration", configuration);
      JSONArray caps = new JSONArray();
      for (Map<String, Object> c : capabilities) {
        caps.put(c);
      }
      res.put("capabilities", caps);
    } catch (JSONException e) {
      throw new RuntimeException("Error encoding to JSON " + e.getMessage(), e);
    }

    return res;
  }
}
