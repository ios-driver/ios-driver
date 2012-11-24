package org.uiautomation.ios.server.command.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class IsEqualHandler extends BaseWebCommandHandler {

  public IsEqualHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    int id = Integer.parseInt(getRequest().getVariableValue(":reference"));
    int other = Integer.parseInt(getRequest().getVariableValue(":other"));
    boolean equal = equal(id, other);
    Response res = new Response();
    res.setSessionId(getSession().getSessionId());
    res.setStatus(0);
    res.setValue(equal);
    return res;
  }

  private boolean equal(int id, int other) throws Exception {
    if (id == other) {
      return true;
    }
    RemoteWebElement rwe1 = new RemoteWebElement(new NodeId(id), getSession());
    RemoteWebElement rwe2 = new RemoteWebElement(new NodeId(other), getSession());
    return rwe1.equalsRemoteWebElement(rwe2);
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
