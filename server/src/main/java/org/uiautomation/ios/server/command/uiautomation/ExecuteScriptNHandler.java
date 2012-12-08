package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class ExecuteScriptNHandler extends UIAScriptHandler {

  private static final String template = "UIAutomation.createJSONResponse(':sessionId',0,:function)";

  public ExecuteScriptNHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
    try {
      String script = getRequest().getPayload().getString("script");
      JSONArray args = getRequest().getPayload().getJSONArray("args");
      String f = "(function() { " + script + "})";
      String js = template.replace(":sessionId", request.getSession()).replace(":function", f + "()");
      setJS(js);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Response handle() throws Exception {
    Response r = super.handle();
    System.out.println(r);
    return r;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {

    return noConfigDefined();
  }
}