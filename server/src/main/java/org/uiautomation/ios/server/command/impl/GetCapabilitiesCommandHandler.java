package org.uiautomation.ios.server.command.impl;

import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.instruments.SessionsManager;

public class GetCapabilitiesCommandHandler extends UIAScriptHandler{

  private static final String capabilities =
      "var json = UIAutomation.getCapabilities();" +
      "UIAutomation.createJSONResponse(':sessionId',0,json)";
  
  public GetCapabilitiesCommandHandler(SessionsManager instruments, WebDriverLikeRequest request) {
    super(instruments, request);
    setJS(capabilities);
  }



}
