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
package org.uiautomation.ios.server.servlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.BeanToJsonConverter;
import org.openqa.selenium.remote.ErrorCodes;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.CommandMapping;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.command.Handler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IOSServlet extends DriverBasedServlet {

  private static final Logger log = Logger.getLogger(IOSServlet.class.getName());
  private static final long serialVersionUID = -1190162363756488569L;
  private final ErrorCodes errorCodes = new ErrorCodes();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      process(request, response);
    } catch (Exception e) {
      log.warning(e.toString());
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      process(request, response);
    } catch (Exception e) {
      log.warning(e.toString());
    }
  }

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws ServletException,
             IOException {
    try {
      process(request, response);
    } catch (Exception e) {
      log.warning(e.toString());
    }
  }

  private void process(HttpServletRequest request, HttpServletResponse response) throws Exception {
    WebDriverLikeRequest req = new WebDriverLikeRequest(request);

    response.setContentType("application/json;charset=UTF-8");
    response.setCharacterEncoding("UTF-8");

    try {
      response.setStatus(200);
      Response resp = getResponse(req);

      // TODO implement the json protocol properly.
      if (req.getGenericCommand() == WebDriverLikeCommand.NEW_SESSION && resp.getStatus() == 0) {
        response.setStatus(301);
        String session = resp.getSessionId();

        if (session == null || session.isEmpty()) {
          response.setStatus(500);
        }

        String scheme = request.getScheme(); // http
        String serverName = request.getServerName(); // hostname.com
        int serverPort = request.getServerPort(); // 80
        String contextPath = request.getContextPath(); // /mywebapp

        // Reconstruct original requesting URL
        String url = scheme + "://" + serverName + ":" + serverPort + contextPath;
        response.setHeader("location", url + "/session/" + session);
      }
      // String s = toString(resp);
      BeanToJsonConverter converter = new BeanToJsonConverter();
      String s = converter.convert(resp);

      // status is also used for debugging, it's worth formatting it nice.
      if (req.getGenericCommand() == WebDriverLikeCommand.STATUS) {
        JSONObject o = new JSONObject(s);
        response.getWriter().print(o.toString(2));
      } else {
        response.getWriter().print(s);
      }
    } catch (WebDriverException e) {
      response.setStatus(500);
      response.getWriter().print(serializeException(e));
      throw new WebDriverException("Error processing response." + e.getMessage(), e);
    } finally {
      response.getWriter().close();
    }
  }

  private String toString(Response r) throws Exception {
    JSONObject o = new JSONObject();
    o.put("sessionId", r.getSessionId());
    o.put("status", r.getStatus());
    o.put("value", r.getValue().toString());
    return o.toString();
  }

  private Response getResponse(WebDriverLikeRequest request) {


    // if the application under test has crashed, the result is an error.
    if (request.hasSession()) {
      ServerSideSession session = getDriver().getSession(request.getSession());
      if (session.hasCrashed()) {
        Response response = new Response();
        response.setStatus(13);
        response.setValue(session.getCrashDetails().toString());
        return response;
      }
    }

    // otherwise,forward to the driver to get the response.
    Level level = Level.INFO;
    long startTime = System.currentTimeMillis();
    WebDriverLikeCommand wdlc = null;
    try {
      wdlc = request.getGenericCommand();
      Handler h = CommandMapping.get(wdlc).createHandler(getDriver(), request);
      return h.handleAndRunDecorators();
    } catch (WebDriverException we) {
      Response response = new Response();
      if (wdlc != null && wdlc.isSessionLess()) {
        response.setSessionId("");
      } else {
        response.setSessionId(request.getSession());
      }

      response.setStatus(errorCodes.toStatusCode(we));

      try {
        JSONObject o = serializeException(we);
        response.setValue(o);
      } catch (JSONException e) {
        level = Level.SEVERE;
        log.warning(e.toString());
      }
      return response;
    } catch (Exception e) {
      level = Level.SEVERE;
      throw new WebDriverException("bug." + e.getMessage(), e);
    } finally {
      String message = String.format("%s  in %dms", request.toString(),
                                     System.currentTimeMillis() - startTime);
      log.log(level, message);
    }
  }

  private JSONObject serializeException(Throwable e) throws JSONException {
    JSONObject res = new JSONObject();
    res.put("message", e.getMessage());
    res.put("class", e.getClass().getCanonicalName());
    res.put("screen", JSONObject.NULL);
    res.put("stackTrace", serializeStackTrace(e.getStackTrace()));
    if (e.getCause() != null) {
      res.put("cause", serializeException(e.getCause()));
    }
    return res;
  }

  private JSONArray serializeStackTrace(StackTraceElement[] els) throws JSONException {
    JSONArray stacktace = new JSONArray();
    for (StackTraceElement el : els) {
      JSONObject stckEl = new JSONObject();
      stckEl.put("fileName", el.getFileName());
      stckEl.put("className", el.getClassName());
      stckEl.put("methodName", el.getMethodName());
      stckEl.put("lineNumber", el.getLineNumber());
      stacktace.put(stckEl);
    }
    return stacktace;
  }
}
