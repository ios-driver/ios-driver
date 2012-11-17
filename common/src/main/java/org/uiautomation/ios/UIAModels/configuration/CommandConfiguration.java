package org.uiautomation.ios.UIAModels.configuration;

public interface CommandConfiguration {

  public void set(String key,Object value);
  public Object get(String key);
  public Object opt(String key,Object defaultValue);
  
}
