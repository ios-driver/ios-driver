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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class SetValueNHandler extends UIAScriptHandler{

  private static final String voidTemplate =
      "var parent = UIAutomation.cache.get(:reference);" +
      "parent:jsMethod;" +
      "UIAutomation.createJSONResponse(':sessionId',0,'')";
  
  public SetValueNHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
    try {
      JSONArray array  =request.getPayload().getJSONArray("value");
      String value = array.getString(0);
      String corrected = value.replaceAll("\\\\", "\\\\\\\\");
      corrected = corrected.replaceAll("\\n", "\\\\n");
      corrected = corrected.replaceAll("\\t", "\\\\t");


      
      String js = voidTemplate
          .replace(":sessionId", request.getSession())
          .replace(":reference", request.getVariableValue(":reference"))
          .replace(":jsMethod", ".setValue('"+corrected+"')");
      setJS(js);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    
    return noConfigDefined();
  }

}
