package org.uiautomation.ios.server.command.impl;

import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseCommandHandler;

public class ConfigurationGetter extends BaseCommandHandler{

  public ConfigurationGetter(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

}
