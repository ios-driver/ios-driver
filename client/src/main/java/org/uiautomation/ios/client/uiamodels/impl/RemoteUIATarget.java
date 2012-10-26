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
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.UIAModels.UIAAlert;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.UIAModels.UIATarget;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.exceptions.NoAlertOpenError;

public class RemoteUIATarget extends RemoteObject implements UIATarget {


  public RemoteUIATarget(RemoteUIADriver driver, String id) {
    super(driver, id);
  }



 


  @Override
  public UIARect getRect() {
    return getUIARect(WebDriverLikeCommand.TARGET_RECT);
  }





  @Override
  public RemoteUIAApplication getFrontMostApp() {
    // return (RemoteUIAApplication) getRemoteObject(WebDriverLikeCommand.FONT_MOST_APP);
    return new RemoteUIAApplication(getDriver(), "1");
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
  public void setDeviceOrientation(Orientation o) {
    JSONObject to = new JSONObject();
    try {
      to.put("orientation", o);
      execute(WebDriverLikeCommand.SET_ORIENTATION, to);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }



  @Override
  public UIAAlert getAlert() throws NoAlertOpenError {
    // if there is an alert, it's hardcoded to ref = 3. Not need to call to know that.
    RemoteUIAAlert alert = new RemoteUIAAlert(getDriver(), "3");
    // but need to access it once with a call just to check if an alert actually exists. If not,
    // throw a no alert exception
    alert.getName();
    return alert;
  }



}
