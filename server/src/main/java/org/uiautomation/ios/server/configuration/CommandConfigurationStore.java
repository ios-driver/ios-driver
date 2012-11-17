package org.uiautomation.ios.server.configuration;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.configuration.CommandConfiguration;

public class CommandConfigurationStore implements CommandConfiguration {

  private final JSONObject config = new JSONObject();

  @Override
  public void set(String key, Object value) {
    try {
      config.put(key, value);
    } catch (JSONException e) {
      e.printStackTrace();
    }

  }

  @Override
  public Object get(String key) {
    return config.opt(key);
  }

  @Override
  public Object opt(String key, Object defaultValue) {
    Object res = get(key);
    if (res == null){
      res = defaultValue;
    }
    return res;
  }

}
