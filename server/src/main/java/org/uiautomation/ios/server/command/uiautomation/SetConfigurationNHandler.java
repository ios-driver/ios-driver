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

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseNativeCommandHandler;

public class SetConfigurationNHandler extends BaseNativeCommandHandler {

  public SetConfigurationNHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    WebDriverLikeCommand command = WebDriverLikeCommand.valueOf((String) getRequest().getVariableValue(":command"));
    JSONObject payload = getRequest().getPayload();

    Iterator<String> iter = payload.keys();
    while (iter.hasNext()) {
      String key = iter.next();
      Object value = payload.opt(key);
      getSession().configure(command).set(key, value);
    }

    Response resp = new Response();
    resp.setSessionId(getSession().getSessionId());
    resp.setStatus(0);
    resp.setValue(new JSONObject());
    return resp;

  }
  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
