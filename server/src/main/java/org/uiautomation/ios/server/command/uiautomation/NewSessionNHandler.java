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
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.command.BaseNativeCommandHandler;

public class NewSessionNHandler extends BaseNativeCommandHandler {

  private ServerSideSession session;

  public NewSessionNHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
    
  }

  public Response handle() throws Exception {
    try {
      GetCapabilitiesNHandler.reset();
      JSONObject payload = getRequest().getPayload();
      IOSCapabilities capabilities = new IOSCapabilities(payload.getJSONObject("desiredCapabilities"));
      session = getDriver().createSession(capabilities);
      session.start();


      Response resp = new Response();
      resp.setSessionId(session.getSessionId());
      resp.setStatus(0);
      resp.setValue("");
      return resp;
    } catch (Exception e) {
      throw new SessionNotCreatedException(e.getMessage());
    }

  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
