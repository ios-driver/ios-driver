package org.uiautomation.ios.UIAModels.configuration;

import java.util.Map;

public interface CommandConfiguration {

  public void set(String key, Object value);

  public Object get(String key);

  public Map<String, Object> getAll();

  public Object opt(String key, Object defaultValue);

}
