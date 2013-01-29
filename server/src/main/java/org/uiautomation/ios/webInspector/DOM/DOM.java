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
package org.uiautomation.ios.webInspector.DOM;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.mobileSafari.NodeId;

public class DOM {

  public static JSONObject getDocument() throws JSONException {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "DOM.getDocument");
    return cmd;
  }

  public static JSONObject resolveNode(NodeId id) {
    JSONObject cmd = new JSONObject();
    try {
      cmd.put("method", "DOM.resolveNode");
      cmd.put("params", new JSONObject().put("nodeId", id.getId()));
      return cmd;
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
  }

  public static JSONObject requestNode(String objectId) throws JSONException {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "DOM.requestNode");
    cmd.put("params", new JSONObject().put("objectId", objectId));
    return cmd;
  }

  public static JSONObject querySelector(NodeId id, String selector) {
    try {
      JSONObject cmd = new JSONObject();
      cmd.put("method", "DOM.querySelector");
      cmd.put("params", new JSONObject().put("nodeId", id.getId()).put("selector", selector));
      return cmd;
    } catch (Exception e) {
      throw new WebDriverException(e);
    }
  }

  public static JSONObject querySelectorAll(NodeId id, String selector) {
    try {
      JSONObject cmd = new JSONObject();
      cmd.put("method", "DOM.querySelectorAll");
      cmd.put("params", new JSONObject().put("nodeId", id.getId()).put("selector", selector));
      return cmd;
    } catch (Exception e) {
      throw new WebDriverException(e);
    }

  }

  public static JSONObject getAttributes(NodeId id) throws JSONException {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "DOM.getAttributes");
    cmd.put("params", new JSONObject().put("nodeId", id.getId()));
    return cmd;
  }

  public static JSONObject highlightNode(NodeId id) {
    try {
      JSONObject color = new JSONObject()
          .put("a", 0.5)
          .put("r", 50)
          .put("g", 100)
          .put("b", 255);

      JSONObject cmd = new JSONObject();
      cmd.put("method", "DOM.highlightNode");
      cmd.put(
          "params",
          new JSONObject()
              .put("nodeId", id.getId())
              .put("highlightConfig", new JSONObject()
                  .put("showInfo", true)
                  .put("contentColor", color)
              ));
      return cmd;
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
  }
}
