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

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.command.PostHandleDecorator;
import org.uiautomation.ios.server.utils.FileTo64EncodedStringUtils;

public class LogElementTree extends DefaultUIAScriptHandler {

  public LogElementTree(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
    addDecorator(new AttachScreenshotToLog(driver));
    try {
      if (request.getPayload().getBoolean("translation")){
        addDecorator(new AddTranslationToLog(driver));
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
   
  }


  class AddTranslationToLog extends PostHandleDecorator {

    public AddTranslationToLog(IOSDriver driver) {
      super(driver);
    }

    @Override
    public void decorate(WebDriverLikeResponse response) {
      JSONObject value = (JSONObject) response.getValue();
      try {
        JSONObject rootNode = value.getJSONObject("tree");
        addTranslation(rootNode, getDriver().getSession(response.getSessionId()).getApplication());
      } catch (Exception e) {
        e.printStackTrace();
      }


    }

    private void addTranslation(JSONObject node, IOSApplication aut) throws JSONException {

      node.put("l10n", aut.getTranslations(node.getString("name")));
      JSONArray children = node.optJSONArray("children");

      if (children != null && children.length() != 0) {
        for (int i = 0; i < children.length(); i++) {
          JSONObject child = children.getJSONObject(i);
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
    public void decorate(WebDriverLikeResponse response) {
      JSONObject value = (JSONObject) response.getValue();
      boolean screenshot;
      try {
        screenshot = value.getBoolean("screenshot");
      } catch (JSONException e1) {
        screenshot = false;
      }

      if (screenshot) {
        String path =
            getDriver().getSession(response.getSessionId()).getOutputFolder() + "/Run 1/" + TakeScreenshot.SCREEN_NAME
                + ".png";
        File source = new File(path);
        FileTo64EncodedStringUtils encoder = new FileTo64EncodedStringUtils(source);
        try {
          String content64 = encoder.encode();
          JSONObject ss = new JSONObject();
          ss.put("64encoded", content64);
          value.put("screenshot", ss);
        } catch (Exception e) {
          throw new IOSAutomationException("Error converting " + source.getAbsolutePath()
              + " to a 64 encoded string " + e.getMessage(), e);
        } finally {
          source.delete();
        }
      } else {
        try {
          value.put("screenshot", JSONObject.NULL);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
  }

}
