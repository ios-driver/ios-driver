package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseNativeCommandHandler;

public class GetCurrentContext extends BaseNativeCommandHandler {

  public GetCurrentContext(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);

  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
    WorkingMode mode = getSession().getMode();
    return new WebDriverLikeResponse(getSession().getSessionId(), 0, mode);
  }
  
  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
