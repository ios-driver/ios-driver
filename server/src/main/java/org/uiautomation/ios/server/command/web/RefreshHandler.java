package org.uiautomation.ios.server.command.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;

public class RefreshHandler extends BaseWebCommandHandler {

  public RefreshHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    getSession().getContext().getDOMContext().reset();
    getSession().getWebInspector().refresh();
    getSession().getWebInspector().waitForPageToLoad();
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
