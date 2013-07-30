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
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.PostHandleDecorator;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.utils.InstrumentsGeneratedImage;
import org.uiautomation.ios.utils.JSONWireImage;

import java.io.File;

public class TakeScreenshotNHandler extends UIAScriptHandler {

  public static final String SCREEN_NAME = "tmpScreenshot";
  private static final
  String
      jsTemplate =
      "UIATarget.localTarget().captureScreenWithName('" + SCREEN_NAME + "');"
      + "UIAutomation.createJSONResponse(':sessionId',0,UIATarget.localTarget().getDeviceOrientation());";

  public TakeScreenshotNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
    setJS(jsTemplate.replace(":sessionId", request.getSession()));
    addDecorator(new SendBack64EncodedStringDecorator(driver));
  }

  class SendBack64EncodedStringDecorator extends PostHandleDecorator {

    public SendBack64EncodedStringDecorator(IOSServerManager driver) {
      super(driver);
    }

    @Override
    public void decorate(Response response) {

      String orientation = response.getValue().toString();
      Orientation o;
      try {
        o = Orientation.valueOf(orientation);
      } catch (IllegalArgumentException e) {
        throw new WebDriverException(
            "the response for the screenshot command should return the orientation the device was on"
            + " when the screenshot was take.The orientation returned : "
            + orientation + " isn't a valid orientation.");
      }

      String session = getRequest().getSession();
      String folder = getDriver().getSession(session).getOutputFolder() + "/Run 1/";

      File source = new File(folder, SCREEN_NAME + ".png");

      try {
        JSONWireImage image = new InstrumentsGeneratedImage(source, o);
        String content64 = image.getAsBase64String();
        response.setValue(content64);
      } catch (Exception e) {
        throw new WebDriverException(
            "Error converting " + source.getAbsolutePath() + " to a 64 encoded string "
            + e.getMessage(), e);
      }

    }
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
