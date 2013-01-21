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
package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseNativeCommandHandler;

public class SetCurrentContextNHandler extends BaseNativeCommandHandler {

  public SetCurrentContextNHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    String context = getRequest().getPayload().getString("name");
    WorkingMode mode = null;
    if (context.startsWith(WorkingMode.Web.toString())) {
      mode = WorkingMode.Web;
    } else {
      mode = WorkingMode.valueOf(context);
    }
    getSession().setMode(mode);

    if (context.startsWith(WorkingMode.Web + "_")) {
      /*List<UIAElement> views = getSession().getNativeDriver().findElements(new TypeCriteria(UIAWebView.class));
      if (views.isEmpty()) {
        throw new NoSuchWindowException("Cannot find a web view in the current app.");
      }*/
      if (getSession().getRemoteWebDriver().getWindowHandles().isEmpty()) {
        throw new NoSuchWindowException("Cannot find a web view in the current app.");
      }
      if (WorkingMode.Web.toString().equals(context)) {
        getSession().setMode(WorkingMode.Web);
      } else {
        String pageId = context.replace(WorkingMode.Web + "_", "");
        getSession().getRemoteWebDriver().switchTo(pageId);
      }

    }
    Response resp = new Response();
    resp.setSessionId(getSession().getSessionId());
    resp.setStatus(0);
    resp.setValue(new JSONObject());
    return resp;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
