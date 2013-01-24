/*
 * Copyright 2012 ios-driver committers.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the Licence at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */

package org.uiautomation.ios.webInspector.DOM;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;

public class Page {

  public static JSONObject navigate(String url) {
    try {
      JSONObject cmd = new JSONObject();
      cmd.put("method", "Page.navigate");
      cmd.put("params", new JSONObject().put("url", url));
      return cmd;
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
  }

  public static JSONObject enablePageEvent() {
    try {
      JSONObject cmd = new JSONObject();
      cmd.put("method", "Page.enable");
      return cmd;
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
  }

  public static JSONObject getResourceTree() {
    try {
      JSONObject cmd = new JSONObject();
      cmd.put("method", "Page.getResourceTree");
      return cmd;
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
  }
  /*public static JSONObject captureScreenshot() {
    try {
      JSONObject cmd = new JSONObject();
      cmd.put("method", "Page.captureScreenshot");
      return cmd;
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
  } */


}
