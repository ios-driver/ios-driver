package org.uiautomation.ios.server.command.web;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class ClearHandler extends BaseWebCommandHandler {

  public ClearHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
    int id = Integer.parseInt(getRequest().getVariableValue(":reference"));
    RemoteWebElement element = new RemoteWebElement(new NodeId(id), getSession());
    element.clear();
    return new WebDriverLikeResponse(getSession().getSessionId(), 0, new JSONObject());
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
