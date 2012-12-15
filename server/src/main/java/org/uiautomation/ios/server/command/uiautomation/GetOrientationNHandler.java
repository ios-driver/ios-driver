package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.UIAScriptHandler;

/**
 * Created with IntelliJ IDEA. User: freynaud Date: 14/12/2012 Time: 23:45 To change this template
 * use File | Settings | File Templates.
 */
public class GetOrientationNHandler extends UIAScriptHandler {

  private static final String template = "var res = UIATarget.localTarget().getDeviceOrientation();"
                                         + "UIAutomation.createJSONResponse(':sessionId',0,res)";

  public GetOrientationNHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);

    JSONObject payload = request.getPayload();

    String js = template.replace(":sessionId", request.getSession());

    setJS(js);
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}

