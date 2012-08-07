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
package org.uiautomation.ios.communication;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.exceptions.IOSAutomationException;

public class WebDriverLikeResponse {

  private String sessionId;
  private int status;
  private Object value;

  protected WebDriverLikeResponse() {}

  public WebDriverLikeResponse(String sessionId, int status, Object value) {
    this.sessionId = sessionId;
    this.status = status;
    this.value = value;
  }

  public WebDriverLikeResponse(JSONObject content) {
    try {
      this.sessionId = content.getString("sessionId");
      this.status = content.getInt("status");
      this.value = content.get("value");
    } catch (JSONException e) {
      throw new IOSAutomationException(e);
    }

  }


  public String getSessionId() {
    return sessionId;
  }

  public int getStatus() {
    return status;
  }

  public Object getValue() {
    return value;
  }

  public String stringify() throws JSONException {
    JSONObject o = new JSONObject();
    o.put("sessionId", sessionId);
    if (sessionId==null){
      o.put("sessionId", JSONObject.NULL);
    }
    o.put("status", status);
    o.put("value", value);
    return o.toString(2);
  }

  protected void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  protected void setStatus(int status) {
    this.status = status;
  }

  public void setValue(Object value) {
    this.value = value;
  }



}
