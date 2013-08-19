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
package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class ElementScrollNHandler extends UIAScriptHandler {

  private static final String scrollUpTemplate =
          "var element = UIAutomation.cache.get(:reference);" +
                  "element.scrollUp();" +
                  "UIAutomation.createJSONResponse(':sessionId',0,'')";


  private static final String scrollDownTemplate =
          "var element = UIAutomation.cache.get(:reference);" +
                  "element.scrollDown();" +
                  "UIAutomation.createJSONResponse(':sessionId',0,'')";

  private static final String scrollLeftTemplate =
          "var element = UIAutomation.cache.get(:reference);" +
                  "element.scrollLeft();" +
                  "UIAutomation.createJSONResponse(':sessionId',0,'')";

  private static final String scrollRightTemplate =
          "var element = UIAutomation.cache.get(:reference);" +
                  "element.scrollRight();" +
                  "UIAutomation.createJSONResponse(':sessionId',0,'')";

  private static final String scrollToNameTemplate =
          "var element = UIAutomation.cache.get(:reference);" +
                  "element.scrollToElementWithName(':name');" +
                  "UIAutomation.createJSONResponse(':sessionId',0,'')";

  private static final String scrollToPredicateTemplate =
          "var element = UIAutomation.cache.get(:reference);" +
                  "element.scrollToElementWithPredicate(\":predicateString\");" +
                  "UIAutomation.createJSONResponse(':sessionId',0,'')";

  private static final String scrollToVisibleTemplate =
          "var element = UIAutomation.cache.get(:reference);" +
                  "element.scrollToVisible();" +
                  "UIAutomation.createJSONResponse(':sessionId',0,'')";

  public ElementScrollNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
    String template = null;

    JSONObject payload = request.getPayload();
    if (payload.has("direction")) {
      String direction = payload.optString("direction");
      if (direction.equals("up")) {
        template = scrollUpTemplate;
      } else if (direction.equals("down")) {
        template = scrollDownTemplate;
      } else if (direction.equals("left")) {
        template = scrollLeftTemplate;
      } else if (direction.equals("right")) {
        template = scrollRightTemplate;
      } else {
        throw new WebDriverException("Invalid value for scrolling direction");
      }

    } else if (payload.has("name")) {
      String name = payload.optString("name");
      template = scrollToNameTemplate.replace(":name", name);

    } else if (payload.has("predicateString")) {
      String predicateString = payload.optString("predicateString");
      template = scrollToPredicateTemplate.replace(":predicateString", predicateString);

    } else if (payload.has("toVisible")) {
      template = scrollToVisibleTemplate;

    } else {
      throw new WebDriverException("Unrecognised payload for ELEMENT_SCROLL");

    }


    String js = template
            .replace(":sessionId", request.getSession())
            .replace(":reference", request.getVariableValue(":reference"));

    setJS(js);
  }


  @Override
  public JSONObject configurationDescription() throws JSONException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
