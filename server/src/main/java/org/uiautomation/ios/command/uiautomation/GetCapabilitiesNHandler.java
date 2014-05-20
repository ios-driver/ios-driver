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
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.IOSServerManager;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.command.PostHandleDecorator;
import org.uiautomation.ios.command.UIAScriptHandler;
import org.uiautomation.ios.instruments.InstrumentsFailedToStartException;
import org.uiautomation.ios.utils.JSTemplate;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class GetCapabilitiesNHandler extends UIAScriptHandler {

  private static final Logger log = Logger.getLogger(GetCapabilitiesNHandler.class.getName());

  private static final JSTemplate template = new JSTemplate(
      "var json = UIAutomation.getCapabilities();" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,json)",
      "sessionId");

  public GetCapabilitiesNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
    setJS(template.generate(request.getSession()));
    addDecorator(new AddAllSupportedLocalesDecorator(getServer()));
  }


  @Override
  public synchronized Response handle() throws Exception {
    Response r = getSession().getCachedCapabilityResponse();
    if (r == null) {
      log.warning("Didn't know the capabilities. Should have known at registration.");
      throw new InstrumentsFailedToStartException(
          "The driver never recieved the capabilities or the "
          + "device. Most likely instruments crashed.");
    }
    return r;
  }


  class AddAllSupportedLocalesDecorator extends PostHandleDecorator {

    public AddAllSupportedLocalesDecorator(IOSServerManager driver) {
      super(driver);

    }

    @Override
    public synchronized void decorate(Response response) {

      if (getSession().hasBeenDecorated()) {
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
      response.setValue(o);
      getSession().setDecorated(true);
    }
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
