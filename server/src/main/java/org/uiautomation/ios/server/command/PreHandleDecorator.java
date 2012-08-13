package org.uiautomation.ios.server.command;

import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.instruments.SessionsManager;


public abstract class PreHandleDecorator extends HandlerDecorator{

  public PreHandleDecorator(SessionsManager context) {
    super(context);
  }

  public abstract void decorate(WebDriverLikeRequest request);
}

