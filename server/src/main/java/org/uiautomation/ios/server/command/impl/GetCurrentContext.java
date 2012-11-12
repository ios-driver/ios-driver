package org.uiautomation.ios.server.command.impl;

import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseCommandHandler;

public class GetCurrentContext extends BaseCommandHandler {

  public GetCurrentContext(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);

  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
    String handle = getSession().getWindowHandle();
    return new WebDriverLikeResponse(getSession().getSessionId(), 0, handle);
  }

}
