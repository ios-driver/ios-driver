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
package org.uiautomation.ios.server.command.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.UIAModels.predicate.ValueCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAKeyboard;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;

public class GetHandler extends BaseWebCommandHandler {

  // TODO freynaud cached by session.
  private WebElement addressBar;
  private static final boolean nativeEvents = false;

  public GetHandler(IOSServerManager driver, WebDriverLikeRequest request) {
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

  private WebElement getAddressBar() {


    By b = By.xpath("//UIAElement[@name=l10n('Address')] | //UIATextField[@value=l10n('Search or enter an address')]");


    addressBar = getNativeDriver().findElement(b);
    return addressBar;
  }

  private void typeURLNative(String url) {
    getSession().getDualDriver().setMode(WorkingMode.Native);
    getAddressBar().click();
    RemoteUIAKeyboard keyboard = (RemoteUIAKeyboard) getNativeDriver().getKeyboard();
    keyboard.sendKeys(url);
    UIAElement go = keyboard.findElement(new NameCriteria("Go"));
    go.tap();
    getSession().getDualDriver().setMode(WorkingMode.Web);

  }

  private void fakeTypeURL(String url) {
    getWebDriver().get(url);
  }
}
