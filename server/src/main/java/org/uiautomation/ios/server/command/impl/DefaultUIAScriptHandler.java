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
package org.uiautomation.ios.server.command.impl;

import java.io.File;

import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.UIAApplication;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAElementArray;
import org.uiautomation.ios.UIAModels.UIAKeyboard;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.UIAModels.UIATarget;
import org.uiautomation.ios.UIAModels.UIAWindow;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.CommandMapping;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.instruments.SessionsManager;

/**
 * execute the command on instruments, and returns a String.
 * 
 */
public class DefaultUIAScriptHandler extends UIAScriptHandler {



  public DefaultUIAScriptHandler(SessionsManager instruments, WebDriverLikeRequest request) {
    super(instruments, request);
    setJS(computeJS(request.getPayload()));
  }



  private String computeJS(JSONObject payload) {
    Class<?> returnType = getRequest().getGenericCommand().returnType();
    if (returnType == String.class || returnType == UIARect.class || returnType == Integer.class
        || returnType == Boolean.class) {
      return jsForString(payload);
    } else if (isAUIASimpleObject(returnType)) {
      return jsForRemoteObject(payload);
    } else if (returnType == Void.class) {
      return jsForVoid(payload);
    } else {
      return null;
    }
  }


  public boolean isAUIASimpleObject(Class<?> type) {
    if (type == UIAApplication.class) return true;
    if (type == UIATarget.class) return true;
    if (type == UIAWindow.class) return true;
    if (type == UIAElement.class) return true;
    if (type == UIAElementArray.class) return true;
    if (type == UIAKeyboard.class)return true; 
    return false;
  }

  private IOSApplication getAUT() {
    return getSessionsManager().getCurrentApplication();
  }

  // TODO freynaud extract.
  private static final String stringTemplate =
      "var target = UIAutomation.cache.get(:reference);var myStringResult = target:jsMethod ;UIAutomation.createJSONResponse(':sessionId',0,myStringResult)";

  private static final String objectTemplate =
      "var parent = UIAutomation.cache.get(:reference);var myObjectResult = parent:jsMethod;var k=UIAutomation.cache.store(myObjectResult);UIAutomation.createJSONResponse(':sessionId',0,myObjectResult)";

  private static final String voidTemplate =
      "var parent = UIAutomation.cache.get(:reference);parent:jsMethod;UIAutomation.createJSONResponse(':sessionId',0,'')";

  private String jsForRemoteObject(JSONObject payload) {
    WebDriverLikeCommand command = getRequest().getGenericCommand();
    CommandMapping mapping = CommandMapping.get(command);
    String method = mapping.jsMethod(payload, getAUT());

    String js =
        objectTemplate.replace(":jsMethod", method)
            .replace(":sessionId", getSessionsManager().getCurrentSessionId())
            .replace(":reference", getRequest().getVariableValue(":reference"));


    return js;
  }



  private String jsForString(JSONObject payload) {
    WebDriverLikeCommand command = getRequest().getGenericCommand();
    CommandMapping mapping = CommandMapping.get(command);
    String method = mapping.jsMethod(payload, getAUT());

    String js =
        stringTemplate.replace(":jsMethod", method)
            .replace(":sessionId", getSessionsManager().getCurrentSessionId())
            .replace(":reference", getRequest().getVariableValue(":reference"));


    if (js.contains(":lastScreenshotPath")) {
      String path = getSessionsManager().getCurrentSessionOutputFolder() + "/Run 1/current.png";
      new File(path).delete();
      js = js.replace(":lastScreenshotPath", "'" + path + "'");
    }

    return js;
  }

  private String jsForVoid(JSONObject payload) {
    WebDriverLikeCommand command = getRequest().getGenericCommand();
    CommandMapping mapping = CommandMapping.get(command);
    String method = mapping.jsMethod(payload, getAUT());

    String js =
        voidTemplate.replace(":jsMethod", method)
            .replace(":sessionId", getSessionsManager().getCurrentSessionId())
            .replace(":reference", getRequest().getVariableValue(":reference"));

    return js;
  }

}
