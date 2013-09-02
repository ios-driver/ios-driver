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


public class SetAlertTextHandler extends UIAScriptHandler {

  private static final String jsTemplate = "var alert = UIAutomation.cache.get(3);"
                                           + "alert.sendKeys(':value');"
                                           + "UIAutomation.createJSONResponse(':sessionId',0,'');";

  public SetAlertTextHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);

    try {
      String value = request.getPayload().getString("text");
      String corrected = value.replaceAll("\\\\", "\\\\\\\\");
      corrected = corrected.replaceAll("\\n", "\\\\n");

      if (value.contains("\\t")) {
        throw new WebDriverException("cannot tab on the ios keyboard.");
      }
      String js = jsTemplate
          .replace(":sessionId", getRequest().getSession())
          .replace(":value", corrected);
      setJS(js);

    } catch (JSONException e) {
      throw new WebDriverException("error parsing request " + request + "\n" + e.getMessage(), e);
    }

  }


  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}