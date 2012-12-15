package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
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

