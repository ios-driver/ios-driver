package org.uiautomation.ios.server.command.uiautomation;

import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.UIAScriptHandler;

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
        if (i < array.length() - 1) {
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
    if (value instanceof Double) {
      return value.toString();
    }
    if (value instanceof Double) {
      return value.toString();
    }
    if (value instanceof String) {
      return "'" + value.toString() + "'";
    }
    if (value instanceof Boolean) {
      return value.toString();
    }
    if (value instanceof JSONObject) {
      JSONObject v = (JSONObject) value;
      if (v.has("ELEMENT")) {
        Integer i = v.optInt("ELEMENT");
        return "UIAutomation.cache.get(" + i + ",false)";
      } else {
        return v.toString();
      }
    }
    if (value instanceof JSONArray) {
      StringBuilder builder = new StringBuilder();
      builder.append("[");
      JSONArray a = (JSONArray) value;
      for (int i = 0; i < a.length(); i++) {
        Object element = a.opt(i);
        builder.append(convert(element));
        if (i < a.length() - 1) {
          builder.append(",");
        }
      }
      builder.append("]");
      return builder.toString();
    }

    log.warning("Not implemented, JS argument is a " + value.getClass());
    throw new WebDriverException("Not implemented, JS argument is a " + value.getClass());
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {

    return noConfigDefined();
  }
}