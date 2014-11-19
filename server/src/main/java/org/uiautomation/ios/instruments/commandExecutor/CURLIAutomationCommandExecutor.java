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
package org.uiautomation.ios.instruments.commandExecutor;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.application.LanguageDictionary;
import org.uiautomation.ios.command.UIAScriptRequest;
import org.uiautomation.ios.command.UIAScriptResponse;
import org.uiautomation.ios.drivers.RemoteIOSNativeDriver;
import org.uiautomation.ios.servlet.DriverBasedServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CURLIAutomationCommandExecutor extends BaseUIAutomationCommandExecutor {

  private final BlockingQueue<UIAScriptRequest> requestQueue = new ArrayBlockingQueue<>(1);
  private final String COM_WITH_INSTRUMENTS_DOWN = "Cannot process UIARequest, instruments communication has stopped";


  public CURLIAutomationCommandExecutor(ServerSideSession session) {
    super(session);
  }

  public UIAScriptResponse executeCommand(UIAScriptRequest request) {
    handleLastCommand(request);
    if (isReady()) {
      requestQueue.add(request);
    } else {
      throw new WebDriverException(COM_WITH_INSTRUMENTS_DOWN);
    }
    return waitForResponse();
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
    private final int UUID_LENGTH = 36;
    private final String PARTIAL_IDENTIFIER = "b94b2b80-4330-11e4-916c-0800200c9a66-PARTIAL";
    private final String FINAL_PART_IDENTIFIER = "b94b2b80-4330-11e4-916c-0800200c9a66-FINAL";
    private HashMap jsonMap = new HashMap();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
      try {
        log("sendNextCommand");
        sendNextCommand(request, response);
      } catch (Exception e) {
        log.log(Level.SEVERE,"error sending next command",e);
      }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
      try {
        getResponse(request, response);
      } catch (Exception e) {
        log.warning("CURL commandExecutor between server and instruments crashed " + e.getMessage());
      }
    }

    private void sendNextCommand(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
      log.fine("sending command");
      UIAScriptRequest nextCommand = getCommunicationChannel(request).getNextCommand();
      log.fine("got next command" + nextCommand);
      String script = nextCommand.getScript();

      log.fine("sending request to script:" + script);
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
        String json = null;
        String sessionUUID = null;
        String jsonConcatenated = "";
        if (writer.toString().endsWith(PARTIAL_IDENTIFIER)){
            sessionUUID = writer.toString().substring(0, UUID_LENGTH);
            if (jsonMap.get(sessionUUID) != null){
                jsonConcatenated = (String)jsonMap.get(sessionUUID);
            }
            jsonConcatenated =  jsonConcatenated.concat(writer.toString().substring(UUID_LENGTH, writer.toString().length() - PARTIAL_IDENTIFIER.length()));
            jsonMap.put(sessionUUID, jsonConcatenated);
            return;
        }else if (writer.toString().endsWith(FINAL_PART_IDENTIFIER)){
            sessionUUID = writer.toString().substring(0, UUID_LENGTH);
            json =  ((String)jsonMap.get(sessionUUID)).concat(writer.toString().substring(UUID_LENGTH, writer.toString().length() - FINAL_PART_IDENTIFIER.length()));
            jsonMap.remove(sessionUUID);
        }else{
            json = writer.toString();
        }
        json = Normalizer.normalize(json, LanguageDictionary.form);
        UIAScriptResponse r = new UIAScriptResponse(json);
        log.fine("content : " + r);

        if (r.isFirstResponse()) {
          log.fine("got first response");
          Response resp = r.getResponse();
          ServerSideSession session = getDriver().getSession(resp.getSessionId());
          session.setCapabilityCachedResponse(resp);
          //GetCapabilitiesNHandler.setCachedResponse(resp);
          RemoteIOSNativeDriver
              nativeDriver =
              getDriver().getSession(resp.getSessionId()).getDualDriver().getNativeDriver();
          nativeDriver.communication().registerUIAScript();
        } else {
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

    private CURLIAutomationCommandExecutor getCommunicationChannel(HttpServletRequest request)
        throws Exception {
      String opaqueKey = request.getParameter("sessionId");
      RemoteIOSNativeDriver
          nativeDriver =
          getDriver().getSession(opaqueKey).getDualDriver().getNativeDriver();

      UIAutomationCommandExecutor channel = nativeDriver.communication();
      if (channel instanceof CURLIAutomationCommandExecutor) {
        return (CURLIAutomationCommandExecutor) channel;
      } else {
        throw new RuntimeException("Bug.Using a servlet to communicate with instruments only "
                                   + "makes sense in the case of a CURL based commandExecutor.For "
                                   + channel.getClass() + " the servlet shouldn't be called.");
      }
    }
  }
}
