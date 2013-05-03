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
import org.uiautomation.ios.utils.FileTo64EncodedStringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TakeScreenshotNHandler extends UIAScriptHandler {

  public static final String SCREEN_NAME = "tmpScreenshot";
  private static final
  String
      jsTemplate =
      "UIATarget.localTarget().captureScreenWithName('" + SCREEN_NAME + "');"
      + "UIAutomation.createJSONResponse(':sessionId',0,'ok');";

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

      String
          path =
          getDriver().getSession(getRequest().getSession()).getOutputFolder() + "/Run 1/"
          + SCREEN_NAME
          + ".png";


      int rotateDegrees = 0;
      Orientation orientation = getDriver().getSession(getRequest().getSession()).getNativeDriver().getNativeOrientation();

      switch(orientation)
      {
        case LANDSCAPE:
          rotateDegrees = 270;
          break;
        case UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT:
          rotateDegrees = 90;
          break;
        case UIA_DEVICE_ORIENTATION_PORTRAIT_UPSIDEDOWN:
          rotateDegrees = 180;
          break;
      }

      if (rotateDegrees != 0) {
        try {
          final File source = new File(path);

          // It's possible the screenshot hasn't saved to disk yet, so we'll wait up to 5 seconds for that to happen.
          int waitTimeInMs = 5000;
          while (! source.exists() && (waitTimeInMs >= 0)) {
            try {
              Thread.sleep(100);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
            waitTimeInMs -= 100;
          }

          final BufferedImage originalImage = ImageIO.read(source);

          final BufferedImage rotated;
          if (rotateDegrees == 90 || rotateDegrees == 270) {
            rotated = new BufferedImage(originalImage.getHeight(), originalImage.getWidth(), originalImage.getType());
          } else {
            rotated = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
          }

          // Rotate the image and then move it back up to the origin through a translation call, since it'll pivot around
          // the center point which will cause non-square images to offset by the different in height and width.
          final Graphics2D graphics = rotated.createGraphics();
          graphics.rotate(Math.toRadians(rotateDegrees), rotated.getWidth() / 2, rotated.getHeight() / 2);
          graphics.translate((rotated.getWidth() - originalImage.getWidth()) / 2, (rotated.getHeight() - originalImage.getHeight()) / 2);
          graphics.drawImage(originalImage, 0, 0, originalImage.getWidth(), originalImage.getHeight(), null);

          // Overwrite the original screenshot file so the rest of the screenshot handler functions as if the image were never rotated.
          final FileOutputStream out = new FileOutputStream(path);
          ImageIO.write(rotated, "PNG", out);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

      File source = new File(path);
      FileTo64EncodedStringUtils encoder = new FileTo64EncodedStringUtils(source);
      try {
        String content64 = encoder.encode();
        source.delete();
        // JSONObject value = new JSONObject();
        // value.put("64encoded", content64);
        response.setValue(content64);
      } catch (Exception e) {
        throw new WebDriverException(
            "Error converting " + source.getAbsolutePath() + " to a 64 encoded string "
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
