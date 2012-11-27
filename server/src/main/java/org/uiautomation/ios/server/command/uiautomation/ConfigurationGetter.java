package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseNativeCommandHandler;

public class ConfigurationGetter extends BaseNativeCommandHandler{

  public ConfigurationGetter(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
