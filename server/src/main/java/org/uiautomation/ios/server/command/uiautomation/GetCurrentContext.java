package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseNativeCommandHandler;

public class GetCurrentContext extends BaseNativeCommandHandler {

  public GetCurrentContext(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);

  }

  @Override
  public Response handle() throws Exception {
    WorkingMode mode = getSession().getMode();
    Response resp = new Response();
    resp.setSessionId(getSession().getSessionId());
    resp.setStatus(0);
    resp.setValue(mode);
    return resp;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
