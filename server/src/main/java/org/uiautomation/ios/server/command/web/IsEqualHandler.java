package org.uiautomation.ios.server.command.web;

import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class IsEqualHandler extends BaseWebCommandHandler {

  public IsEqualHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
    int id = Integer.parseInt(getRequest().getVariableValue(":reference"));
    int other = Integer.parseInt(getRequest().getVariableValue(":other"));
    boolean equal = equal(id,other);
    return new WebDriverLikeResponse(getRequest().getSession(), 0, equal);
  }

  private boolean equal(int id, int other) throws Exception {
    if (id == other) {
      return true;
    }
    RemoteWebElement rwe1 = new RemoteWebElement(new NodeId(id), getSession());
    RemoteWebElement rwe2 = new RemoteWebElement(new NodeId(other), getSession());
    return rwe1.equalsRemoteWebElement(rwe2);
  }

}
