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
package org.uiautomation.ios.server.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.uiautomation.ios.communication.FailedWebDriverLikeResponse;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.CommandMapping;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.command.Handler;
import org.uiautomation.ios.server.command.PostHandleDecorator;
import org.uiautomation.ios.server.command.PreHandleDecorator;

public class IOSServlet extends UIAScriptProxyBasedServlet {


  private static final long serialVersionUID = -1190162363756488569L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      process(request, response);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      process(request, response);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      process(request, response);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void process(HttpServletRequest request, HttpServletResponse response) throws Exception {
    WebDriverLikeRequest req = getRequest(request);
    WebDriverLikeResponse resp = getResponse(req);

    response.setContentType("application/json;charset=UTF-8");
    response.setCharacterEncoding("UTF-8");

    // TODO implement the json protocol properly.
    if (req.getGenericCommand() == WebDriverLikeCommand.NEW_SESSION) {
      response.setStatus(301);
      String session = resp.getSessionId();
      
      String scheme = request.getScheme();             // http
      String serverName = request.getServerName();     // hostname.com
      int serverPort = request.getServerPort();        // 80
      String contextPath = request.getContextPath();   // /mywebapp
      
      // Reconstruct original requesting URL
      String url = scheme+"://"+serverName+":"+serverPort+contextPath;
      response.setHeader("location", url+"/session/" + session);
    } else {
      response.setStatus(200);
    }
    response.getWriter().print(resp.stringify());
    response.getWriter().close();

  }

  private WebDriverLikeRequest getRequest(HttpServletRequest request) throws Exception {

    String method = request.getMethod();
    String path = request.getPathInfo();
    String json = null;
    if (request.getInputStream() != null) {
      StringWriter w = new StringWriter();
      IOUtils.copy(request.getInputStream(), w, "UTF-8");
      json = w.toString();
    }
    JSONObject o = new JSONObject();
    if (json != null && !json.isEmpty()) {
      o = new JSONObject(json);
    }

    WebDriverLikeRequest orig = new WebDriverLikeRequest(method, path, o);
    return orig;

  }

  private WebDriverLikeResponse getResponse(WebDriverLikeRequest request) {
    try {
      WebDriverLikeCommand wdlc = request.getGenericCommand();
      List<PreHandleDecorator> pres=null;
      List<PostHandleDecorator> posts=null;
      Handler h = CommandMapping.get(wdlc).createHandler(getSessionsManager(), request);
      return h.handleAndRunDecorators();
    } catch (Exception e) {
      e.printStackTrace();
      return new FailedWebDriverLikeResponse(getSessionsManager().getCurrentSessionId(), e);

    }

  }
}
