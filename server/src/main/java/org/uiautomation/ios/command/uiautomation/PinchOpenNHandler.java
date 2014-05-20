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

public class PinchOpenNHandler extends UIAScriptHandler {

  private static final JSTemplate template = new JSTemplate(
      "UIATarget.localTarget().pinchOpenFromToForDuration({x:'%:x1$f', y:'%:y1$f'}, {x:'%:x2$f', y:'%:y2$f'}, '%:duration$f');" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,'')",
      "sessionId", "x1", "y1", "x2", "y2", "duration");

  public PinchOpenNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);

    JSONObject payload = request.getPayload();
    String js = template.generate(
        request.getSession(),
        payload.optDouble("fromX"),
        payload.optDouble("fromY"),
        payload.optDouble("toX"),
        payload.optDouble("toY"),
        payload.optDouble("duration"));
    setJS(js);
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}