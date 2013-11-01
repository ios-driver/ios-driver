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
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.utils.CoordinateUtils;
import org.uiautomation.ios.server.utils.JSTemplate;
import org.uiautomation.ios.wkrdp.RemoteIOSWebDriver;
import org.uiautomation.ios.wkrdp.model.NodeId;
import org.uiautomation.ios.wkrdp.model.RemoteWebNativeBackedElement;

public class ScrollNHandler extends UIAScriptHandler {

  private static final JSTemplate plainFromElementTemplate = new JSTemplate(
      "var element = UIAutomation.cache.get(%:nodeId$d, false);" +
      "var status = Number(!element.isVisible());" +
      "if (status == 0) {" +
        "var hp = element.hitpoint();" +
        "var toX = hp.x + %:toX$d;" +
        "var toY = hp.y + %:toY$d;" +
        "if (toX < 0) {toX = 0} else if (toX >= %:screenSizeX$d) {toX = %:screenSizeX$d - 1}" +
        "if (toY < 0) {toY = 0} else if (toY >= %:screenSizeY$d) {toY = %:screenSizeY$d - 1}" +
        "UIATarget.localTarget().dragFromToForDuration(hp, {x:toX, y:toY}, 1);" +
      "}" +
      "UIAutomation.createJSONResponse('%:sessionId$s', status, '');",
      "sessionId", "nodeId", "screenSizeX", "screenSizeY", "toX", "toY");
  private static final JSTemplate nativeFromElementTemplate = new JSTemplate(
      "UIATarget.localTarget().dragFromToForDuration({x:%:fromX$d,y:%:fromY$d},{x:%:toX$d,y:%:toY$d},1);" +
          "UIAutomation.createJSONResponse('%:sessionId$s',0,'')",
      "sessionId", "fromX", "fromY", "toX", "toY");
  private static final JSTemplate fromCenterTemplate = new JSTemplate(
      "UIATarget.localTarget().dragFromToForDuration({x:%:fromX$d, y:%:fromY$d}, {x:%:toX$d, y:%:toY$d}, 1);" +
      "UIAutomation.createJSONResponse('%:sessionId$s', 0, '')",
      "sessionId", "fromX", "fromY", "toX", "toY");

  public ScrollNHandler(IOSServerManager driver, WebDriverLikeRequest request) throws Exception {
    super(driver, request);

    JSONObject payload = request.getPayload();
    Dimension screenSize = getNativeDriver().getScreenSize();
    Point offset = new Point(payload.getInt("xoffset"), payload.getInt("yoffset"));
    String elementId = payload.optString("element");
    if (!payload.isNull("element") && !elementId.equals("")) {
      if (RemoteIOSWebDriver.isPlainElement(elementId)) {
        NodeId nodeId = RemoteIOSWebDriver.plainNodeId(elementId);
        plainScrollFromElement(request, screenSize, nodeId.getId(), offset);
      } else {
        nativeScrollFromElement(request, screenSize, elementId, offset);
      }
    } else {
      scrollFromCenter(request, screenSize, offset);
    }
  }

  private void plainScrollFromElement(WebDriverLikeRequest request, Dimension screenSize, int nodeId, Point offset) {
    setJS(plainFromElementTemplate.generate(
        request.getSession(),
        nodeId,
        screenSize.getWidth(),
        screenSize.getHeight(),
        offset.getX(),
        offset.getY()));
  }

  private void nativeScrollFromElement(WebDriverLikeRequest request, Dimension screenSize, String elementId,
                                       Point offset) throws Exception {
    RemoteWebNativeBackedElement element = (RemoteWebNativeBackedElement) getWebDriver().createElement(elementId);
    Point fromPoint = element.getLocation();
    fromPoint = CoordinateUtils.forcePointOnScreen(fromPoint, screenSize);
    Point toPoint = new Point(
        fromPoint.getX() + offset.getX(),
        fromPoint.getY() + offset.getY());
    toPoint = CoordinateUtils.forcePointOnScreen(toPoint, screenSize);
    setJS(nativeFromElementTemplate.generate(
        request.getSession(),
        fromPoint.getX(),
        fromPoint.getY(),
        toPoint.getX(),
        toPoint.getY()));
  }

  private void scrollFromCenter(WebDriverLikeRequest request, Dimension screenSize, Point offset) {
    Point fromPoint = CoordinateUtils.getScreenCenter(screenSize);
    Point toPoint = new Point(fromPoint.getX() + offset.getX(), fromPoint.getY() + offset.getY());
    toPoint = CoordinateUtils.forcePointOnScreen(toPoint, screenSize);
    setJS(fromCenterTemplate.generate(
        request.getSession(),
        fromPoint.getX(),
        fromPoint.getY(),
        toPoint.getX(),
        toPoint.getY()));
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
