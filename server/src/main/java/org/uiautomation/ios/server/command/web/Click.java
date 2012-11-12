package org.uiautomation.ios.server.command.web;

import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseCommandHandler;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class Click extends BaseCommandHandler {

  public Click(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
    System.out.println("web click element");
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {

    System.out.println("width = "+getSession().getWebInspector().getNativePageWidth());
    String id = getRequest().getVariableValue(":reference");
    RemoteWebElement element = new RemoteWebElement(id, getSession());
    element.click();
    return new WebDriverLikeResponse(getSession().getSessionId(), 0, new JSONObject());
  }

}
