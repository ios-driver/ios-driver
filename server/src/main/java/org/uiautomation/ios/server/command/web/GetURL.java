package org.uiautomation.ios.server.command.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;

public class GetURL extends BaseWebCommandHandler {

  public GetURL(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
    String url = getSession().getWebInspector().getPageURL();
    return new WebDriverLikeResponse(getSession().getSessionId(), 0, url);
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
