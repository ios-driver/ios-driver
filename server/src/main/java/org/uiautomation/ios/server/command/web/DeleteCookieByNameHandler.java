package org.uiautomation.ios.server.command.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;

import java.net.URL;

public class DeleteCookieByNameHandler extends BaseWebCommandHandler {


  public DeleteCookieByNameHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
  }


  @Override
  public Response handle() throws Exception {
    String name = getRequest().getVariableValue(":name");
    String url = getSession().getRemoteWebDriver().getCurrentUrl();
    URL u = new URL(url);
    String domain = u.getHost();

    getSession().getRemoteWebDriver().deleteCookie(name, domain);
    Response res = new Response();
    res.setSessionId(getSession().getSessionId());
    res.setStatus(0);
    res.setValue(new JSONObject());
    return res;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
