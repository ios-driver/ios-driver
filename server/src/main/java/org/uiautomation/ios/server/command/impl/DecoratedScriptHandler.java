package org.uiautomation.ios.server.command.impl;

import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.instruments.SessionsManager;

public abstract class DecoratedScriptHandler extends DefaultUIAScriptHandler {

  private ResponseDecorator decorator;

  public DecoratedScriptHandler(SessionsManager instruments, WebDriverLikeRequest request,
      ResponseDecorator transformer) {
    super(instruments, request);
    this.decorator = transformer;
  }


  @Override
  public WebDriverLikeResponse handle() throws Exception {
    WebDriverLikeResponse response = super.handle();
    decorator.decorate(response);
    return response;
  }
}
