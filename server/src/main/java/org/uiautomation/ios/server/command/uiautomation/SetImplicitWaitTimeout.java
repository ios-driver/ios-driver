package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;

public class SetImplicitWaitTimeout extends SetTimeoutCommandHandler {

  public SetImplicitWaitTimeout(IOSDriver driver, WebDriverLikeRequest request) throws Exception {
    super(driver, request);
  }

  protected String getVariableToCorrect(){
    return "ms";
  }
  
  protected String getScript(IOSDriver driver, WebDriverLikeRequest r) throws Exception {  
    int timeout = r.getPayload().getInt("ms");
    int timeoutInSec = timeout/1000;
    String type = "implicit";
    String s = setTimeout.replace(":timeout", String.format("%d", timeoutInSec));
    s = s.replace(":type", type);
    return s;
  }
  
  @Override
  public Response handle() throws Exception {
    return super.handle();
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
