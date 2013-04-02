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

public class ScrollHandler extends UIAScriptHandler {

    private static final String voidTemplate =
            "UIATarget.localTarget().scrollDown();" +
                    "UIAutomation.createJSONResponse(':sessionId',0,'')";

    public ScrollHandler(IOSServerManager driver, WebDriverLikeRequest request) throws Exception {
        super(driver, request);

        JSONObject payload = request.getPayload();
        String elementId =  payload.optString("element");

        Point fromCoords = getStartCoordinatesCentered(request, elementId);


    }

    public Point getStartCoordinatesCentered(WebDriverLikeRequest request, String elementId) throws InterruptedException {

        final String getElementTemplate =
                "var element = UIAutomation.cache.get(:reference);" +
                        "UIAutomation.createJSONResponse(':sessionId',0,result);";

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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
