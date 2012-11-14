package org.uiautomation.ios.server.command.web;

import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseCommandHandler;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class GetTextCommandHandler extends BaseCommandHandler {

  public GetTextCommandHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
    String id = getRequest().getVariableValue(":reference");
    RemoteWebElement element = new RemoteWebElement(id, getSession());
    String text = element.getText();
    return new WebDriverLikeResponse(getSession().getSessionId(), 0,text);
  }

}
