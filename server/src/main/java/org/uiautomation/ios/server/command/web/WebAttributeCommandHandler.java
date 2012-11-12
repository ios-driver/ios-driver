package org.uiautomation.ios.server.command.web;

import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseCommandHandler;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class WebAttributeCommandHandler extends BaseCommandHandler{

  public WebAttributeCommandHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
    String id = getRequest().getVariableValue(":reference");
    String attributeName = getRequest().getVariableValue(":name");
    RemoteWebElement element = new RemoteWebElement(id, getSession());
    String value = element.getAttribute(attributeName);
    return new WebDriverLikeResponse(getSession().getSessionId(), 0, value);
  }

}
