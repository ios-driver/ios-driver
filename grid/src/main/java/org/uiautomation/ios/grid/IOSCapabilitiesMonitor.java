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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.TestSlot;
import org.openqa.grid.internal.utils.SelfRegisteringRemote;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class IOSCapabilitiesMonitor implements Runnable {

  private boolean active;
  private IOSRemoteProxy proxy;
  private URL node;
  private List<DesiredCapabilities> registeredCapabilities;
  private int noResponse;
  private static final Logger log = Logger.getLogger(IOSCapabilitiesMonitor.class.getName());

  public IOSCapabilitiesMonitor(IOSRemoteProxy proxy) {
    this.proxy = proxy;
    this.node = proxy.getRemoteHost();
    registeredCapabilities = proxy.getOriginalRegistrationRequest().getCapabilities();
    active = true;
    this.noResponse = 0;
  }

  private static JSONObject extractObject(HttpResponse resp) throws IOException, JSONException {
    BufferedReader rd = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
    StringBuilder s = new StringBuilder();
    String line;
    while ((line = rd.readLine()) != null) {
      s.append(line);
    }
    rd.close();
    return new JSONObject(s.toString());
  }

  @Override
  public void run() {
    while (active) {
      try {
        RegistrationRequest latest = createRegistrationRequest();
        if (latest == null) {
          noResponse++;
          proxy.setAvailable(false);
          if (noResponse >= 30) {
            active = false;
            removeNode();
          }
          continue;
        }
        noResponse = 0;
        proxy.setAvailable(true);
        List<DesiredCapabilities> latestCapabilities = latest.getCapabilities();
        if (!registeredCapabilities.toString().equals(latestCapabilities.toString())) {
          log.info("New capabilities registered on " + node.toString() + ". Updating...");
          updateCapabilities(latest);
        }
        Thread.sleep(2000);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private RegistrationRequest createRegistrationRequest() throws Exception {
    RegistrationRequest registrationRequest = new RegistrationRequest();

    List<DesiredCapabilities> capabilities = getNodeCapabilities();
    if (capabilities == null) {
      return null;
    }
    for (DesiredCapabilities cap : capabilities) {
      registrationRequest.addDesiredCapability(cap);
    }

    registrationRequest.getConfiguration().put(RegistrationRequest.AUTO_REGISTER, true);
    registrationRequest.getConfiguration().put(RegistrationRequest.PROXY_CLASS,
        IOSRemoteProxy.class.getCanonicalName());

    registrationRequest.getConfiguration()
        .put(RegistrationRequest.HUB_HOST, proxy.getRegistry().getHub().getHost());
    registrationRequest.getConfiguration()
        .put(RegistrationRequest.HUB_PORT, proxy.getRegistry().getHub().getPort());
    registrationRequest.getConfiguration()
        .put(RegistrationRequest.REMOTE_HOST, "http://" + node.getHost() + ":" + node.getPort());
    registrationRequest.getConfiguration().put(RegistrationRequest.MAX_SESSION, 1);

    return registrationRequest;
  }

  private List<DesiredCapabilities> getNodeCapabilities() {
    try {
      List<DesiredCapabilities> capabilities = new ArrayList<DesiredCapabilities>();

      JSONObject status = getNodeStatusJson();
      if (status == null) {
        return null;
      }

      String ios = status.getJSONObject("value").getJSONObject("ios").optString("simulatorVersion");
      JSONArray supportedApps = status.getJSONObject("value").getJSONArray("supportedApps");

      for (int i = 0; i < supportedApps.length(); i++) {
        Map<String, Object> capability = new HashMap<String, Object>();
        capability.put("maxInstances", "1");
        if (ios.isEmpty()) {
          capability.put("ios", "5.1");
          capability.put("browserName", "IOS Device");
          capability.put(IOSCapabilities.DEVICE, DeviceType.iphone);
        } else {
          capability.put("ios", ios);
          capability.put("browserName", "IOS Simulator");
        }
        JSONObject app = supportedApps.getJSONObject(i);
        for (String key : JSONObject.getNames(app)) {
          if ("locales".equals(key)) {
            JSONArray loc = app.getJSONArray(key);
            List<String> locales = new ArrayList<String>();
            for (int j = 0; j < loc.length(); j++) {
              locales.add(loc.getString(j));
            }
            capability.put("locales", locales);
          } else {
            Object o = app.get(key);
            capability.put(key, o);
          }
        }
        capabilities.add(new DesiredCapabilities(capability));
      }
      return capabilities;
    } catch (Exception e) {
      return null;
    }
  }

  private JSONObject getNodeStatusJson() throws IOException, JSONException {
    HttpClient client = new DefaultHttpClient();

    String url = "http://" + node.getHost() + ":" + node.getPort() + "/wd/hub/status";

    BasicHttpRequest r = new BasicHttpRequest("GET", url);

    HttpResponse response = client.execute(new HttpHost(node.getHost(), node.getPort()), r);
    return extractObject(response);
  }

  private void removeNode() {
    //proxy.teardown();
    proxy.unregister();
  }

  private void updateCapabilities(RegistrationRequest registrationRequest) {
    try {
      proxy.setRestarting(true);
      while (proxyBusy()) {
        System.out.println("Node busy... waiting...");
        Thread.sleep(1000);
      }
      active = false;
      registerNode(registrationRequest);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private boolean proxyBusy() {
    List<TestSlot> slots = proxy.getTestSlots();
    for (TestSlot slot : slots) {
      if (slot.getSession() != null) {
        return true;
      }
    }
    return false;
  }

  private void registerNode(RegistrationRequest registrationRequest) {
    SelfRegisteringRemote remote = new SelfRegisteringRemote(registrationRequest);
    remote.startRegistrationProcess();
  }
}