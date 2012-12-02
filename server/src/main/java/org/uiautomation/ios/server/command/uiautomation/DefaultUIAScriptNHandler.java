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
package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.UIAAlert;
import org.uiautomation.ios.UIAModels.UIAApplication;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAElementArray;
import org.uiautomation.ios.UIAModels.UIAKeyboard;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.UIAModels.UIATarget;
import org.uiautomation.ios.UIAModels.UIAWindow;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.CommandMapping;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.command.UIAScriptHandler;

/**
 * execute the command on instruments, and returns the result cast based on the expected result.
 * 
 */
public class DefaultUIAScriptNHandler extends UIAScriptHandler {

  // TODO freynaud extract?
  private static final String stringTemplate = 
      "var parent = UIAutomation.cache.get(:reference);" +
      "var myStringResult = parent:jsMethod ;" +
      "UIAutomation.createJSONResponse(':sessionId',0,myStringResult)";

  private static final String objectTemplate =
      "var parent = UIAutomation.cache.get(:reference);" +
      "var myObjectResult = parent:jsMethod;" +
      "var k=UIAutomation.cache.store(myObjectResult);" +
      "UIAutomation.createJSONResponse(':sessionId',0,myObjectResult)";

  private static final String voidTemplate =
      "var parent = UIAutomation.cache.get(:reference);" +
      "parent:jsMethod;UIAutomation.createJSONResponse(':sessionId',0,'')";


  public DefaultUIAScriptNHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
    setJS(computeJS());
  }

  private String computeJS() {
    Class<?> returnType = getRequest().getGenericCommand().returnType();
    String template = findTemplate(returnType);
    return getFinalJS(template);
  }

  private String findTemplate(Class<?> returnType) {
    if (returnType == String.class || returnType == UIARect.class || returnType == Integer.class
        || returnType == Boolean.class || returnType == JSONObject.class) {
      return stringTemplate;
    } else if (isAUIASimpleObject(returnType)) {
      return objectTemplate;
    } else if (returnType == Void.class) {
      return voidTemplate;
    } else {
      return null;
    }
  }


  public boolean isAUIASimpleObject(Class<?> type) {
    if (type == UIAApplication.class) return true;
    if (type == UIATarget.class) return true;
    if (type == UIAWindow.class) return true;
    if (type == UIAButton.class) return true;
    if (type == UIAElement.class) return true;
    if (type == UIAElementArray.class) return true;
    if (type == UIAKeyboard.class) return true;
    if (type == UIAAlert.class) return true;
    return false;
  }

  private IOSApplication getAUT() {
    ServerSideSession session = getDriver().getSession(getRequest().getSession());
    return session.getApplication();
  }


  private String getFinalJS(String template) {
    WebDriverLikeCommand command = getRequest().getGenericCommand();
    CommandMapping mapping = CommandMapping.get(command);
    String method = mapping.jsMethod(getRequest().getPayload(), getAUT());

    String js =
        template.replace(":jsMethod", method)
        .replace(":sessionId", getRequest().getSession());
    if (getRequest().hasVariable(":reference")) {
      js = js.replace(":reference", getRequest().getVariableValue(":reference"));
    }

    return js;
  }
  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }


}
