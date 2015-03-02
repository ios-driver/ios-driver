/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
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
package org.uiautomation.ios.command.uiautomation;

import com.google.common.collect.ImmutableMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.IOSServerManager;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.application.IOSRunningApplication;
import org.uiautomation.ios.command.PostHandleDecorator;
import org.uiautomation.ios.command.UIAScriptHandler;
import org.uiautomation.ios.instruments.InstrumentsAppleScreenshotService;
import org.uiautomation.ios.instruments.TakeScreenshotService;
import org.uiautomation.ios.utils.JSTemplate;
import org.uiautomation.ios.utils.InstrumentsGeneratedImage;
import org.uiautomation.ios.utils.JSONWireImage;
import org.uiautomation.ios.drivers.RemoteIOSWebDriver;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogElementTreeNHandler extends UIAScriptHandler {

  private static final Logger log = Logger.getLogger(LogElementTreeNHandler.class.getName());
  private static final JSTemplate template = new JSTemplate(
      "var root = UIAutomation.cache.get('%:reference$s');" +
      "var result = root.tree(%:attachScreenshot$b);" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,result);",
      "sessionId", "reference", "attachScreenshot");
  private static final String SCREEN_NAME = "tmpScreenshot";

  public LogElementTreeNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);

    String reference = "1";
    if (request.hasVariable(":reference")) {
      reference = request.getVariableValue(":reference");
    }

    addDecorator(new AttachScreenshotToLog(driver));
    addDecorator(new GetHTMLForWebView(driver));
    try {
      if (request.getPayload().has("translation") && request.getPayload().getBoolean("translation")) {
        addDecorator(new AddTranslationToLog(driver));
      }
    } catch (JSONException e) {
      log.log(Level.SEVERE,"format error",e);
    }

    final String js;
    try {
      boolean attachScreenshot = request.getPayload().has("attachScreenshot") &&
                                 request.getPayload().getBoolean("attachScreenshot");
      js = template.generate(request.getSession(), reference, attachScreenshot);
    } catch (JSONException e) {
      throw new WebDriverException("wrong params", e);
    }
    setJS(js);
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
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
        addTranslation(rootNode, getAUT());
      } catch (Exception e) {
        log.log(Level.SEVERE,"decoration: translation error",e);
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
      Long orientationCode = Long.parseLong(value.get("deviceOrientation").toString());

      Orientation o = Orientation.fromInterfaceOrientation(orientationCode.intValue());

      // TODO freynaud rethink that.
      if (screenshot) {
        TakeScreenshotService service = getNativeDriver().getScreenshotService();

        if (service instanceof InstrumentsAppleScreenshotService) {
          File source = ((InstrumentsAppleScreenshotService) service).getScreenshotFile();
          JSONWireImage image = new InstrumentsGeneratedImage(source, o);
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
          throw new RuntimeException("NI");
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

      Map<String, Object> webView = getWebView(tree);
      if (webView != null) {
        getDriver().getSession(getRequest().getSession());
        RemoteIOSWebDriver inspector = getWebDriver();
        try {
          String rawHTML = null;
          if (inspector == null) {
            log.severe("cannot decorate null webDriver");
            rawHTML = "<inspector is null>";
          } else {
            if (inspector.getCurrentPageID() != -1) {
              rawHTML = inspector.getPageSource();
            }
          }
          rawHTML = (rawHTML == null ? "<page source is null>" : rawHTML);
          webView.put("source", rawHTML);
        } catch (Exception e) {
          log.log(Level.SEVERE, "decoration error ", e);
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
}
