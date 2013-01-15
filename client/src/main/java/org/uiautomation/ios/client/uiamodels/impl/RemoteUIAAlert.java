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

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.security.Credentials;
import org.uiautomation.ios.UIAModels.UIAAlert;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAStaticText;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;

import java.util.List;

public class RemoteUIAAlert extends RemoteUIAElement implements UIAAlert, Alert {

  public RemoteUIAAlert(RemoteUIADriver driver, String reference) {
    super(driver, reference);

  }

  @Override
  public UIAButton getCancelButton() {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.ALERT_CANCEL_BUTTON);
    return getDriver().execute(request);

  }

  @Override
  public UIAButton getDefaultButton() {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.ALERT_DEFAULT_BUTTON);
    UIAButton butt = getDriver().execute(request);
    return butt;
  }

  @Override
  public void dismiss() {
    UIAButton butt = getCancelButton();
    if (butt == null) {
      butt = getDefaultButton();
    }
    butt.tap();
  }

  @Override
  public void accept() {
    getDefaultButton().tap();
  }

  @Override
  public void sendKeys(String keysToSend) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void authenticateUsing(Credentials credentials) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public String getText() {
    List<UIAElement> texts = this.findElements(new TypeCriteria(UIAStaticText.class));
    // first one is the title, 2nd is what we want.
    if (texts.size() >= 2) {
      return texts.get(1).getValue();
    } else {
      // can't find the text, dump the alert object tree.
      try {
        return logElementTree(null, false).toString(2);
      } catch (Exception e) {
        throw new WebDriverException(e);
      }
    }
  }


}
