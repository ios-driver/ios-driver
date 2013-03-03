package org.uiautomation.ios.wkrdp.command;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;

public class Runtime {


  public static JSONObject evaluate(String js) {
    try {
      JSONObject cmd = new JSONObject();
      cmd.put("method", "Runtime.evaluate")
          .put("params",
               new JSONObject()
                   .put("expression", js)
                   .put("objectGroup", "console")
                   .put("includeCommandLineAPI", true)
                   .put("doNotPauseOnExceptionsAndMuteConsole", true)
                   .put("returnByValue", false));

      return cmd;
    } catch (JSONException e) {
      throw new WebDriverException("json encoding", e);
    }

  }
  // {"method":"Runtime.evaluate","params":{"expression":"alert('tt123')","objectGroup":"console","includeCommandLineAPI":true,"doNotPauseOnExceptionsAndMuteConsole":true,"returnByValue":false},"id":57}
}
