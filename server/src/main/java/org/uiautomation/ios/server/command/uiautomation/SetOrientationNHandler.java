package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class SetOrientationNHandler  extends UIAScriptHandler {

  private static final String template = 
      "UIATarget.localTarget().setDeviceOrientation(:orientation);" +
      "UIAutomation.createJSONResponse(':sessionId',0,'')";
  
  public SetOrientationNHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
    
    JSONObject payload = request.getPayload();
    
    String js =  template
            .replace(":sessionId", request.getSession())
            .replace(":orientation", payload.optString("orientation"));
           
    setJS(js);
  }
  
  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
