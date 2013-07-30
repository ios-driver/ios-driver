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

import com.google.common.collect.ImmutableMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.application.IOSRunningApplication;
import org.uiautomation.ios.server.command.PostHandleDecorator;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.utils.InstrumentsGeneratedImage;
import org.uiautomation.ios.utils.JSONWireImage;
import org.uiautomation.ios.wkrdp.RemoteIOSWebDriver;

import java.io.File;
import java.util.List;
import java.util.Map;

public class LogElementTreeNHandler extends UIAScriptHandler {

  private static final String jsTemplate = "var root = UIAutomation.cache.get(':reference');"
                                           + "var result = root.tree(:attachScreenshot);"
                                           + "UIAutomation.createJSONResponse(':sessionId',0,result);";

  public LogElementTreeNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);

    String reference = "1";
    if (request.hasVariable(":reference")) {
      reference = request.getVariableValue(":reference");
    }

    addDecorator(new AttachScreenshotToLog(driver));
    addDecorator(new GetHTMLForWebView(driver));
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
      throw new WebDriverException("wrong params", e);
    }

    setJS(js);

  }

  class AddTranslationToLog extends PostHandleDecorator {

    public AddTranslationToLog(IOSServerManager driver) {
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

    private void addTranslation(Map<String, Object> node, IOSRunningApplication aut)
        throws JSONException {

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

    public AttachScreenshotToLog(IOSServerManager driver) {
      super(driver);
    }

    @Override
    public void decorate(Response response) {
      Map<String, Object> value = (Map<String, Object>) response.getValue();
      boolean screenshot;

      screenshot = (Boolean) value.get("screenshot");

      if (screenshot) {
        String path = getDriver().getSession(response.getSessionId()).getOutputFolder() + "/Run 1/"
                      + TakeScreenshotNHandler.SCREEN_NAME + ".png";
        File source = new File(path);
        System.out.println(response.getValue());
        JSONWireImage image = new InstrumentsGeneratedImage(source, Orientation.LANDSCAPE);
        try {
          String content64 = image.getAsBase64String();
          value.put("screenshot", ImmutableMap.of("64encoded", content64));
        } catch (Exception e) {
          throw new WebDriverException(
              "Error converting " + source.getAbsolutePath() + " to a 64 encoded string "
              + e.getMessage(), e);
        } finally {
          source.delete();
        }
      } else {
        value.put("screenshot", null);
      }
    }
  }

  class GetHTMLForWebView extends PostHandleDecorator {

    public GetHTMLForWebView(IOSServerManager driver) {
      super(driver);
    }

    @Override
    public void decorate(Response response) {
      Map<String, Object> value = (Map<String, Object>) response.getValue();
      Map<String, Object> tree = (Map<String, Object>) value.get("tree");

      Map<String, Object> webView = (Map<String, Object>) getWebView(tree);
      if (webView != null) {
        ServerSideSession session = getDriver().getSession(getRequest().getSession());
        RemoteIOSWebDriver inspector = session.getRemoteWebDriver();
        try {
          String rawHTML = inspector.getPageSource();
          webView.put("source", rawHTML);
        } catch (Exception e) {

          e.printStackTrace();
        }

      }

    }

    private Map<String, Object> getWebView(Map<String, Object> tree) {
      return parseNode(tree);
    }

    private Map<String, Object> parseNode(Map<String, Object> node) {
      String type = (String) node.get("type");
      if ("UIAWebView".equals(type)) {
        return node;
      } else {
        if (node.containsKey("children")) {
          List<Map<String, Object>> children = (List<Map<String, Object>>) node.get("children");
          for (Map<String, Object> child : children) {
            Map<String, Object> res = parseNode(child);
            if (res != null) {
              return res;
            }
          }
        }
      }
      return null;
    }

    private boolean containsAWebView(Response response) {

      return true;
    }
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
