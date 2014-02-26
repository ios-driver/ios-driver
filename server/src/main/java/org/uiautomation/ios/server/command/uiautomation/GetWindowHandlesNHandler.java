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
package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.UIAModels.UIAWebView;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.InstrumentsBackedNativeIOSDriver;
import org.uiautomation.ios.server.command.BaseNativeCommandHandler;
import org.uiautomation.ios.server.instruments.NoInstrumentsImplementationAvailable;
import org.uiautomation.ios.wkrdp.message.WebkitPage;

import java.util.HashSet;
import java.util.Set;

public class GetWindowHandlesNHandler extends BaseNativeCommandHandler {

  public GetWindowHandlesNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    Set<String> handles = new HashSet<String>();
    handles.add(WorkingMode.Native.toString());

    InstrumentsBackedNativeIOSDriver nativeDriver = getNativeDriver();
    if ((nativeDriver.getInstruments() instanceof NoInstrumentsImplementationAvailable) ||
        (nativeDriver.findElements(new TypeCriteria(UIAWebView.class)).size() > 0)) {
      for (WebkitPage page : getWebDriver().getPages()) {
        handles.add(WorkingMode.Web + "_" + page.getPageId());
      }
    }

    Response resp = new Response();
    resp.setSessionId(getSession().getSessionId());
    resp.setStatus(0);
    resp.setValue(handles);
    return resp;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
