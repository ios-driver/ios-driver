package org.uiautomation.ios.client.uiamodels.impl.configuration;

import org.uiautomation.ios.UIAModels.configuration.CommandConfiguration;
import org.uiautomation.ios.UIAModels.configuration.DriverConfiguration;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.communication.WebDriverLikeCommand;

public class RemoteDriverConfiguration implements DriverConfiguration {

  private final WorkingMode mode;
  private final RemoteUIADriver driver;

  public RemoteDriverConfiguration(WorkingMode mode, RemoteUIADriver driver) {
    this.mode = mode;
    this.driver = driver;
  }

  @Override
  public CommandConfiguration get(WebDriverLikeCommand command) {
    return new RemoteCommandConfiguration(command, driver, mode);
  }

}
