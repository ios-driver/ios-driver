package org.uiautomation.ios.server.command.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;

public class GetTitleHandler  extends BaseWebCommandHandler{

  public GetTitleHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
   String title = getSession().getWebInspector().getPageTitle();
   Response res = new Response();
   res.setSessionId(getSession().getSessionId());
   res.setStatus(0);
   res.setValue(title);
   return res;
  }
  
  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}