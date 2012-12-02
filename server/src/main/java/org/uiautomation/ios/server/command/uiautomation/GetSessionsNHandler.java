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

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.command.BaseNativeCommandHandler;

public class GetSessionsNHandler extends BaseNativeCommandHandler {

  public GetSessionsNHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    JSONArray res = new JSONArray();

    List<ServerSideSession> sessions = getDriver().getSessions();

    for (ServerSideSession s : sessions) {
      JSONObject session = new JSONObject();
      session.put("id", s.getSessionId());

      IOSApplication app = s.getApplication();
      IOSCapabilities cap = getDriver().getCapabilities(app);
      session.put("capabilities", cap.getRawCapabilities());
      res.put(session);
    }
    Response resp = new Response();
    resp.setSessionId("dummy one");
    resp.setStatus(0);
    resp.setValue(res.toString());
    return resp;
  }
  
  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
