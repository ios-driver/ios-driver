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

import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.command.PreHandleDecorator;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.instruments.IOSDriver;
import org.uiautomation.ios.server.utils.hack.TimeSpeeder;

public class SetTimeoutCommandHandler extends UIAScriptHandler {


  private static final String setTimeout = "UIAutomation.setTimeout(:timeout);"
      + "UIAutomation.createJSONResponse(':sessionId',0,'')";

  public SetTimeoutCommandHandler(IOSDriver driver, WebDriverLikeRequest request)
      throws Exception {
    super(driver, request);
    addDecorator(new CorrectTimeout(driver));
  }

  private String getScript(IOSDriver driver, WebDriverLikeRequest r) throws Exception {
    int timeout = r.getPayload().getInt("timeout");
    String s = setTimeout.replace(":timeout", String.format("%d", timeout));
    return s;
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
    setJS(getScript(getDriver(), getRequest()));
    return super.handle();
  }

  class CorrectTimeout extends PreHandleDecorator {

    public CorrectTimeout(IOSDriver driver) {
      super(driver);
    }

    @Override
    public void decorate(WebDriverLikeRequest request) {
      try {
        int timeout = request.getPayload().getInt("timeout");
        float timeCorrection = TimeSpeeder.getInstance().getSecondDuration();
        float correctTimeout = timeout * timeCorrection;
        request.getPayload().put("timeout", (int) correctTimeout);
      } catch (Exception e) {
        throw new IOSAutomationException(
            "error correcting the timeout to take the timespeeder into account." + e.getMessage(),
            e);
      }

    }
  }
}
