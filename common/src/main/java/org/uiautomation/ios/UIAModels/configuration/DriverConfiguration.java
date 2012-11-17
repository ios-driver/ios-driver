package org.uiautomation.ios.UIAModels.configuration;

import org.uiautomation.ios.communication.WebDriverLikeCommand;

public interface DriverConfiguration {

  public CommandConfiguration configure(WebDriverLikeCommand command);
}
