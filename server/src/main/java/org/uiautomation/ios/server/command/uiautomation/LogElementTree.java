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
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.command.PostHandleDecorator;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.utils.FileTo64EncodedStringUtils;

import com.google.common.collect.ImmutableMap;

public class LogElementTree extends UIAScriptHandler {

  private static final String jsTemplate = "var root = UIAutomation.cache.get(':reference');"
      + "var result = root.tree(:attachScreenshot);" + "UIAutomation.createJSONResponse(':sessionId',0,result);";

  public LogElementTree(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);

    String reference = "1";
    if (request.hasVariable(":reference")) {
      reference = request.getVariableValue(":reference");
    }

    addDecorator(new AttachScreenshotToLog(driver));
    try {
      if (request.getPayload().getBoolean("translation")) {
        addDecorator(new AddTranslationToLog(driver));
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }

    String js;
    try {
      js = jsTemplate.replace(":sessionId", request.getSession())
          .replace(":attachScreenshot", "" + request.getPayload().getBoolean("attachScreenshot"))
          .replace(":reference", reference);
    } catch (JSONException e) {
      throw new IOSAutomationException("wrong params", e);
    }

    setJS(js);

  }

  class AddTranslationToLog extends PostHandleDecorator {

    public AddTranslationToLog(IOSDriver driver) {
      super(driver);
    }

    @Override
    public void decorate(Response response) {
      Map<String, Object> value = (Map<String, Object>) response.getValue();
      try {
        Map<String, Object> rootNode = (Map<String, Object>) value.get("tree");
        addTranslation(rootNode, getDriver().getSession(response.getSessionId()).getApplication());
      } catch (Exception e) {
        e.printStackTrace();
      }

    }

    private void addTranslation(Map<String, Object> node, IOSApplication aut) throws JSONException {

      node.put("l10n", aut.getTranslations((String) node.get("name")));
      List<Map<String, Object>> children = (List<Map<String, Object>>) node.get("children");
      if (children != null && children.size() != 0) {
        for (Map<String, Object> child : children) {
          addTranslation(child, aut);
        }
      }
    }

  }

  class AttachScreenshotToLog extends PostHandleDecorator {

    public AttachScreenshotToLog(IOSDriver driver) {
      super(driver);
    }

    @Override
    public void decorate(Response response) {
      Map<String, Object> value = (Map<String, Object>) response.getValue();
      boolean screenshot;

      screenshot = (Boolean) value.get("screenshot");

      if (screenshot) {
        String path = getDriver().getSession(response.getSessionId()).getOutputFolder() + "/Run 1/"
            + TakeScreenshot.SCREEN_NAME + ".png";
        File source = new File(path);
        FileTo64EncodedStringUtils encoder = new FileTo64EncodedStringUtils(source);
        try {
          String content64 = encoder.encode();
          value.put("screenshot", ImmutableMap.of("64encoded", content64));
        } catch (Exception e) {
          throw new IOSAutomationException("Error converting " + source.getAbsolutePath() + " to a 64 encoded string "
              + e.getMessage(), e);
        } finally {
          source.delete();
        }
      } else {
        value.put("screenshot", null);
      }
    }
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
