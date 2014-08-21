/*
 * Copyright 2012-2014 eBay Software Foundation and ios-driver committers
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
import org.apache.http.message.BasicHttpRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.utils.GridHubConfiguration;
import org.openqa.selenium.net.PortProber;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.internal.HttpClientFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSServerManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;


public class IOSMutableProxyTest {

  private static org.openqa.grid.web.Hub hub;

  private static HttpClientFactory httpClientFactory = new HttpClientFactory();
  private HttpClient client = httpClientFactory.getHttpClient();
  private IOSMutableRemoteProxy remoteProxy;

  private static URL proxyApi;
  private static HttpHost host;
  private String remoteHost = "http://localhost:5555";


  @BeforeClass
  public void setup() throws Exception {
    GridHubConfiguration c = new GridHubConfiguration();
    c.getAllParams().put(org.openqa.grid.common.RegistrationRequest.TIME_OUT, 12345);
    c.setPort(PortProber.findFreePort());
    c.setHost("localhost");

    List<String>
        servlets =
        Arrays.asList(new String[]{IOSProxyStateUpdaterServlet.class.getCanonicalName()});
    c.setServlets(servlets);

    hub = new org.openqa.grid.web.Hub(c);
    Registry registry = hub.getRegistry();

    proxyApi = new URL("http://" + hub.getHost() + ":" + hub.getPort() + "/grid/admin/IOSProxyStateUpdaterServlet/");
    host = new HttpHost(hub.getHost(), hub.getPort());

    hub.start();

    org.openqa.grid.common.RegistrationRequest
        req =
        new org.openqa.grid.common.RegistrationRequest();
    Map<String, Object> capability = new HashMap<String, Object>();
    capability.put(CapabilityType.BROWSER_NAME, "Mobile safari");
    req.addDesiredCapability(capability);

    Map<String, Object> config = new HashMap<String, Object>();
    config.put(org.openqa.grid.common.RegistrationRequest.REMOTE_HOST, "http://localhost:5555");
    req.setConfiguration(config);
    remoteProxy = new IOSMutableRemoteProxy(req, registry);

    registry.add(remoteProxy);

    Map<String, Object> cap = new HashMap<String, Object>();
    cap.put(CapabilityType.BROWSER_NAME, "app1");

  }

  @Test
  public void stateIsMissing() throws IOException, JSONException {
    BasicHttpRequest r = new BasicHttpRequest("GET", proxyApi.toExternalForm() + "?id=" + remoteHost);

    HttpResponse response = client.execute(host, r);
    assertEquals(500, response.getStatusLine().getStatusCode());
    JSONObject o = extractObject(response);
    assertEquals(o.getString("msg"), IOSProxyStateUpdaterServlet.STATE_MANDATORY);
  }

  @Test
  public void idIsMissing() throws IOException, JSONException {
    BasicHttpRequest r = new BasicHttpRequest("GET", proxyApi.toExternalForm() + "?state=" + IOSServerManager.State.stopping.name());

    HttpResponse response = client.execute(host, r);
    assertEquals(500, response.getStatusLine().getStatusCode());
    JSONObject o = extractObject(response);
    assertEquals(o.getString("msg"), IOSProxyStateUpdaterServlet.ID_MANDATORY);
  }

  @Test
  public void invalidState() throws IOException, JSONException {
    String s = IOSServerManager.State.stopping.name() + "2";
    BasicHttpRequest r = new BasicHttpRequest("GET", proxyApi.toExternalForm() + "?id=" + remoteHost + "&state=" + s);

    HttpResponse response = client.execute(host, r);
    assertEquals(500, response.getStatusLine().getStatusCode());
    JSONObject o = extractObject(response);
    assertEquals(o.getString("msg"), s + IOSProxyStateUpdaterServlet.ILLEGAL_STATE);
  }

  @Test
  public void stateChanges() throws IOException, JSONException {
    IOSServerManager.State updated = IOSServerManager.State.stopping;
    assertEquals(remoteProxy.getState(), IOSMutableRemoteProxy.DEFAULT_STATE);
    BasicHttpRequest r = new BasicHttpRequest("GET", proxyApi.toExternalForm() + "?id=" + remoteHost + "&state=" + updated);

    HttpResponse response = client.execute(host, r);
    assertEquals(200, response.getStatusLine().getStatusCode());
    assertEquals(remoteProxy.getState(), updated);
  }


  private JSONObject extractObject(HttpResponse resp) throws IOException, JSONException {
    BufferedReader rd = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
    StringBuilder s = new StringBuilder();
    String line;
    while ((line = rd.readLine()) != null) {
      s.append(line);
    }
    rd.close();

    return new JSONObject(s.toString());
  }

  private IOSMutableRemoteProxy create(Registry registry) {
    org.openqa.grid.common.RegistrationRequest req = org.openqa.grid.common.RegistrationRequest
        .build("-role", "webdriver", "-host", "localhost", "-" + RegistrationRequest.HUB_HOST, "localhost");

    req.getCapabilities().clear();

    DesiredCapabilities capability = new DesiredCapabilities();
    capability.setBrowserName("Mobile Safari");
    req.addDesiredCapability(capability);

    Map<String, Object> config = new HashMap<String, Object>();
    config.put(RegistrationRequest.REMOTE_HOST, remoteHost);
    req.setConfiguration(config);

    IOSMutableRemoteProxy remoteProxy = new IOSMutableRemoteProxy(req, registry);
    remoteProxy.setupTimeoutListener();
    return remoteProxy;

  }
}
