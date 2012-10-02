package org.uiautomation.ios.client.uiamodels.impl;

import org.uiautomation.ios.UIAModels.UIAAlert;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.communication.WebDriverLikeCommand;

public class RemoteUIAAlert extends RemoteUIAElement implements UIAAlert {



  public RemoteUIAAlert(RemoteUIADriver driver, String reference) {
    super(driver, reference);
  
  }

  @Override
  public UIAButton getCancelButton() {
    return (RemoteUIAButton) getRemoteObject(WebDriverLikeCommand.ALERT_CANCEL_BUTTON);
  }

  @Override
  public UIAButton getDefaultButton() {
    return (RemoteUIAButton) getRemoteObject(WebDriverLikeCommand.ALERT_DEFAULT_BUTTON);
  }

}
