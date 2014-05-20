/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
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

package org.uiautomation.ios.grid;

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
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.IOSServerConfiguration;
import org.uiautomation.ios.IOSServerManager;
import org.uiautomation.ios.application.APPIOSApplication;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationRequest {

  private URL hubURL;
  private String nodeHost;

  private List<Map<String, Object>> capabilities = new ArrayList<Map<String, Object>>();
  private Map<String, Object> configuration = new HashMap<String, Object>();


  public RegistrationRequest(IOSServerConfiguration config, IOSServerManager driver)
      throws MalformedURLException {
    this.hubURL = new URL(config.getRegistrationURL());
    this.nodeHost = config.getHost();

    List<APPIOSApplication> apps = driver.getSupportedApplications();
    for (APPIOSApplication app : apps) {
      for (DeviceType deviceType : app.getSupportedDevices()) {
        IOSCapabilities cap = null;

        if (deviceType == DeviceType.iphone) {
          cap = IOSCapabilities.iphone(app.getApplicationName());

          if (app.getApplicationName().equals("Safari")) {
            cap.setCapability("browserName", "iPhone");
          } else {
            cap.setCapability("browserName", "IOS Simulator");
          }
        }
        else if (deviceType == DeviceType.ipad) {
          cap = IOSCapabilities.ipad(app.getApplicationName());

          if (app.getApplicationName().equals("Safari")) {
            cap.setCapability("browserName", "iPad");
          } else {
            cap.setCapability("browserName", "IOS Simulator");
          }
        }

        if (cap != null) {
          cap.setCapability("platform", "MAC");
          cap.setCapability("maxInstances", 1);

          capabilities.add(cap.getRawCapabilities());
        }
      }
    }

    configuration.put("hubHost", hubURL.getHost());
    configuration.put("hubPort", hubURL.getPort());
    configuration.put("remoteHost", "http://" + nodeHost + ":" + config.getPort());
    configuration.put("maxSession", 1);

    if (config.getProxy() != null) {
      configuration.put("proxy", config.getProxy());
    } else {
      configuration.put("proxy", "org.openqa.grid.selenium.proxy.DefaultRemoteProxy");
    }
  }

  public void registerToHub() {

    HttpClient client = new DefaultHttpClient();
    try {
      URL registration = hubURL;

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
