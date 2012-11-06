package org.uiautomation.ios.webInspector.DOM;

import org.json.JSONException;
import org.json.JSONObject;

public class RemoteObject {

  private String id;
  
  public RemoteObject(JSONObject o) throws JSONException{
    System.out.println("creating RO from "+o.toString(2));
    this.id = o.optString("objectId");
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
