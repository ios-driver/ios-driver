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
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.PostHandleDecorator;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.utils.hack.TimeSpeeder;

public class GetTimeoutNHandler extends UIAScriptHandler {


  private static final String getTimeout = 
      "var timeout =UIAutomation.getTimeout(':type');"+
      "UIAutomation.createJSONResponse(':sessionId',0,timeout)";

  public GetTimeoutNHandler(IOSDriver driver, WebDriverLikeRequest request)
      throws Exception {
    super(driver, request);
    String type = request.getPayload().getString("type");
    setJS(getTimeout.replace(":type",type));
    addDecorator(new CorrectTimeout(driver));
  }

  class CorrectTimeout extends PostHandleDecorator {

    public CorrectTimeout(IOSDriver driver) {
      super(driver);
    }

    @Override
    public void decorate(Response response) {
      try {
        Integer timeout = (Integer) response.getValue();
        float timeCorrection = TimeSpeeder.getInstance().getSecondDuration();
        float correctTimeout = timeout / timeCorrection;
        response.setValue((int)correctTimeout);
      } catch (Exception e) {
        throw new WebDriverException(
            "error correcting the timeout to take the timespeeder into account." + e.getMessage(),
            e);
      }


    }

  }
  
  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
