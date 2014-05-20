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
package org.uiautomation.ios.command.uiautomation;

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
import org.uiautomation.ios.CommandMapping;
import org.uiautomation.ios.IOSServerManager;
import org.uiautomation.ios.command.UIAScriptHandler;
import org.uiautomation.ios.utils.JSTemplate;

/**
 * execute the command on instruments, and returns the result cast based on the expected result.
 */
public class DefaultUIAScriptNHandler extends UIAScriptHandler {

  // TODO freynaud extract?
  private static final JSTemplate stringTemplate = buildJSTemplate(
      "var parent = UIAutomation.cache.get(%:reference$s);" +
      "var myStringResult = parent%:jsMethod$s;" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,myStringResult)");
  private static final JSTemplate objectTemplate = buildJSTemplate(
      "var parent = UIAutomation.cache.get(%:reference$s);" +
      "var myObjectResult = parent%:jsMethod$s;" +
          "var k=UIAutomation.cache.store(myObjectResult);" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,myObjectResult)");
  private static final JSTemplate voidTemplate = buildJSTemplate(
      "var parent = UIAutomation.cache.get(%:reference$s);" +
      "parent%:jsMethod$s;UIAutomation.createJSONResponse('%:sessionId$s',0,'')");

  private static JSTemplate buildJSTemplate(String template) {
    return new JSTemplate(template, "sessionId", "reference", "jsMethod");
  }

  public DefaultUIAScriptNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
    setJS(computeJS());
  }

  private String computeJS() {
    Class<?> returnType = getRequest().getGenericCommand().returnType();
    JSTemplate template = findTemplate(returnType);
    return getFinalJS(template);
  }

  private JSTemplate findTemplate(Class<?> returnType) {
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
    if (type == UIAApplication.class) {
      return true;
    }
    if (type == UIATarget.class) {
      return true;
    }
    if (type == UIAWindow.class) {
      return true;
    }
    if (type == UIAButton.class) {
      return true;
    }
    if (type == UIAElement.class) {
      return true;
    }
    if (type == UIAElementArray.class) {
      return true;
    }
    if (type == UIAKeyboard.class) {
      return true;
    }
    if (type == UIAAlert.class) {
      return true;
    }
    return false;
  }

  private String getFinalJS(JSTemplate template) {
    String reference = getRequest().hasVariable(":reference") ? getRequest().getVariableValue(":reference")
        : ":reference";
    WebDriverLikeCommand command = getRequest().getGenericCommand();
    CommandMapping mapping = CommandMapping.get(command);
    String jsMethod = mapping.jsMethod(getRequest().getPayload());
    String sessionId = getRequest().getSession();

    return template.generate(sessionId, reference, jsMethod);
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
