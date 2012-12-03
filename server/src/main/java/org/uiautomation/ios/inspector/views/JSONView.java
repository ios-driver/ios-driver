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
package org.uiautomation.ios.inspector.views;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;

public class JSONView implements View {

  private JSONObject object;
  private JSONArray array;

  public JSONView(JSONObject o) {
    this.object = o;
  }

  public JSONView(JSONArray a) {
    this.array = a;
  }

  private String getContent() throws JSONException {
    if (object == null && array == null) {
      throw new WebDriverException("json view needs to have either jsonobject or array. Cannot be null");
    }
    int indent = 2;
    return object != null ? object.toString(indent) : array.toString(indent);
  }

  @Override
  public void render(HttpServletResponse response) throws Exception {
    response.setContentType("application/x-javascript");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(200);
    response.getWriter().print(getContent());
  }

}
