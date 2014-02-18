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

package org.uiautomation.ios.server.command.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;
import org.uiautomation.ios.server.command.uiautomation.SetScriptTimeoutNHandler;

import java.util.ArrayList;
import java.util.List;


public class SetScriptTimeoutHandler extends BaseWebCommandHandler {

  private static final List<WebDriverLikeCommand> impacted = new ArrayList<WebDriverLikeCommand>();

  public SetScriptTimeoutHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
    impacted.add(WebDriverLikeCommand.ELEMENT);
    impacted.add(WebDriverLikeCommand.ELEMENT_ROOT);
    impacted.add(WebDriverLikeCommand.ELEMENTS);
    impacted.add(WebDriverLikeCommand.ELEMENTS_ROOT);
  }

  @Override
  public Response handle() throws Exception {
    Integer ms = getRequest().getPayload().getInt("ms");
    SetScriptTimeoutNHandler.TIMEOUT = ms;
    for (WebDriverLikeCommand command : impacted) {
      getSession().configure(command).set("script", ms);
    }

    Response res = new Response();
    res.setSessionId(getSession().getSessionId());
    res.setStatus(0);
    res.setValue(new JSONObject());
    return res;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
