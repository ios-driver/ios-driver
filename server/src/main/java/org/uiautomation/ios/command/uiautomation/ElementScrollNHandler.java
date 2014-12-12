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

import com.google.common.collect.ObjectArrays;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.IOSServerManager;
import org.uiautomation.ios.command.UIAScriptHandler;
import org.uiautomation.ios.utils.JSTemplate;

public class ElementScrollNHandler extends UIAScriptHandler {

  private static final JSTemplate scrollUpTemplate = buildJSTemplate(
      "var element = UIAutomation.cache.get(%:reference$s);" +
      "element.scrollUp();" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,'', true)");
  private static final JSTemplate scrollDownTemplate = buildJSTemplate(
      "var element = UIAutomation.cache.get(%:reference$s);" +
      "element.scrollDown();" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,'', true)");
  private static final JSTemplate scrollLeftTemplate = buildJSTemplate(
      "var element = UIAutomation.cache.get(%:reference$s);" +
      "element.scrollLeft();" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,'', true)");
  private static final JSTemplate scrollRightTemplate = buildJSTemplate(
      "var element = UIAutomation.cache.get(%:reference$s);" +
      "element.scrollRight();" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,'', true)");
  private static final JSTemplate scrollToNameTemplate = buildJSTemplate(
      "var element = UIAutomation.cache.get(%:reference$s);" +
      "element.scrollToElementWithName('%:name$s');" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,'', true)",
      "name");
  private static final JSTemplate scrollToPredicateTemplate = buildJSTemplate(
      "var element = UIAutomation.cache.get(%:reference$s);" +
      "element.scrollToElementWithPredicate(\"%:predicateString$s\");" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,'', true)",
      "predicateString");
  private static final JSTemplate scrollToVisibleTemplate = buildJSTemplate(
      "var element = UIAutomation.cache.get(%:reference$s);" +
      "element.scrollToVisible();" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,'', true)");

  private static JSTemplate buildJSTemplate(String template, String... extraArgs) {
    String[] args = {"sessionId", "reference"};
    return new JSTemplate(template, ObjectArrays.concat(args, extraArgs, String.class));
  }

  public ElementScrollNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);

    String reference = request.getVariableValue(":reference");
    String sessionId = request.getSession();
    final String js;
    JSONObject payload = request.getPayload();
    if (payload.has("direction")) {
      JSTemplate template;
      switch (payload.optString("direction")) {
        case "up": template = scrollUpTemplate; break;
        case "down": template = scrollDownTemplate; break;
        case "left": template = scrollLeftTemplate; break;
        case "right": template = scrollRightTemplate; break;
        default: throw new WebDriverException("Invalid value for scrolling direction");
      }
      js = template.generate(sessionId, reference);
    } else if (payload.has("name")) {
      String name = payload.optString("name");
      js = scrollToNameTemplate.generate(sessionId, reference, name);
    } else if (payload.has("predicateString")) {
      String predicateString = payload.optString("predicateString");
      js = scrollToPredicateTemplate.generate(sessionId, reference, predicateString);
    } else if (payload.has("toVisible")) {
      js = scrollToVisibleTemplate.generate(sessionId, reference);
    } else {
      throw new WebDriverException("Unrecognised payload for ELEMENT_SCROLL");
    }
    setJS(js);
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
