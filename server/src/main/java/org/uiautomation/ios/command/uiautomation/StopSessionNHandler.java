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
package org.uiautomation.ios.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.IOSServerManager;
import org.uiautomation.ios.command.UIAScriptHandler;

import java.util.logging.Logger;

public class StopSessionNHandler extends UIAScriptHandler {

  private static final Logger log = Logger.getLogger(StopSessionNHandler.class.getName());

  public StopSessionNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
    setJS("stop");
  }

  public StopSessionNHandler(IOSServerManager server, ServerSideSession session){
    super(server,session);
    setJS("stop");
  }

  public Response handle() throws Exception {
    super.handle();
    // TODO freynaud. waiting 1 sec gives time to instruments to stop properly. ( get the stop command,
    // break the loop and finishing the script. Not waiting result in an instruments crash, but is faster.
    Thread.sleep(1000);
    String opaqueKey = getSession().getSessionId();
    getServer().stop(opaqueKey);

    Response resp = new Response();
    resp.setSessionId(opaqueKey);
    resp.setStatus(0);
    resp.setValue(new JSONObject());
    return resp;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }


}
