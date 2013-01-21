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
package org.uiautomation.ios.server.command.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.UIAModels.predicate.ValueCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAKeyboard;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;

public class GetHandler extends BaseWebCommandHandler {

  // TODO freynaud cached by session.
  private UIAElement addressBar;
  private static final boolean nativeEvents = false;

  public GetHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    JSONObject desc = new JSONObject();
    desc.put(
        "nativeEvents",
        "{boolean}, default to "
        + nativeEvents
        + ".true = UIAutomation native events will be used to enter the URL (slow) , Web =  WebKit remote debugging will be used.Faster.");
    return desc;
  }

  @Override
  public Response handle() throws Exception {
    long start = System.currentTimeMillis();
    String url = getRequest().getPayload().getString("url");
    /*boolean newPageWillBeLoaded = true;
    if (url.contains("#")) {
      try {
        String currentURL = getSession().getRemoteWebDriver().getCurrentUrl();
        int index = url.indexOf(currentURL);

        if (index == 0) {
          String delta = url.replace(currentURL, "");
          if (delta.startsWith("#")) {
            newPageWillBeLoaded = false;
          }
        }
      } catch (Exception e) {
        // ignore.
      }
    }

    if (newPageWillBeLoaded) {
      getSession().getRemoteWebDriver().getContext().newContext();
    }    */

    boolean useNativeEvents = getConfiguration("nativeEvents", nativeEvents);

    if (useNativeEvents) {
      typeURLNative(url);
    } else {
      fakeTypeURL(url);
    }
    /*if (newPageWillBeLoaded) {
      getSession().getRemoteWebDriver().waitForPageToLoad();
    } */
    Response res = new Response();
    res.setSessionId(getSession().getSessionId());
    res.setStatus(0);
    res.setValue(new JSONObject());
    return res;
  }

  private UIAElement getAddressBar() {

    Criteria urlAddressBar = new AndCriteria(new TypeCriteria(UIAElement.class), new ValueCriteria(
        "Go to this address"));
    addressBar = getSession().getNativeDriver().findElement(urlAddressBar);
    return addressBar;
  }

  private void typeURLNative(String url) {
    getAddressBar().tap();
    RemoteUIAKeyboard keyboard = (RemoteUIAKeyboard) getSession().getNativeDriver().getKeyboard();
    keyboard.sendKeys(url);
    keyboard.findElement(new NameCriteria("Go")).tap();
  }

  private void fakeTypeURL(String url) {
    getSession().getRemoteWebDriver().get(url);
  }

}
