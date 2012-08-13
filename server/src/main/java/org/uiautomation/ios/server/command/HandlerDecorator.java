package org.uiautomation.ios.server.command;

import org.uiautomation.ios.server.instruments.SessionsManager;



public abstract class HandlerDecorator {

  private final SessionsManager context;

  public HandlerDecorator(SessionsManager context) {
    this.context = context;
  }

  protected SessionsManager getContext() {
    return context;
  }

}
