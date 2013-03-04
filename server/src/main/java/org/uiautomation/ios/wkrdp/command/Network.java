package org.uiautomation.ios.wkrdp.command;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;

public class Network {

  // unsupported
  public static JSONObject canClearBrowserCache() {
    try {
      JSONObject cmd = new JSONObject();
      cmd.put("method", "Network.canClearBrowserCache");
      return cmd;
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
  }

  public static JSONObject enable() {
    try {
      JSONObject cmd = new JSONObject();
      cmd.put("method", "Network.enable");
      return cmd;
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
  }
}
