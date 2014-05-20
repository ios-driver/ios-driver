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

public class FlickInsideWithOptionsNHandler extends UIAScriptHandler {

  private static final JSTemplate template = new JSTemplate(
      "var element = UIAutomation.cache.get(%:reference$s);" +
      "element.flickInsideWithOptions({touchCount:%:touchCountx$d, " +
          "startOffset:{x:%:xstart$f, y:%:ystart$f}, endOffset:{x:%:xend$f, y:%:yend$f}});" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,'')",
      "sessionId", "reference", "xstart", "ystart", "xend", "yend", "touchCountx");

  public FlickInsideWithOptionsNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);

    JSONObject payload = request.getPayload();

    String js = template.generate(
        request.getSession(),
        request.getVariableValue(":reference"),
        payload.optDouble("xstart"),
        payload.optDouble("ystart"),
        payload.optDouble("xend"),
        payload.optDouble("yend"),
        payload.optInt("touchCount"));
    setJS(js);
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
