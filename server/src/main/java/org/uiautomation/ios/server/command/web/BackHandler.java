/*
 * Copyright 2012 ios-driver committers.
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
package org.uiautomation.ios.server.command.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;

public class BackHandler extends BaseWebCommandHandler {

  private static final boolean nativeEvents = false;

  public BackHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    boolean useNativeEvents = getConfiguration("nativeEvents", nativeEvents);

    if (useNativeEvents) {
      // backNative();
    } else {
      backWeb();
    }
    getSession().getRemoteWebDriver().getContext().newContext();
    getSession().getRemoteWebDriver().waitForPageToLoad();
    Response resp = new Response();
    resp.setSessionId(getSession().getSessionId());
    resp.setStatus(0);
    resp.setValue(new JSONObject());
    return resp;
  }

  private void backWeb() throws Exception {
    getSession().getRemoteWebDriver().back();
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    JSONObject desc = new JSONObject();
    desc.put(
        "nativeEvents",
        "{boolean}, default to "
        + nativeEvents
        + ".true = UIAutomation native events will be used to enter click the back arrow (slow) , Web =  WebKit remote debugging will be used.Faster.");
    return desc;
  }

}
