package org.uiautomation.ios.mobileSafari;

import org.json.JSONObject;
import org.uiautomation.ios.mobileSafari.events.Event;

public interface EventListener {
  public void onPageLoad();
  public void domHasChanged(Event event);
  public void frameDied(JSONObject message);
}
