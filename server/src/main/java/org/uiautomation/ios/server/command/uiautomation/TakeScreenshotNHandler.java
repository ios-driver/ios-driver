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

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.PostHandleDecorator;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.utils.FileTo64EncodedStringUtils;

public class TakeScreenshotNHandler extends UIAScriptHandler {

  public static final String SCREEN_NAME = "tmpScreenshot";
  private static final String jsTemplate = "UIATarget.localTarget().captureScreenWithName('" + SCREEN_NAME + "');"
      + "UIAutomation.createJSONResponse(':sessionId',0,'ok');";

  public TakeScreenshotNHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
    setJS(jsTemplate.replace(":sessionId", request.getSession()));
    addDecorator(new SendBack64EncodedStringDecorator(driver));
  }

  class SendBack64EncodedStringDecorator extends PostHandleDecorator {

    public SendBack64EncodedStringDecorator(IOSDriver driver) {
      super(driver);
    }

    @Override
    public void decorate(Response response) {

      String path = getDriver().getSession(getRequest().getSession()).getOutputFolder() + "/Run 1/" + SCREEN_NAME
          + ".png";
      File source = new File(path);
      FileTo64EncodedStringUtils encoder = new FileTo64EncodedStringUtils(source);
      try {
        String content64 = encoder.encode();
        source.delete();
        // JSONObject value = new JSONObject();
        // value.put("64encoded", content64);
        response.setValue(content64);
      } catch (Exception e) {
        throw new WebDriverException("Error converting " + source.getAbsolutePath() + " to a 64 encoded string "
            + e.getMessage(), e);
      } finally {
        source.delete();
      }

    }
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
