package org.uiautomation.ios.mobileSafari;

import org.json.JSONObject;

public interface EventListener {
  public void onPageLoad();
  public void domHasChanged(JSONObject message);
  public void frameDied(JSONObject message);
}
