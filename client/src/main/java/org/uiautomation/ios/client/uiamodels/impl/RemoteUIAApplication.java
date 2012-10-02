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

import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.UIAApplication;
import org.uiautomation.ios.UIAModels.UIAElementArray;
import org.uiautomation.ios.UIAModels.UIAKeyboard;
import org.uiautomation.ios.UIAModels.UIAWindow;
import org.uiautomation.ios.communication.WebDriverLikeCommand;

public class RemoteUIAApplication extends RemoteObject implements UIAApplication {

  public RemoteUIAApplication(RemoteUIADriver driver, String reference) {
    super(driver, reference);
  }

  @SuppressWarnings("unchecked")
  public UIAElementArray<UIAWindow> getWindows() {
    return (UIAElementArray<UIAWindow>) getRemoteObject(WebDriverLikeCommand.WINDOWS,
        new JSONObject());
  }

  public RemoteUIAWindow getMainWindow() {
    //return (RemoteUIAWindow) getRemoteObject(WebDriverLikeCommand.MAIN_WINDOW);
    return new RemoteUIAWindow(getDriver(), "0");
  }

  @Override
  public UIAKeyboard getKeyboard() {
    return (UIAKeyboard) getRemoteObject(WebDriverLikeCommand.KEYBOARD);
  }

  @Override
  public String getBundleId() {
    return getObject(WebDriverLikeCommand.BUNDLE_ID);
  }

  @Override
  public String getVersion() {
    return getObject(WebDriverLikeCommand.VERSION);
  }

  @Override
  public String getBundleVersion() {
    return getObject(WebDriverLikeCommand.BUNDLE_VERSION);
  }



}
