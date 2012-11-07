package org.uiautomation.ios.webInspector.DOM;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.webInspector.WebInspector;

public class DOM {

  private final WebInspector inspector;

  public DOM(WebInspector inspector) {
    this.inspector = inspector;
  }

  public static JSONObject getDocument() throws JSONException {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "DOM.getDocument");
    return cmd;
  }

  public RemoteObject resolveNode(int nodeId) {
    JSONObject cmd = new JSONObject();
    try {
      cmd.put("method", "DOM.resolveNode");
      cmd.put("params", new JSONObject().put("nodeId", nodeId));

      JSONObject t = inspector.sendCommand(cmd);
      RemoteObject remoteObject =
          new RemoteObject(t.getJSONObject("result").getJSONObject("object"));
      return remoteObject;
    } catch (Exception e) {
      throw new IOSAutomationException("error remote debugging " + e.getMessage(), e);
    }

  }


  public static JSONObject requestNode(RemoteObject ro) throws JSONException {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "DOM.requestNode");
    cmd.put("params", new JSONObject().put("objectId", ro.getId()));
    return cmd;
  }


  public static JSONObject setChildNodes(JSONObject o) {
    return null;
  }



}
