package org.uiautomation.ios.webInspector.DOM;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.mobileSafari.DebugProtocol;
import org.uiautomation.ios.server.ServerSideSession;

public class RemoteObject {

  private final String id;
  private final ServerSideSession session;
  private final DebugProtocol protocol;

  public RemoteObject(String id, ServerSideSession session) throws JSONException {
    this.session = session;
    this.protocol = session.getWebInspector().getProtocol();
    this.id = id;
  }

  public DebugProtocol getProtocol() {
    return protocol;
  }



  public String getId() {
    return id;
  }

  protected ServerSideSession getSession(){
    return session;
  }
  


  @Override
  public String toString() {
    return id;
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
