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
import org.uiautomation.ios.server.command.PostHandleDecorator;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.command.impl.hack.TimeSpeeder;
import org.uiautomation.ios.server.instruments.SessionsManager;

public class GetTimeoutCommandHandler extends UIAScriptHandler {


  private static final String getTimeout = 
      "var timeout = UIAutomation.TIMEOUT_IN_SEC;"+
      "UIAutomation.createJSONResponse(':sessionId',0,timeout)";

  public GetTimeoutCommandHandler(SessionsManager context, WebDriverLikeRequest request)
      throws Exception {
    super(context, request);
    setJS(getTimeout);
    addDecorator(new CorrectTimeout(context));
  }

  class CorrectTimeout extends PostHandleDecorator {

    public CorrectTimeout(SessionsManager context) {
      super(context);
    }

    @Override
    public void decorate(WebDriverLikeResponse response) {
      try {
        Integer timeout = (Integer) response.getValue();
        float timeCorrection = TimeSpeeder.getInstance().getSecondDuration();
        float correctTimeout = timeout / timeCorrection;
        response.setValue((int)correctTimeout);
      } catch (Exception e) {
        throw new IOSAutomationException(
            "error correcting the timeout to take the timespeeder into account." + e.getMessage(),
            e);
      }


    }

  }

}
