package org.uiautomation.ios.server.command.impl;

import org.uiautomation.ios.communication.WebDriverLikeResponse;

public interface ResponseDecorator {

  public void decorate(WebDriverLikeResponse original);
}
