package org.uiautomation.ios.server.configuration;

import java.util.HashMap;
import java.util.Map;

import org.uiautomation.ios.UIAModels.configuration.CommandConfiguration;
import org.uiautomation.ios.UIAModels.configuration.DriverConfiguration;
import org.uiautomation.ios.communication.WebDriverLikeCommand;

public class DriverConfigurationStore implements DriverConfiguration {

  private final Map<WebDriverLikeCommand, CommandConfiguration> configurations = new HashMap<WebDriverLikeCommand, CommandConfiguration>();

  
  public DriverConfigurationStore(){
    System.out.println("driver store created");
  }
  @Override
  public CommandConfiguration configure(WebDriverLikeCommand command) {
    CommandConfiguration config = configurations.get(command);
    if (config == null) {
      System.out.println("creating store for "+command);
      config = new CommandConfigurationStore();
      configurations.put(command, config);
    }
    return config;
  }

}
