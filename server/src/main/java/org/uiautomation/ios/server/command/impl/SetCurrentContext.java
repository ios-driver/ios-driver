package org.uiautomation.ios.server.command.impl;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseNativeCommandHandler;

public class SetCurrentContext extends BaseNativeCommandHandler {

  public SetCurrentContext(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
    String context = getRequest().getPayload().getString("name");
    WorkingMode mode = WorkingMode.valueOf(context);
    getSession().setMode(mode);
    return new WebDriverLikeResponse(getRequest().getSession(), 0, new JSONObject());
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
