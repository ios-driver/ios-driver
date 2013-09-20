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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.utils.JSTemplate;

public class SetValueNHandler extends UIAScriptHandler {

  private static final JSTemplate template = new JSTemplate(
      "var parent = UIAutomation.cache.get(%:reference$s);" +
      "parent.sendKeys('%:value$s',%:useNativeEvents$b);" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,'')",
      "sessionId", "reference", "value", "useNativeEvents");

  public SetValueNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);

    boolean useNativeEvents = getConfiguration("nativeEvents");
    try {
      JSONArray array = request.getPayload().getJSONArray("value");
      StringBuilder b = new StringBuilder();

      if (array != null) {
        int len = array.length();
        for (int i = 0; i < len; i++) {
          b.append(array.get(i).toString());
        }
      }

      String corrected = b.toString().replaceAll("\\\\", "\\\\\\\\");
      corrected = corrected.replaceAll("\\n", "\\\\n");
      corrected = corrected.replaceAll("\\t", "\\\\t");

      String js = template.generate(
          request.getSession(),
          request.getVariableValue(":reference"),
          corrected,
          useNativeEvents);
      setJS(js);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}