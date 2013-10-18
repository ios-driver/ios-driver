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

package org.uiautomation.ios.server.command.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.utils.CoordinateUtils;
import org.uiautomation.ios.server.utils.JSTemplate;
import org.uiautomation.ios.wkrdp.model.RemoteWebNativeBackedElement;

public class DoubleTapHandler extends UIAScriptHandler {

  private static final JSTemplate template = new JSTemplate(
      "UIATarget.localTarget().doubleTap({x:%:tapX$d, y:%:tapY$d});" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,'')",
      "sessionId", "tapX", "tapY"
  );

  public DoubleTapHandler(IOSServerManager driver, WebDriverLikeRequest request) throws Exception {
    super(driver, request);

    JSONObject payload = request.getPayload();
    String elementId = payload.optString("element");

    Dimension screenSize = driver.getSession(request.getSession()).getNativeDriver().getScreenSize();
    RemoteWebNativeBackedElement element = (RemoteWebNativeBackedElement) getSession().getRemoteWebDriver().createElement(elementId);
    Point tapPoint = element.getLocation();
    tapPoint = CoordinateUtils.forcePointOnScreen(tapPoint, screenSize);

    String js = template.generate(request.getSession(), tapPoint.getX(), tapPoint.getY());
    setJS(js);
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
