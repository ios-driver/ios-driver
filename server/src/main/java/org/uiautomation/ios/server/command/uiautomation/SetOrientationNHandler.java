package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class SetOrientationNHandler extends UIAScriptHandler {

  private static final
  String
      template =
      "UIATarget.localTarget().setDeviceOrientation(:orientation);"
      + "UIAutomation.createJSONResponse(':sessionId',0,'')";

  public SetOrientationNHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);

    JSONObject payload = request.getPayload();
    String orientation = payload.optString("orientation");
    Orientation o = Orientation.valueOf(orientation);

    String js = template.replace(":sessionId", request.getSession()).replace(":orientation",
                                                                             o.instrumentsValue());

    setJS(js);
  }

  @Override
  public Response handle() throws Exception {
    Response r = super.handle();
    return r;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
