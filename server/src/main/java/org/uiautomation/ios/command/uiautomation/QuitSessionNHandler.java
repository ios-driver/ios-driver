package org.uiautomation.ios.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.IOSServerManager;
import org.uiautomation.ios.command.BaseNativeCommandHandler;
import org.uiautomation.ios.communication.WebDriverLikeRequest;


public class QuitSessionNHandler extends BaseNativeCommandHandler{

  public QuitSessionNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

  @Override
  public Response handle() throws Exception {
    getSession().stop();
    return  createResponse(new JSONObject());
  }
}
