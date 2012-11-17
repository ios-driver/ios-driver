package org.uiautomation.ios.server.command;

import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;

public abstract class BaseWebCommandHandler extends BaseCommandHandler {

  public BaseWebCommandHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  protected  <T> T  getConfiguration(String key) {
    T webSpecific = super.getConfiguration(WorkingMode.Web+"." + key);
    if (webSpecific != null) {
      return webSpecific;
    } else {
      return super.getConfiguration(key);
    }
  }
}
