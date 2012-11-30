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
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.UIAModels.configuration.CommandConfiguration;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;

public class SetTimeoutHandler extends BaseWebCommandHandler {

  public SetTimeoutHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  /**
   * type - {string} The type of operation to set the timeout for. Valid values
   * are: "script" for script timeouts, "implicit" for modifying the implicit
   * wait timeout and "page load" for setting a page load timeout.
   */
  @Override
  public Response handle() throws Exception {
    JSONObject payload = getRequest().getPayload();
    String type = payload.optString("type");
    if ("page load".equals(type)){
      long timeout = payload.getLong("ms");
      // meant for driver.get command
      CommandConfiguration conf = getSession().configure(WebDriverLikeCommand.URL);
      conf.set("page load",timeout);
    }else {
      throw new UnsupportedCommandException("timeout "+payload+" NI");
    }
    Response res = new Response();
    res.setSessionId(getSession().getSessionId());
    res.setStatus(0);
    res.setValue(new JSONObject());
    return res;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return new JSONObject();
  }

}
