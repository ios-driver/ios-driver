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
package org.uiautomation.ios.server.command.impl;

import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class StopSession extends UIAScriptHandler {

  public StopSession(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
    setJS("stop");
  }

  public WebDriverLikeResponse handle() throws Exception {
    super.handle();
    String opaqueKey = getRequest().getSession();
    getDriver().stop(opaqueKey);
    
    
    JSONObject o = new JSONObject();
    try {
      o.put("sessionId", opaqueKey);
      o.put("status", 0);
      o.put("value", "");
      WebDriverLikeResponse r = new WebDriverLikeResponse(o);
      return r;
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    } 

  }

}
