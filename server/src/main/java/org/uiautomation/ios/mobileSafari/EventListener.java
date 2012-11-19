package org.uiautomation.ios.mobileSafari;

import org.json.JSONObject;

public interface EventListener {
  public void onPageLoad();
  public void frameNavigated(JSONObject message);
}
