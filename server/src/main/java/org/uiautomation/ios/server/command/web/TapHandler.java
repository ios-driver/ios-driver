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
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.utils.CoordinateUtils;
import org.uiautomation.ios.wkrdp.model.RemoteWebElement;

public class TapHandler extends UIAScriptHandler {

  private static final String tapTemplate =
      "UIATarget.localTarget().tap({x:tapX, y:tapY, width:tapWidth, height:tapHeight});" +
          "UIAutomation.createJSONResponse(':sessionId',0,'')";

  // CGRectMake(yourPoint.x, yourPoint.y, yourSize.width, yourSize.height);

  public TapHandler(IOSServerManager driver, WebDriverLikeRequest request) throws Exception {
    super(driver, request);

    JSONObject payload = request.getPayload();
    String elementId = payload.optString("element");

    //String ref = request.getVariableValue(":reference");
    RemoteWebElement element = getSession().getRemoteWebDriver().createElement(elementId);
    Point center = CoordinateUtils.getCenterPointFromElement(element);

    Point location = element.getLocation();
    Dimension size = element.getSize();

    String js = tapTemplate
        .replace(":sessionId", request.getSession())
        .replace("tapX", Integer.toString(location.getX()))
        .replace("tapY", Integer.toString(location.getY() + 115))
        .replace("tapWidth", Integer.toString(size.getWidth()))
        .replace("tapHeight", Integer.toString(size.getHeight()));

    setJS(js);
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
