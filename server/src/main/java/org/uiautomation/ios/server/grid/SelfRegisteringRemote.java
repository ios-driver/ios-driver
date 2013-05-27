/*
 * Copyright 2013 ios-driver committers.
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
import org.apache.http.message.BasicHttpRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.internal.HttpClientFactory;
import org.uiautomation.ios.server.IOSServerConfiguration;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.grid.exception.GridException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

public class SelfRegisteringRemote {

  private final HttpClientFactory httpClientFactory;
  private IOSServerConfiguration nodeConfig;
  private IOSServerManager driver;
  private static final Logger log = Logger.getLogger(SelfRegisteringRemote.class.getName());

  public SelfRegisteringRemote(IOSServerConfiguration config, IOSServerManager driver) {
    this.nodeConfig = config;
    this.driver = driver;
    this.httpClientFactory = new HttpClientFactory();
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

  public void startRegistrationProcess() {
    new Thread(new Runnable() { // Thread safety reviewed

      public void run() {
        boolean first = true;
        while (true) {
          try {
            boolean checkForPresence = true;
            if (first) {
              first = false;
              checkForPresence = false;
            }
            registerToHub(checkForPresence);
          } catch (GridException e) {
            log.info("Problem registering the node : " + e.getMessage());
          } catch (MalformedURLException e) {
            e.printStackTrace();
          }
          try {
            Thread.sleep(5000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
  }

  private void registerToHub(boolean checkPresenceFirst) throws MalformedURLException {
    // check for presence :
    boolean ok = checkPresenceFirst == true ? !isAlreadyRegistered() : true;

    if (ok) {
      RegistrationRequest
          request =
          new RegistrationRequest(nodeConfig, driver);
      request.registerToHub();
    }

  }

  private boolean isAlreadyRegistered() {
    HttpClient client = httpClientFactory.getHttpClient();
    try {
      URL hubRegistrationURL = new URL(nodeConfig.getRegistrationURL());
      URL api = new URL("http://" + hubRegistrationURL.getHost() + ":" + hubRegistrationURL.getPort() + "/grid/api/proxy");
      HttpHost host = new HttpHost(api.getHost(), api.getPort());

      String id = "http://" + nodeConfig.getHost() + ":" + nodeConfig.getPort();
      BasicHttpRequest r = new BasicHttpRequest("GET", api.toExternalForm() + "?id=" + id);

      HttpResponse response = client.execute(host, r);
      if (response.getStatusLine().getStatusCode() != 200) {
        throw new GridException("hub down or not responding. Reason : " + response.getStatusLine().getReasonPhrase());
      }
      JSONObject o = extractObject(response);
      return (Boolean) o.get("success");
    } catch (Exception e) {
      throw new GridException("Problem registering with hub", e);
    }
  }
}
