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

import java.io.IOException;
import java.io.StringWriter;
import java.text.Normalizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.uiautomation.ios.server.UIAScriptRequest;
import org.uiautomation.ios.server.UIAScriptResponse;
import org.uiautomation.ios.server.application.LanguageDictionary;
import org.uiautomation.ios.server.instruments.CommunicationChannel;


public class UIAScriptServlet extends DriverBasedServlet {

  private static final long serialVersionUID = 41227429706998662L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
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
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void sendNextCommand(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    UIAScriptRequest nextCommand = communication(request).getNextCommand();
    String script = nextCommand.getScript();

    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(200);
    response.getWriter().print(script);
    response.getWriter().close();
  }

  private void getResponse(HttpServletRequest request, HttpServletResponse response)
      throws Exception, JSONException {

    if (request.getInputStream() != null) {
      StringWriter writer = new StringWriter();
      IOUtils.copy(request.getInputStream(), writer, "UTF-8");
      String json = writer.toString();
      json = Normalizer.normalize(json, LanguageDictionary.norme);
      UIAScriptResponse r = new UIAScriptResponse(json);
      communication(request).setNextResponse(r);
    }



  }

  private CommunicationChannel communication(HttpServletRequest request) throws Exception {
    String opaqueKey = request.getParameter("sessionId");
    return getDriver().getSession(opaqueKey).communication();
  }

}
