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
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.IOSServerManager;
import org.uiautomation.ios.command.UIAScriptHandler;
import org.uiautomation.ios.utils.CoordinateUtils;
import org.uiautomation.ios.utils.JSTemplate;
import org.uiautomation.ios.drivers.RemoteIOSWebDriver;
import org.uiautomation.ios.wkrdp.model.NodeId;
import org.uiautomation.ios.wkrdp.model.RemoteWebElement;
import org.uiautomation.ios.wkrdp.model.RemoteWebNativeBackedElement;

public class FlickNHandler extends UIAScriptHandler {

  // dragFromToForDuration()'s duration argument must be in [0.5 .. 60.0).
  private static final JSTemplate plainFromElementTemplate = new JSTemplate(
      "var element = UIAutomation.cache.get(%:nodeId$d, false);" +
      "var status = Number(!element.isVisible());" +
      "if (status == 0) {" +
        "var hp = element.hitpoint();" +
        "var toX = hp.x + %:offsetX$d;" +
        "var toY = hp.y + %:offsetY$d;" +
        "if (toX < 0) {toX = 0} else if (toX >= %:screenSizeX$d) {toX = %:screenSizeX$d - 1}" +
        "if (toY < 0) {toY = 0} else if (toY >= %:screenSizeY$d) {toY = %:screenSizeY$d - 1}" +
        "var dx = toX - hp.x;" +
        "var dy = toY - hp.y;" +
        "var distance = Math.sqrt(dx*dx + dy*dy);" +
        "var duration = distance / %:speed$f;" +
        "if (duration < 0.5) {" +
          "UIATarget.localTarget().flickFromTo(hp, {x:toX, y:toY});" +
        "} else {" +
          "if (duration >= 60.0) {duration = 59.9}" +
          "UIATarget.localTarget().dragFromToForDuration(hp, {x:toX, y:toY}, duration);" +
        "}" +
      "}" +
      "UIAutomation.createJSONResponse('%:sessionId$s', status, '');",
      "sessionId", "nodeId", "screenSizeX", "screenSizeY", "offsetX", "offsetY", "speed");
  private static final JSTemplate fromToTemplate = new JSTemplate(
      "var duration = %:duration$f;" +
      "if (duration < 0.5) {" +
        "UIATarget.localTarget().flickFromTo({x:%:fromX$d, y:%:fromY$d}, {x:%:toX$d, y:%:toY$d});" +
      "} else {" +
        "if (duration >= 60.0) {duration = 59.9}" +
        "UIATarget.localTarget().dragFromToForDuration({x:%:fromX$d, y:%:fromY$d}, {x:%:toX$d, y:%:toY$d}, duration);" +
      "}" +
      "UIAutomation.createJSONResponse('%:sessionId$s', 0, '')",
      "sessionId", "fromX", "fromY", "toX", "toY", "duration");

  public FlickNHandler(IOSServerManager driver, WebDriverLikeRequest request) throws Exception {
    super(driver, request);

    JSONObject payload = request.getPayload();
    Dimension screenSize = getNativeDriver().getScreenSize();
    String elementId = payload.optString("element");
    if (!payload.isNull("element") && !elementId.equals("")) {
      Point offset = new Point(payload.getInt("xoffset"), payload.getInt("yoffset"));
      double speed = payload.optDouble("speed", 1.0);
      if (RemoteIOSWebDriver.isPlainElement(elementId)) {
        NodeId nodeId = RemoteIOSWebDriver.plainNodeId(elementId);
        plainFlickFromElement(request, screenSize, offset, speed, nodeId.getId());
      } else {
        nativeFlickFromElement(request, screenSize, offset, speed, elementId);
      }
    } else {
      double speedX = payload.optDouble("xspeed", 1.0);
      double speedY = payload.optDouble("yspeed", 1.0);
      flickFromCenter(request, screenSize, speedX, speedY);
    }
  }

  private void plainFlickFromElement(WebDriverLikeRequest request, Dimension screenSize, Point offset, double speed,
                                     int nodeId) {
    String js = plainFromElementTemplate.generate(
        request.getSession(),
        nodeId,
        screenSize.getWidth(),
        screenSize.getHeight(),
        offset.getX(),
        offset.getY(),
        speed);
    setJS(js);
  }

  private void nativeFlickFromElement(WebDriverLikeRequest request, Dimension screenSize, Point offset, double speed,
                                      String elementId) throws Exception {
    RemoteWebNativeBackedElement element = (RemoteWebNativeBackedElement) getWebDriver().createElement(elementId);
    Point fromPoint = element.getLocation(RemoteWebElement.ElementPosition.CENTER);
    Point toPoint = new Point(fromPoint.getX() + offset.getX(), fromPoint.getY() + offset.getY());
    fromPoint = CoordinateUtils.forcePointOnScreen(fromPoint, screenSize);
    toPoint = CoordinateUtils.forcePointOnScreen(toPoint, screenSize);
    int dx = toPoint.getX() - fromPoint.getX();
    int dy = toPoint.getY() - fromPoint.getY();
    double distance = Math.sqrt(dx*dx + dy*dy);
    double duration = distance / speed;
    setJS(fromToTemplate.generate(
        request.getSession(),
        fromPoint.getX(),
        fromPoint.getY(),
        toPoint.getX(),
        toPoint.getY(),
        duration));
  }

  private void flickFromCenter(WebDriverLikeRequest request, Dimension screenSize, double speedX, double speedY) {
    Point fromPoint = CoordinateUtils.getScreenCenter(screenSize);

    // Convert "flick from center at given speed vector" to "drag from center to edge for duration".
    double duration = Math.min(
        fromPoint.getX() / Math.abs(speedX),
        fromPoint.getY() / Math.abs(speedY));
    Point toPoint = new Point(
        (int)(fromPoint.getX() + speedX * duration),
        (int)(fromPoint.getY() + speedY * duration));
    toPoint = CoordinateUtils.forcePointOnScreen(toPoint, screenSize);
    setJS(fromToTemplate.generate(
        request.getSession(),
        fromPoint.getX(),
        fromPoint.getY(),
        toPoint.getX(),
        toPoint.getY(),
        duration));
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
