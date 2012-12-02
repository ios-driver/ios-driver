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
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class PinchCloseNHandler extends UIAScriptHandler{
  //UIATarget.localTarget().pinchCloseFromToForDuration({x:"300", y:"400"}, {x:"50", y:"100"}, "1");
  
  
    private static final String template = 
        "UIATarget.localTarget().pinchCloseFromToForDuration({x:':x1', y:':y1'}, {x:':x2', y:':y2'}, ':duration');" +
        "UIAutomation.createJSONResponse(':sessionId',0,'')";
    
    public PinchCloseNHandler(IOSDriver driver, WebDriverLikeRequest request) {
      super(driver, request);
      
      Object payload = request.getPayload();
      String js =  template
              .replace(":sessionId", request.getSession())
              .replace(":x1", "310")
              .replace(":y1", "410")
              .replace(":x2", "10")
              .replace(":y2", "25")
              .replace(":duration", "1");
      setJS(js);
    }
    
    @Override
    public JSONObject configurationDescription() throws JSONException {
      return noConfigDefined();
    }

}
