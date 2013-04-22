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
package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.Point;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.command.UIAScriptRequest;
import org.uiautomation.ios.server.utils.CoordinateUtils;

public class FlickNHandler extends UIAScriptHandler {

    private static final String voidTemplate =
            "UIATarget.localTarget().flickFromToForDuration({x:fromX, y:fromY}, {x:toX, y:toY}, duration);" +
                    "UIAutomation.createJSONResponse(':sessionId',0,'')";

    private static final String getElementTemplate =
            "var element = UIAutomation.cache.get(:reference);" +
                    "UIAutomation.createJSONResponse(':sessionId',0,result);";


    public FlickNHandler(IOSServerManager driver, WebDriverLikeRequest request) throws Exception {
        super(driver, request);

        JSONObject payload = request.getPayload();
        String elementId =  payload.optString("element");
        Point fromCoords = getStartCoordinatesCentered(request, elementId);


        String xOffset = payload.optString("xoffset");
        String yOffset = payload.optString("yoffset");
        String speed = payload.optString("speed");

        String js = voidTemplate
                .replace(":sessionId", request.getSession())
                .replace("fromX", Integer.toString(fromCoords.getX()))
                .replace("fromY", Integer.toString(fromCoords.getY()))
                .replace("toX", Integer.toString((fromCoords.getX() + Integer.valueOf(xOffset))))
                .replace("toY", Integer.toString((fromCoords.getY() + Integer.valueOf(yOffset))))
                .replace("duration", speed);
        setJS(js);
    }

    public Point getStartCoordinatesCentered(WebDriverLikeRequest request, String elementId) throws InterruptedException {
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
    }

    @Override
    public JSONObject configurationDescription() throws JSONException {
        return noConfigDefined();
    }


}
