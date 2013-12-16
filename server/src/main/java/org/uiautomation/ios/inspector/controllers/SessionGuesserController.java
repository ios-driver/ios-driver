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
package org.uiautomation.ios.inspector.controllers;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.communication.Helper;
import org.uiautomation.ios.communication.HttpClientFactory;
import org.uiautomation.ios.inspector.model.Cache;
import org.uiautomation.ios.inspector.views.RedirectView;
import org.uiautomation.ios.inspector.views.View;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

public class SessionGuesserController implements IDECommandController {

  private final Cache cache;

  public SessionGuesserController(Cache cache) {
    this.cache = cache;
  }

  @Override
  public boolean canHandle(String pathInfo) {
   // allow either "http://localhost/inspector" or "http://localhost/inspector/" 
    return pathInfo == null || pathInfo.equals("/");
  }

  @Override
  public View handle(HttpServletRequest req) throws Exception {
    Session session = getSession();
    // allow either "http://localhost/inspector" or "http://localhost/inspector/" 
    String base = req.getPathInfo() == null? "inspector/session/" : "session/";
    return new RedirectView(base + session.getSessionId() + "/home");
  }

  private Session getSession() throws Exception {
    HttpClient client = HttpClientFactory.getClient();
    String url = cache.getEndPoint() + "/status";
    URL u = new URL(url);
    BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("GET", url);

    HttpHost h = new HttpHost(u.getHost(), u.getPort());
    HttpResponse response = client.execute(h, r);

    JSONObject o = Helper.extractObject(response);
    String id = o.optString("sessionId");
    if (id == null) {
      throw new WebDriverException("cannot guess the sessionId for the IDE to use.");
    }
    return new Session(id);
  }
}
