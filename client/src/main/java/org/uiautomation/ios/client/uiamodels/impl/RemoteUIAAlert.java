package org.uiautomation.ios.client.uiamodels.impl;

import org.uiautomation.ios.UIAModels.UIAAlert;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;

public class RemoteUIAAlert extends RemoteUIAElement implements UIAAlert {

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
    return getDriver().execute(request);
  }

}
