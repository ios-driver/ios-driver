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
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.command.PostHandleDecorator;
import org.uiautomation.ios.server.command.UIAScriptHandler;

import java.util.List;
import java.util.Map;

public class GetCapabilitiesNHandler extends UIAScriptHandler {

  // TODO freynaud
  private static Response cachedResponse = null;
  private static boolean hasBeenDecorated = false;

  public static void reset() {
    cachedResponse = null;
    hasBeenDecorated = false;
  }

  private static final String capabilities = "var json = UIAutomation.getCapabilities();"
                                             + "UIAutomation.createJSONResponse(':sessionId',0,json)";

  public GetCapabilitiesNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
    setJS(capabilities.replace(":sessionId", request.getSession()));
    addDecorator(new AddAllSupportedLocalesDecorator(getDriver()));
  }

  @Override
  public Response handle() throws Exception {
    if (cachedResponse == null) {
      cachedResponse = super.handle();
    }
    return cachedResponse;
  }

  public static void setCachedResponse(Response r) {
    cachedResponse = r;
  }

  class AddAllSupportedLocalesDecorator extends PostHandleDecorator {

    public AddAllSupportedLocalesDecorator(IOSServerManager driver) {
      super(driver);

    }

    @Override
    public void decorate(Response response) {
      if (hasBeenDecorated) {
        return;
      }
      ServerSideSession session = getDriver().getSession(response.getSessionId());
      Map<String, Object> o = (Map<String, Object>) response.getValue();
      List<String> ls = session.getApplication().getSupportedLanguagesCodes();

      o.put("supportedLocales", ls);
      o.put("takesScreenshot", true);
      o.put(IOSCapabilities.CONFIGURABLE, true);
      o.put(IOSCapabilities.ELEMENT_TREE, true);
      o.put(IOSCapabilities.IOS_SEARCH_CONTEXT, true);
      o.put(IOSCapabilities.IOS_TOUCH_SCREEN, true);

      o.put("rotatable", true);
      o.put("locationContextEnabled", true);

      o.put("browserName", session.getCapabilities().getBundleName());
      o.put("browserVersion", session.getApplication().getCapabilities().getBundleVersion());

      o.put("platform", "IOS");
      o.put("platformName", "IOS");
      o.put("platformVersion", session.getCapabilities().getSDKVersion());

      o.put("javascriptEnabled", true);
      o.put("cssSelectors", true);
      o.put("takesElementScreenshot", false);
      
      o.put(IOSCapabilities.SIMULATOR, true);
      o.put(IOSCapabilities.DEVICE, session.getCapabilities().getDevice());
      o.put(IOSCapabilities.VARIATION, session.getCapabilities().getDeviceVariation());
      // TODO freynaud fill in device here ?
      response.setValue(o);
      hasBeenDecorated = true;

    }
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
