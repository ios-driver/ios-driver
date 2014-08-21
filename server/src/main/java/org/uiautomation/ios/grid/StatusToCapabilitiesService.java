package org.uiautomation.ios.grid;


import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

public class StatusToCapabilitiesService {

  private final URL url;

  public StatusToCapabilitiesService(URL nodeStatusURL) {
    this.url = nodeStatusURL;
  }


  public List<DesiredCapabilities> getNodeCapabilities() {
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


    String statusURL = this.url.toExternalForm();

    BasicHttpRequest r = new BasicHttpRequest("GET", statusURL);

    HttpResponse response = client.execute(new HttpHost(url.getHost(), url.getPort()), r);
    return extractObject(response);
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
}
