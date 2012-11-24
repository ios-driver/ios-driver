package org.uiautomation.ios.server.command.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class GetTextHandler extends BaseWebCommandHandler {

  public GetTextHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    int id = Integer.parseInt(getRequest().getVariableValue(":reference"));
    RemoteWebElement element = new RemoteWebElement(new NodeId(id), getSession());
    String text = element.getText();
    Response res = new Response();
    res.setSessionId(getSession().getSessionId());
    res.setStatus(0);
    res.setValue(text);
    return res;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
