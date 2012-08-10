package org.uiautomation.ios.server.command.impl;

import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.instruments.SessionsManager;

public class TranslationDecoratorScriptHandler extends DecoratedScriptHandler {

  public TranslationDecoratorScriptHandler(SessionsManager instruments, WebDriverLikeRequest request) {
    super(instruments, request,new TranslationDecorator(instruments));
  }
}
