package org.uiautomation.ios.mobileSafari.events;

import org.json.JSONException;
import org.json.JSONObject;

public class ChildNodeRemoved extends NodeEvent {

  public ChildNodeRemoved(JSONObject message) throws JSONException {
    super(message);
  }

}
