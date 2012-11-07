package org.uiautomation.ios.webInspector.DOM;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.webInspector.DebugProtocol;

public class RemoteObject {

  private String id;
  private final DebugProtocol protocol;
  private final JSONObject raw;

  public RemoteObject(JSONObject o,DebugProtocol protocol) throws JSONException {
    this.raw = o;
    this.protocol = protocol;
    this.id = o.optString("objectId");
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  @Override
  public String toString() {
    try {
      return raw.toString(2);
    } catch (JSONException e) {
      return "Error parsing the raw remote object" + e.getMessage();
    }
  }
  
  public <T> T call(String function) throws Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", ""));

    cmd.put(
        "params",
        new JSONObject()
            .put("objectId", this.getId())
            .put("functionDeclaration",
                "(function(arg) { var res = this" + function + "; return res;})")
            .put("arguments", args).put("returnByValue", false));



    JSONObject response = protocol.sendCommand(cmd);
    return protocol.cast(response);

  }
}
