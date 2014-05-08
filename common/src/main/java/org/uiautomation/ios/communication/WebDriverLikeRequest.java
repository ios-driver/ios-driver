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
package org.uiautomation.ios.communication;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class WebDriverLikeRequest {

  private String method;
  private String path;
  // TODO freynaud extract that to a dedicated object to avoid json objects
  // leaking exception
  // everywhere.
  private JSONObject payload;

  public WebDriverLikeRequest(HttpServletRequest request) throws IOException, JSONException {
    method = request.getMethod();
    path = request.getPathInfo();
    String json = null;
    if (request.getInputStream() != null) {
      StringWriter w = new StringWriter();
      IOUtils.copy(request.getInputStream(), w, "UTF-8");
      json = w.toString();
    }
    JSONObject o = new JSONObject();
    if (json != null && !json.isEmpty()) {
      o = new JSONObject(json);

    }
    payload = o;
  }

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

  public WebDriverLikeRequest(String method, Path path, Map<String, ?> params) {
    this.method = method;
    this.path = path.getPath();
    this.payload = new JSONObject(params);
  }

  public boolean hasPayload() {
    return payload != null && payload.length() != 0;
  }

  public String toString() {
    String res = method + ":" + path;
    if (hasPayload()) {
      res += "\n\tbody:" + payload;
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

  public boolean hasVariable(String variable) {
    WebDriverLikeCommand genericCommand = getGenericCommand();
    boolean ok = genericCommand.path().contains(variable);
    return ok;
  }

  public String getSession() {
    return getVariableValue(":sessionId");
  }

  public boolean hasSession() {
    return hasVariable(":sessionId");
  }

}
