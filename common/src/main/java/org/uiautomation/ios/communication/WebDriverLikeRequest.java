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

public class WebDriverLikeRequest {


  private String method;
  private String path;
  // TODO freynaud extract that to a dedicated object to avoid json objects leaking exception
  // everywhere.
  private JSONObject payload;

  public WebDriverLikeRequest(String method, Path path, JSONObject payload) {
    this.method = method;
    this.path = path.getPath();
    this.payload = payload;
  }

  public WebDriverLikeRequest(String method, String path) {
    this(method, path, new JSONObject());
  }

  public WebDriverLikeRequest(String method, Path path) {
    this(method, path, new JSONObject());
  }

  public WebDriverLikeRequest(String method, String path, JSONObject payload) {
    this.method = method;
    this.path = path;
    this.payload = payload;
  }

  public boolean hasPayload() {
    return payload != null && payload.length() != 0;
  }

  public String toString() {
    String res = method + ":" + path;
    if (hasPayload()) {
      res += "\nbody:" + payload;
    }
    return res;
  }

  public String toJSON() throws JSONException {
    return toJSON(0);
  }


  public String toJSON(int i) throws JSONException {
    JSONObject o = new JSONObject();
    o.put("method", method);
    o.put("path", path);
    o.put("payload", payload);
    return o.toString(i);
  }



  public String getMethod() {
    return method;
  }



  public String getPath() {
    return path;
  }



  public JSONObject getPayload() {
    return payload;
  }

  public WebDriverLikeCommand getGenericCommand() {
    return WebDriverLikeCommand.getCommand(method, path);
  }

  public String getVariableValue(String variable) {
    WebDriverLikeCommand genericCommand = getGenericCommand();
    int i = genericCommand.getIndex(variable);
    String[] pieces = path.split("/");
    return pieces[i];
  }
  
  public String getSession(){
    return getVariableValue(":sessionId");
  }



}
