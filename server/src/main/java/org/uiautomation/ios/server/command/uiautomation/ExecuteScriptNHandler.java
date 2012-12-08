package org.uiautomation.ios.server.command.uiautomation;

import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.instruments.InstrumentsManager;

public class ExecuteScriptNHandler extends UIAScriptHandler {

  private static final Logger log = Logger.getLogger(ExecuteScriptNHandler.class.getName());

  private static final String template = "UIAutomation.createJSONResponse(':sessionId',0,:function)";

  public ExecuteScriptNHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
    try {
      String script = getRequest().getPayload().getString("script");
      JSONArray args = getRequest().getPayload().getJSONArray("args");

      String arguments = buildArguments(args);

      String f = "(function() { " + script + "})";
      String js = template.replace(":sessionId", request.getSession()).replace(":function", f + "(" + arguments + ")");
      setJS(js);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private String buildArguments(JSONArray array) {
    if (array.length() == 0) {
      return "";
    } else {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < array.length(); i++) {
        Object value = array.opt(i);
        builder.append(convert(value));
        if (i < array.length()-1) {
          builder.append(",");
        }

      }
      return builder.toString();
    }
  }

  private String convert(Object value) {
    if (value instanceof Integer) {
      return value.toString();
    }
    log.warning("Not implemented, JS argument is a " + value.getClass());
    return "TODO";
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