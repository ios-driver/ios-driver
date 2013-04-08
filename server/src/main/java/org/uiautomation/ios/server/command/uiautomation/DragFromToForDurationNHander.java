package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class DragFromToForDurationNHander extends UIAScriptHandler {

  private static final String voidTemplate =
          "UIATarget.localTarget().dragFromToForDuration({x:fromX, y:fromY}, {x:toX, y:toY}, duration);" +
                  "UIAutomation.createJSONResponse(':sessionId',0,'')";



  public DragFromToForDurationNHander(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);

    JSONObject payload = request.getPayload();

    String js = voidTemplate
            .replace(":sessionId", request.getSession())
            .replace("fromX", payload.optString("fromX"))
            .replace("fromY", payload.optString("fromY"))
            .replace("toX", payload.optString("toX"))
            .replace("toY", payload.optString("toY"))
            .replace("duration", payload.optString("duration"));


    setJS(js);
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
