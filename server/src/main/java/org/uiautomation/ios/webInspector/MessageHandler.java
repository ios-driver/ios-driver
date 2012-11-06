package org.uiautomation.ios.webInspector;

import org.json.JSONObject;

public interface MessageHandler {

  public void handle(String msg);
  public JSONObject getResponse(int id);
}
