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

import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.command.PostHandleDecorator;
import org.uiautomation.ios.server.instruments.SessionsManager;


public class TakeScreenshot extends DefaultUIAScriptHandler {

  public static final String SCREEN_NAME = "tmpScreenshot";

  public TakeScreenshot(SessionsManager context, WebDriverLikeRequest request) {
    super(context, request);
    addDecorator(new SendBack64EncodedStringDecorator(context));
  }

  class SendBack64EncodedStringDecorator extends PostHandleDecorator {

    public SendBack64EncodedStringDecorator(SessionsManager context) {
      super(context);
    }

    @Override
    public void decorate(WebDriverLikeResponse response) {

      String path = getContext().getCurrentSessionOutputFolder() + "/Run 1/" + SCREEN_NAME +".png";
      File source = new File(path);
      FileTo64EncodedStringUtils encoder = new FileTo64EncodedStringUtils(source);
      try {
        String content64 = encoder.encode();
        source.delete();
        JSONObject value = new JSONObject();
        value.put("64encoded", content64);
        response.setValue(value);
      } catch (Exception e) {
        throw new IOSAutomationException("Error converting " + source.getAbsolutePath()
            + " to a 64 encoded string " + e.getMessage(), e);
      } finally {
        source.delete();
      }

    }
  }
}
