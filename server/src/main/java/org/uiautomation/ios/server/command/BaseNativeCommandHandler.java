package org.uiautomation.ios.server.command;

import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;

public abstract class BaseNativeCommandHandler extends BaseCommandHandler{

  public BaseNativeCommandHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

 
  protected <T> T getConfiguration(String key) {
    return getConfiguration(key, (T) null);
  }
  
  
  protected  <T> T  getConfiguration(String key,T defaultValue) {
    T nativeSpecific = getConf(WorkingMode.Native+"." + key);
    if (nativeSpecific != null) {
      return nativeSpecific;
    } else {
      return super.getConf(key,defaultValue);
    }
  }

}
