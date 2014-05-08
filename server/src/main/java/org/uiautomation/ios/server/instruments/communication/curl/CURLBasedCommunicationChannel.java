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
package org.uiautomation.ios.server.instruments.communication.curl;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.server.InstrumentsBackedNativeIOSDriver;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.application.LanguageDictionary;
import org.uiautomation.ios.server.command.UIAScriptRequest;
import org.uiautomation.ios.server.command.UIAScriptResponse;
import org.uiautomation.ios.server.instruments.communication.BaseCommunicationChannel;
import org.uiautomation.ios.server.instruments.communication.CommunicationChannel;
import org.uiautomation.ios.server.servlet.DriverBasedServlet;
import org.uiautomation.ios.utils.ApplicationCrashDetails;

import java.io.IOException;
import java.io.StringWriter;
import java.text.Normalizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CURLBasedCommunicationChannel extends BaseCommunicationChannel {

  private final BlockingQueue<UIAScriptRequest> requestQueue = new ArrayBlockingQueue<>(1);

  public CURLBasedCommunicationChannel(String sessionId) {
    super(sessionId);
  }

  public UIAScriptResponse executeCommand(UIAScriptRequest request) {
    handleLastCommand(request);
    requestQueue.add(request);
    return waitForResponse();
  }

  @Override
  public void stop() {

  }

  private void addResponse(UIAScriptResponse r) {
    setNextResponse(r);
  }

  private UIAScriptRequest getNextCommand() throws InterruptedException {
    UIAScriptRequest res = requestQueue.take();
    return res;
  }

  public static class UIAScriptServlet extends DriverBasedServlet {

    private static final Logger log = Logger.getLogger(UIAScriptServlet.class.getName());
    private static final long serialVersionUID = 41227429706998662L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
      try {
        log("sendNextCommand");
        sendNextCommand(request, response);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
      try {
        getResponse(request, response);
      } catch (Exception e) {
        log.warning("CURL communication between server and instruments crashed "+e.getMessage());
      }
    }

    private void sendNextCommand(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
      UIAScriptRequest nextCommand = getCommunicationChannel(request).getNextCommand();
      String script = nextCommand.getScript();

      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");
      response.setStatus(200);
      response.getWriter().print(script);
      response.getWriter().close();
    }

    private void getResponse(HttpServletRequest request, HttpServletResponse response)
        throws Exception {

      log.fine("got a request on the script thread.");
      if (request.getInputStream() != null) {
        StringWriter writer = new StringWriter();
        IOUtils.copy(request.getInputStream(), writer, "UTF-8");
        String json = writer.toString();
        json = Normalizer.normalize(json, LanguageDictionary.form);
        UIAScriptResponse r = new UIAScriptResponse(json);
        log.fine("content : " + r);

        if (r.isFirstResponse()) {
          log.fine("got first response");
          Response resp = r.getResponse();
          ServerSideSession session = getDriver().getSession(resp.getSessionId());
          session.setCapabilityCachedResponse(resp);
          //GetCapabilitiesNHandler.setCachedResponse(resp);
          InstrumentsBackedNativeIOSDriver
              nativeDriver =
              getDriver().getSession(resp.getSessionId()).getDualDriver().getNativeDriver();
          nativeDriver.communication().registerUIAScript();
        } else {
          if (isCrashed(request)) {
            r.getResponse().setStatus(13);
            r.getResponse().setValue(getCrashDetails(request).toString());
          }
          getCommunicationChannel(request).addResponse(r);
        }
        UIAScriptRequest nextCommand = getCommunicationChannel(request).getNextCommand();
        String script = nextCommand.getScript();

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(200);
        response.getWriter().print(script);
        response.getWriter().close();
      }
    }

    private boolean isCrashed(HttpServletRequest request) {
      String opaqueKey = request.getParameter("sessionId");
      return getDriver().getSession(opaqueKey).hasCrashed();
    }

    private ApplicationCrashDetails getCrashDetails(HttpServletRequest request) {
      String opaqueKey = request.getParameter("sessionId");
      return getDriver().getSession(opaqueKey).getCrashDetails();
    }

    private CURLBasedCommunicationChannel getCommunicationChannel(HttpServletRequest request)
        throws Exception {
      String opaqueKey = request.getParameter("sessionId");
      InstrumentsBackedNativeIOSDriver
          nativeDriver =
          getDriver().getSession(opaqueKey).getDualDriver().getNativeDriver();

      CommunicationChannel channel = nativeDriver.communication();
      if (channel instanceof CURLBasedCommunicationChannel) {
        return (CURLBasedCommunicationChannel) channel;
      } else {
        throw new RuntimeException("Bug.Using a servlet to communicate with instruments only "
                                   + "makes sense in the case of a CURL based communication.For "
                                   + channel.getClass() + " the servlet shouldn't be called.");
      }
    }
  }
}
