package org.uiautomation.ios.webInspector.DOM;

import org.json.JSONException;
import org.json.JSONObject;

public class DOM {

  
  public static JSONObject getDocument() throws JSONException{
    JSONObject cmd = new JSONObject();
    cmd.put("method", "DOM.getDocument");
    return cmd;
  }
  
  public static JSONObject resolveNode(int nodeId) throws JSONException{
    JSONObject cmd = new JSONObject();
    cmd.put("method", "DOM.resolveNode");
    cmd.put("params",new JSONObject()
    .put("nodeId", nodeId));
    return cmd;
  }
  
  
  public static JSONObject requestNode(RemoteObject ro) throws JSONException{
    JSONObject cmd = new JSONObject();
    cmd.put("method", "DOM.requestNode");
    cmd.put("params",new JSONObject()
    .put("objectId", ro.getId()));
    return cmd;
  }
  
}
