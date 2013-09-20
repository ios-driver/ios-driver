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
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.command.UIAScriptRequest;
import org.uiautomation.ios.server.utils.CoordinateUtils;
import org.uiautomation.ios.server.utils.JSTemplate;
import org.uiautomation.ios.wkrdp.model.RemoteWebNativeBackedElement;

public class FlickNHandler extends UIAScriptHandler {
  private static final JSTemplate dragTemplate = new JSTemplate(
      "UIATarget.localTarget().dragFromToForDuration({x:%:fromX$d,y:%:fromY$d},{x:%:toX$d,y:%:toY$d},%:duration$s);" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,'')",
      "sessionId", "fromX", "fromY", "toX", "toY", "duration");
  private static final JSTemplate getElementTemplate = new JSTemplate(
      "var element = UIAutomation.cache.get(%:reference$s);" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,result);",
      "sessionId", "reference");

  public FlickNHandler(IOSServerManager driver, WebDriverLikeRequest request) throws Exception {
    super(driver, request);

    JSONObject payload = request.getPayload();
    String elementId = payload.optString("element");

    Point fromPoint = null;
    Dimension screenSize = driver.getSession(request.getSession()).getNativeDriver().getScreenSize();

    if (!payload.isNull("element") && !elementId.equals("")) {
      try {
        RemoteWebNativeBackedElement element = (RemoteWebNativeBackedElement) getSession().getRemoteWebDriver().createElement(elementId);
        fromPoint = element.getLocation();
      } catch (Exception e) {
        // TODO freynaud fix that.
        //fromPoint = getStartCoordinatesCentered(request, elementId);
      }
    } else {
      fromPoint = new Point(screenSize.getWidth() / 2, screenSize.getHeight() / 2);
    }

    String xOffset = payload.optString("xoffset");
    if (xOffset.equals("")) {
      xOffset = payload.optString("xspeed");
    }
    String yOffset = payload.optString("yoffset");
    if (yOffset.equals("")) {
      yOffset = payload.optString("yspeed");
    }

    String speed = payload.optString("speed").equals("") ? "1" : payload.optString("speed");
    if(Integer.valueOf(speed) < .5){
      speed = "0.5";
    }

    Point toPoint = new Point(fromPoint.getX() + Integer.valueOf(xOffset), fromPoint.getY() + Integer.valueOf(yOffset));

    fromPoint = CoordinateUtils.forcePointOnScreen(fromPoint, screenSize);
    toPoint = CoordinateUtils.forcePointOnScreen(toPoint, screenSize);

    String js = dragTemplate.generate(
        request.getSession(),
        fromPoint.getX(),
        fromPoint.getY(),
        toPoint.getX(),
        toPoint.getY(),
        speed);
    setJS(js);
  }

  /*public Point getStartCoordinatesCentered(WebDriverLikeRequest request, String elementId) throws InterruptedException {
    String getElementJS = getElementTemplate.generate(request.getSession(), elementId);

    UIAScriptRequest r = new UIAScriptRequest(getElementJS);
    communication().sendNextCommand(r);
    Response response;
    if ("stop".equals(getElementJS)) {
      response = new Response();
      response.setSessionId(getRequest().getSession());
      response.setStatus(0);
      response.setValue("ok");
    } else {
      response = communication().waitForResponse().getResponse();
    }

    return CoordinateUtils.getCenterPointFromResponse(response);
  }*/

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}