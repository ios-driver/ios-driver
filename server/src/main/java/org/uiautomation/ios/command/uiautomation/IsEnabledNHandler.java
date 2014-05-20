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
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.IOSServerManager;
import org.uiautomation.ios.command.UIAScriptHandler;
import org.uiautomation.ios.utils.JSTemplate;

public class IsEnabledNHandler extends UIAScriptHandler {

  private static final JSTemplate template = new JSTemplate(
      "var element = UIAutomation.cache.get(%:reference$s, false);" +
      "var result = element.isEnabled();" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,result)",
      "sessionId", "reference");

  public IsEnabledNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
    String js = template.generate(
        request.getSession(),
        request.getVariableValue(":reference"));
    setJS(js);
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return super.noConfigDefined();
  }
}