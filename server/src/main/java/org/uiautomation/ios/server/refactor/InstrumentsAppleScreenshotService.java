/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
 *
 * Licensed unde-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.uiautomation.ios.server.refactor;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.server.command.UIAScriptRequest;
import org.uiautomation.ios.server.command.UIAScriptResponse;
import org.uiautomation.ios.server.simulator.InstrumentsApple;
import org.uiautomation.ios.utils.InstrumentsGeneratedImage;
import org.uiautomation.ios.utils.JSONWireImage;

import java.io.File;

public class InstrumentsAppleScreenshotService implements TakeScreenshotService {

  private final InstrumentsApple instruments;
  private final String jsCommand;
  private final UIAScriptRequest command;
  private static final String SCREEN_NAME = "tmpScreenshot";
  private final String sessionId;
  private static final
  String
      jsTemplate =
      "UIATarget.localTarget().captureScreenWithName('" + SCREEN_NAME + "');"
      + "UIAutomation.createJSONResponse(':sessionId',0,UIATarget.localTarget().getDeviceOrientation());";


  public InstrumentsAppleScreenshotService(InstrumentsApple instruments,String sessionId) {
    this.instruments = instruments;
    this.sessionId = sessionId;
    this.jsCommand = jsTemplate.replace(":sessionId", sessionId);
    this.command = new UIAScriptRequest(jsCommand);
  }


  private Response executeScreenshotCommand(){
    UIAScriptResponse r = instruments.getChannel().executeCommand(command);
    return r.getResponse();
  }

  private String extractScreenshot(Response response){
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

    File folder = new File(instruments.getOuput(),"/Run 1/");
    File source = new File(folder, SCREEN_NAME + ".png");

    try {
      JSONWireImage image = new InstrumentsGeneratedImage(source, o);
      String content64 = image.getAsBase64String();
      return content64;
    } catch (Exception e) {
      throw new WebDriverException(
          "Error converting " + source.getAbsolutePath() + " to a 64 encoded string "
          + e.getMessage(), e);
    }
  }

  @Override
  public String getScreenshot() {
    Response r = executeScreenshotCommand();
    return extractScreenshot(r);
  }
}


