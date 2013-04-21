/*
 * Copyright 2012 ios-driver committers.
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
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.command.UIAScriptRequest;
import org.uiautomation.ios.server.utils.CoordinateUtils;
import org.uiautomation.ios.wkrdp.model.RemoteWebElement;

public class ScrollHandler extends UIAScriptHandler {

  private static final String getScreenSizeTemplate =
      "var size = UIATarget.localTarget().rect().size;" +
          "UIAutomation.createJSONResponse(':sessionId',0,result);";

  private static final String scrollTemplate =
      "UIATarget.localTarget().dragFromToForDuration({{x:fromX, y:fromY}, {x:toX, y:toY}, 1});" +
          "UIAutomation.createJSONResponse(':sessionId',0,'')";


  public ScrollHandler(IOSServerManager driver, WebDriverLikeRequest request) throws Exception {
    super(driver, request);

    JSONObject payload = request.getPayload();
    String elementId = payload.optString("element");

    getScreenSize(request);

    //String ref = request.getVariableValue(":reference");
    RemoteWebElement element = getSession().getRemoteWebDriver().createElement(elementId);
    Point fromPoint;
    if(element!=null){
      fromPoint = CoordinateUtils.getCenterPointFromElement(element);
    }
    else{
      fromPoint = getScreenCenter();
    }
    Point offset = new Point(payload.getInt("xoffset"), payload.getInt("yoffset"));

    String js = scrollTemplate
        .replace(":sessionId", request.getSession())
        .replace("fromX", Integer.toString(fromPoint.getX()))
        .replace("fromY", Integer.toString(fromPoint.getY()))
        .replace("toX", Integer.toString(fromPoint.getX() + offset.getX()))
        .replace("toY", Integer.toString(fromPoint.getY() + offset.getY()));

    setJS(js);
  }

  private Point getScreenCenter(){
    return new Point(200, 200);
  }

  private Dimension getScreenSize(WebDriverLikeRequest request) throws InterruptedException {
    String getElementJS = getScreenSizeTemplate
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

    return new Dimension(1,1);
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
