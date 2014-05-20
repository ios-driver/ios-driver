/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uiautomation.ios.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.IOSServerManager;

public class SetImplicitWaitTimeoutNHandler extends SetTimeoutNHandler {

  // TODO freynaud use config store.static is a bug.
  public static Integer TIMEOUT = 0;

  public SetImplicitWaitTimeoutNHandler(IOSServerManager driver, WebDriverLikeRequest request)
      throws Exception {
    super(driver, request);
  }

  protected String getVariableToCorrect() {
    return "ms";
  }

  @Override
  protected String getScript(IOSServerManager driver, WebDriverLikeRequest r) throws Exception {
    String type = "implicit";
    TIMEOUT = r.getPayload().getInt(kTimeoutName);
    int timeout = TIMEOUT / 1000;
    String sessionId = r.getSession();
    return generateScript(type, timeout, sessionId);
  }

  @Override
  public Response handle() throws Exception {
    return super.handle();
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}