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
package org.uiautomation.ios.client.uiamodels.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.UIAModels.UIATarget;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;

public class RemoteUIATarget extends RemoteObject implements UIATarget {


  public RemoteUIATarget(RemoteUIADriver driver, String id) {
    super(driver, id);
  }



  public String getModel() {
    return getObject(WebDriverLikeCommand.MODEL);
  }



  private JSONObject getJSONResult(WebDriverLikeCommand command) {

    String genericPath = command.path();
    String path =
        genericPath.replace(":sessionId", getSessionId()).replace(":reference", getReference());
    WebDriverLikeRequest request =
        new WebDriverLikeRequest(command.method(), path, new JSONObject());
    WebDriverLikeResponse response;
    try {
      response = getDriver().execute(request);
      return ((JSONObject) response.getValue());
    } catch (Exception e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      return null;
    }
  }

  @Override
  public UIARect getRect() {
    return getUIARect(WebDriverLikeCommand.TARGET_RECT);
  }

  @Override
  public String getName() {
    return getObject(WebDriverLikeCommand.TARGET_NAME);
  }

  @Override
  public String getSystemName() {
    return getObject(WebDriverLikeCommand.SYSTEM_NAME);
  }

  @Override
  public String getSystemVersion() {
    return getObject(WebDriverLikeCommand.SYSTEM_VERSION);
  }

  @Override
  public void takeScreenshot(String path) {
    try {

      // TODO freynaud use getObject ?
      JSONObject res = getJSONResult(WebDriverLikeCommand.SCREENSHOT_WITH_NAME);
      String content = res.getString("64encoded");
      createFileFrom64EncodedString(new File(path), content);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static  void createFileFrom64EncodedString(File f, String encoded64) throws IOException {
    byte[] img64 = Base64.decodeBase64(encoded64);
    FileOutputStream os = new FileOutputStream(f);
    os.write(img64);
    os.close();
  }

  @Override
  public RemoteUIAApplication getFrontMostApp() {
    return (RemoteUIAApplication) getRemoteObject(WebDriverLikeCommand.FONT_MOST_APP);
  }

  @Override
  public void tap(int x, int y) {
    try {
      JSONObject point = new JSONObject();
      point.put("x", x);
      point.put("y", y);
      execute(WebDriverLikeCommand.TARGET_TAP, point);
    } catch (JSONException e) {
      throw new RuntimeException("TODO", e);
    }
  }



  @Override
  public void setTimeout(int timeoutInSeconds) {
    JSONObject to = new JSONObject();
    try {
      to.put("timeout", timeoutInSeconds);
      execute(WebDriverLikeCommand.SET_TIMEOUT, to);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }



  @Override
  public int getTimeout() {
    return (Integer)getObject(WebDriverLikeCommand.GET_TIMEOUT);
  }



  @Override
  public String getLanguage() {
    return getObject(WebDriverLikeCommand.LANGUAGE);
  }



  @Override
  public String getLocale() {
    return getObject(WebDriverLikeCommand.LOCALE);
  }
}
