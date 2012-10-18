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
package org.uiautomation.ios.server.command.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.FailedWebDriverLikeResponse;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.application.Localizable;
import org.uiautomation.ios.server.command.PostHandleDecorator;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class GetCapabilitiesCommandHandler extends UIAScriptHandler {

  private static final String capabilities = "var json = UIAutomation.getCapabilities();"
      + "UIAutomation.createJSONResponse(':sessionId',0,json)";

  public GetCapabilitiesCommandHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
    setJS(capabilities.replace(":sessionId", request.getSession()));
    addDecorator(new AddAllSupportedLocalesDecorator(getDriver()));
  }


  class AddAllSupportedLocalesDecorator extends PostHandleDecorator {

    public AddAllSupportedLocalesDecorator(IOSDriver driver) {
      super(driver);

    }

    @Override
    public void decorate(WebDriverLikeResponse response) {
      ServerSideSession session = getDriver().getSession(response.getSessionId());
      try {
        JSONObject o = new JSONObject((String) response.getValue());
        JSONArray array = new JSONArray();
        List<Localizable> ls = session.getApplication().getSupportedLanguages();

        for (Localizable l : ls) {
          array.put(l.getName());
        }
        o.put("supportedLocales", array);
        response.setValue(o.toString());
      } catch (JSONException e) {
        response = new FailedWebDriverLikeResponse(session.getSessionId(), e);
      }

    }
  }


}
