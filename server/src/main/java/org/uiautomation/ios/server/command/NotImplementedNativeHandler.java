package org.uiautomation.ios.server.command;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.IOSDriver;

public class NotImplementedNativeHandler extends BaseNativeCommandHandler {

  public NotImplementedNativeHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    throw new IOSAutomationException("not implemented");
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
