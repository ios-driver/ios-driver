package org.uiautomation.ios.server.command.web;


import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;

import java.util.List;

public class GetCookiesHandler extends BaseWebCommandHandler {


  public GetCookiesHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
  }


  @Override
  public Response handle() throws Exception {

    List<Cookie> cookies = getSession().getRemoteWebDriver().getCookies();
    Response res = new Response();
    res.setSessionId(getSession().getSessionId());
    res.setStatus(0);
    res.setValue(cookies);
    return res;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
