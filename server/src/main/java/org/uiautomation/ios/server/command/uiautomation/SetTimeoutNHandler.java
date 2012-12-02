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
import org.uiautomation.ios.server.command.PreHandleDecorator;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.utils.hack.TimeSpeeder;

public class SetTimeoutNHandler extends UIAScriptHandler {

  protected static final String setTimeout = "UIAutomation.setTimeout(':type',:timeout);"
      + "UIAutomation.createJSONResponse(':sessionId',0,'')";

  public SetTimeoutNHandler(IOSDriver driver, WebDriverLikeRequest request) throws Exception {
    super(driver, request);
    addDecorator(new CorrectTimeout(driver,this));
  }
  
  protected String getVariableToCorrect(){
    return "timeout";
  }

  protected String getScript(IOSDriver driver, WebDriverLikeRequest r) throws Exception {  
    int timeout = r.getPayload().getInt("timeout");
    String type = r.getPayload().getString("type");
    String s = setTimeout.replace(":timeout", String.format("%d", timeout));
    s = s.replace(":type", type);
    return s;
  }

  @Override
  public Response handle() throws Exception {
    setJS(getScript(getDriver(), getRequest()));
    return super.handle();
  }

  class CorrectTimeout extends PreHandleDecorator {

    private SetTimeoutNHandler handler;
    
    public CorrectTimeout(IOSDriver driver,SetTimeoutNHandler handler) {
      super(driver);
      this.handler = handler;
    }

    @Override
    public void decorate(WebDriverLikeRequest request) {
      try {
        int timeout = request.getPayload().getInt(handler.getVariableToCorrect());
        float timeCorrection = TimeSpeeder.getInstance().getSecondDuration();
        float correctTimeout = timeout * timeCorrection;
        request.getPayload().put(handler.getVariableToCorrect(), (int) correctTimeout);
      } catch (Exception e) {
        throw new WebDriverException("error correcting the timeout to take the timespeeder into account."
            + e.getMessage(), e);
      }

    }
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
