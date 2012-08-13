package org.uiautomation.ios.server.command;

import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.instruments.SessionsManager;

public class NotImplementedHandler extends BaseCommandHandler {

  public NotImplementedHandler(SessionsManager sessionsManager, WebDriverLikeRequest request) {
    super(sessionsManager, request);
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
    throw new IOSAutomationException("not implemented");
  }



}
