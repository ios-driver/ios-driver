package org.uiautomation.ios.webInspector.DOM;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.mobileSafari.NodeId;

public class DOM {

  public static JSONObject getDocument() throws JSONException {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "DOM.getDocument");
    return cmd;
  }

  public static JSONObject resolveNode(NodeId id) throws JSONException {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "DOM.resolveNode");
    cmd.put("params", new JSONObject().put("nodeId", id.getId()));
    return cmd;
  }

  public static JSONObject requestNode(String objectId) throws JSONException {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "DOM.requestNode");
    cmd.put("params", new JSONObject().put("objectId", objectId));
    return cmd;
  }

  public static JSONObject querySelector(NodeId id, String selector) throws JSONException {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "DOM.querySelector");
    cmd.put("params", new JSONObject().put("nodeId", id.getId()).put("selector", selector));
    return cmd;
  }

  public static JSONObject highlightNode(NodeId id) throws JSONException {
    JSONObject color = new JSONObject().put("a", 0.5).put("r", 50).put("g", 100).put("b", 255);

    JSONObject cmd = new JSONObject();
    cmd.put("method", "DOM.highlightNode");
    cmd.put(
        "params",
        new JSONObject().put("nodeId", id.getId()).put("highlightConfig",
            new JSONObject().put("showInfo", true).put("contentColor", color)

        ));
    return cmd;
  }

}
