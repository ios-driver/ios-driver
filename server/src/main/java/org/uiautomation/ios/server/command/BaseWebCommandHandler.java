package org.uiautomation.ios.server.command;

import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;

public abstract class BaseWebCommandHandler extends BaseCommandHandler {

  public BaseWebCommandHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

 
  protected <T> T getConfiguration(String key) {
    return getConfiguration(key, (T) null);
  }

  
  protected <T> T getConfiguration(String key, T defaultValue) {
    T webSpecific = getConf(WorkingMode.Web + "." + key);
    if (webSpecific != null) {
      return webSpecific;
    } else {
      return getConf(key, defaultValue);
    }
  }
}
