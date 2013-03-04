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
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;
import org.uiautomation.ios.server.command.uiautomation.SetImplicitWaitTimeoutNHandler;
import org.uiautomation.ios.wkrdp.model.RemoteWebElement;
import org.uiautomation.ios.wkrdp.model.RemoteWebNativeBackedElement;

public class ClickHandler extends BaseWebCommandHandler {


  public ClickHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    String reference = getRequest().getVariableValue(":reference");
    RemoteWebElement element = getSession().getRemoteWebDriver().createElement(reference);

    boolean useNativeEvents = (Boolean) getConfiguration("nativeEvents");

    if (useNativeEvents && (element instanceof RemoteWebNativeBackedElement)) {
      ((RemoteWebNativeBackedElement) element).nativeClick();
      // native tapping in a webview delays triggering the event for 300ms (because iOS is looking to see if it's a gesture)
      // going to assume if you have implicit waits set you want this delay, if not you want it to return 'fast'
      if (getSession().getWorkingMode() == WorkingMode.Web &&
          SetImplicitWaitTimeoutNHandler.TIMEOUT != null &&
          SetImplicitWaitTimeoutNHandler.TIMEOUT > 0) {
        Thread.sleep(300);
      }
    } else {
      element.click();
    }

    Response resp = new Response();
    resp.setSessionId(getSession().getSessionId());
    resp.setStatus(0);
    resp.setValue(new JSONObject());
    return resp;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    JSONObject desc = new JSONObject();
    desc.put(
        "nativeEvents",
        "{boolean},"
        + "if true  UIAutomation native events will be used to generate the clicks , Web =  WebKit remote debugging will be used.Faster.");
    return desc;
  }

}
