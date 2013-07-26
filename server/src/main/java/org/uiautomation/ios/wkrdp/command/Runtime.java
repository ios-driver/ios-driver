/*
 * Copyright 2012 ios-driver committers.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the Licence at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */
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
