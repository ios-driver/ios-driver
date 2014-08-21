package org.uiautomation.ios.grid;


import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.JsonToBeanConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.uiautomation.ios.communication.Helper.extractObject;

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

      JSONArray supportedApps = status.getJSONObject("value").getJSONArray("supportedApps");

      JsonToBeanConverter convertor = new JsonToBeanConverter();
      for (int i = 0; i < supportedApps.length(); i++) {
        Capabilities cap = convertor.convert(Capabilities.class, supportedApps.getJSONObject(i));
        capabilities.add(new DesiredCapabilities(cap));
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


}
