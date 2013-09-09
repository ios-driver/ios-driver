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
import org.uiautomation.ios.wkrdp.model.RemoteWebNativeBackedElement;

public class FlickNHandler extends UIAScriptHandler {
  private static final String dragTemplate =
      "UIATarget.localTarget().dragFromToForDuration({x:fromX,y:fromY},{x:toX,y:toY},duration);" +
          "UIAutomation.createJSONResponse(':sessionId',0,'')";
  private static final String getElementTemplate =
      "var element = UIAutomation.cache.get(:reference);" +
          "UIAutomation.createJSONResponse(':sessionId',0,result);";

  public FlickNHandler(IOSServerManager driver, WebDriverLikeRequest request) throws Exception {
    super(driver, request);


    JSONObject payload = request.getPayload();
    String elementId = payload.optString("element");

    Point fromPoint = null;
    Dimension screenSize =getNativeDriver().getScreenSize();

    if (!payload.isNull("element") && !elementId.equals("")) {
      try {
        RemoteWebNativeBackedElement element = (RemoteWebNativeBackedElement) getWebDriver().createElement(elementId);
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

    String js = dragTemplate
        .replace(":sessionId", request.getSession())
        .replace("fromX", String.valueOf(fromPoint.getX()))
        .replace("fromY", String.valueOf(fromPoint.getY()))
        .replace("toX", String.valueOf(toPoint.getX()))
        .replace("toY", String.valueOf(toPoint.getY()))
        .replace("duration", speed);
    setJS(js);
  }

  /*public Point getStartCoordinatesCentered(WebDriverLikeRequest request, String elementId) throws InterruptedException {
    String getElementJS = getElementTemplate
        .replace(":reference", elementId)
        .replace(":sessionId", request.getSession());

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
