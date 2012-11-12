package org.uiautomation.ios.server.command.web;

import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseCommandHandler;

public class WebGetTitle  extends BaseCommandHandler{

  public WebGetTitle(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
   String title = getSession().getWebInspector().getPageTitle();
   return new WebDriverLikeResponse(getSession().getSessionId(), 0, title);
  }

}