package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class SetOrientationNHandler extends UIAScriptHandler {

  private static final String template = "UIATarget.localTarget().setDeviceOrientation(:orientation);"
      + "UIAutomation.createJSONResponse(':sessionId',0,'')";

  public SetOrientationNHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);

    JSONObject payload = request.getPayload();

    String js = template.replace(":sessionId", request.getSession()).replace(":orientation",
        payload.optString("orientation"));

    setJS(js);
  }

  @Override
  public Response handle() throws Exception {
    Response r = super.handle();
    // TODO fixes in the instruments script.
    // if the device goes from landcape right to left, and uses web command
    // right after, it's to fast and the webview isn't drawn correctly.Adding a
    // workaround for now, but will need to be investigated.
    Thread.sleep(500);
    return r;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
