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
package org.uiautomation.ios.server.command;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Response;

public class UIAScriptResponse {
  private final String rawResponse;
  
  public UIAScriptResponse(String rawResponse) {
    this.rawResponse = rawResponse;
  }

  public String toString() {
    return rawResponse;
  }

  public Response getResponse() {
    Response response = new Response();
    try {
      JSONObject res = new JSONObject(rawResponse);
      response.setSessionId(res.getString("sessionId"));
      response.setStatus(res.getInt("status"));
      response.setValue(cast(res.get("value")));
      return response;
    } catch (Exception e) {
      throw new WebDriverException(e.getMessage(), e);
    }
  }

  private Object cast(Object object) throws Exception {
   if (object instanceof JSONArray){
     JSONArray ar = (JSONArray)object;
     List<Object> res = new ArrayList<Object>();
     for (int i=0;i<ar.length();i++){
       res.add(ar.get(i));
     }
     return res;
   }else {
     return object;
   }
  }

  public String getRaw() {
    return rawResponse;
  }

}
