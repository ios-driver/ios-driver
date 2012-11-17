package org.uiautomation.ios.client.uiamodels.impl.configuration;

import org.uiautomation.ios.UIAModels.configuration.CommandConfiguration;
import org.uiautomation.ios.UIAModels.configuration.DriverConfiguration;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.communication.WebDriverLikeCommand;

public class RemoteDriverConfiguration implements DriverConfiguration {

  private final RemoteUIADriver driver;

  public RemoteDriverConfiguration(RemoteUIADriver driver) {
    this.driver = driver;
  }

  @Override
  public CommandConfiguration configure(WebDriverLikeCommand command) {
    return new RemoteCommandConfiguration(command, driver);
  }

}
