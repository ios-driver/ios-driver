package org.uiautomation.ios.mobileSafari.events;

import org.json.JSONObject;

public class RawEvent implements Event {

  private final JSONObject raw;
  private final long timestamp;

  public RawEvent(JSONObject raw) {
    this.raw = raw;
    timestamp = System.currentTimeMillis();
  }

  @Override
  public long getAge() {
    return System.currentTimeMillis() - timestamp;
  }
}
