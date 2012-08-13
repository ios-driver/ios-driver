package org.uiautomation.ios.server.command;

import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.instruments.SessionsManager;

public abstract class PostHandleDecorator extends HandlerDecorator{

  public PostHandleDecorator(SessionsManager context) {
    super(context);
  }

  public abstract void decorate(WebDriverLikeResponse response);

}
