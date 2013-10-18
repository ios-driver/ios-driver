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

public class ScrollHandler extends UIAScriptHandler {

  private static final JSTemplate template = new JSTemplate(
      "UIATarget.localTarget().dragFromToForDuration({x:%:fromX$d,y:%:fromY$d},{x:%:toX$d,y:%:toY$d},1);" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,'')",
      "sessionId", "fromX", "fromY", "toX", "toY"
  );

  public ScrollHandler(IOSServerManager driver, WebDriverLikeRequest request) throws Exception {
    super(driver, request);

    JSONObject payload = request.getPayload();
    String elementId = payload.optString("element");

    Dimension screenSize = driver.getSession(request.getSession()).getNativeDriver().getScreenSize();

    Point fromPoint;
    if (!payload.isNull("element") && !elementId.equals("")) {
      RemoteWebNativeBackedElement element = (RemoteWebNativeBackedElement) getSession().getRemoteWebDriver().createElement(elementId);
      fromPoint = element.getLocation();
    } else {
      fromPoint = new Point(screenSize.getWidth() / 2, screenSize.getHeight() / 2);
    }
    fromPoint =  CoordinateUtils.forcePointOnScreen(fromPoint, screenSize);
    Point toPoint = new Point(fromPoint.getX() + payload.getInt("xoffset"), fromPoint.getY() + payload.getInt("yoffset"));
    toPoint = CoordinateUtils.forcePointOnScreen(toPoint, screenSize);
    String js = template.generate(
        request.getSession(),
        fromPoint.getX(),
        fromPoint.getY(),
        toPoint.getX(),
        toPoint.getY());
    setJS(js);
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
