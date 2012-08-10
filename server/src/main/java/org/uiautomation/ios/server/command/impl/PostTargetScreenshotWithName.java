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
import java.io.FileInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.instruments.SessionsManager;

public class PostTargetScreenshotWithName extends DecoratedScriptHandler {


  public PostTargetScreenshotWithName(final SessionsManager instruments,
      WebDriverLikeRequest request) {
    super(instruments, request, new ResponseDecorator() {
      @Override
      public void decorate(WebDriverLikeResponse original) {
        try {
          String path = instruments.getCurrentSessionOutputFolder() + "/Run 1/tmpScreenshot.png";
          File tmp = waitForFileToAppearOnDisk(path);
          JSONObject value = new JSONObject();
          value.put("64encoded", to64encodedString(tmp));
          tmp.delete();
          original.setValue(value);
        } catch (Exception e) {
          throw new IOSAutomationException("error decorating the response ", e);
        }

      }
    });
  }


  public static File waitForFileToAppearOnDisk(String path) throws Exception {
    File f = new File(path);
    int cpt = 0;
    while (!f.exists()) {
      Thread.sleep(250);
      cpt++;
      if (cpt > 5 * 4) {
        throw new Exception("timeout waiting for screenshot file to be written.");
      }
    }
    return f;
  }

  public static String to64encodedString(File from) throws Exception {
    FileInputStream is = new FileInputStream(from);
    byte[] img = IOUtils.toByteArray(is);
    String s = Base64.encodeBase64String(img);
    return s;
  }
}
