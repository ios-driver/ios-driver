package org.uiautomation.ios.webInspector;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class NotificationFactory {


  private final List<String> notifications = new ArrayList<String>();

  public NotificationFactory() {
    notifications.add("DOM.setChildNodes");
  }

  public boolean isNotification(JSONObject message) {
    String method = message.optString("method");
    if (method != null) {
      return notifications.contains(method);
    }
    return false;
  }
  
  
  

}
