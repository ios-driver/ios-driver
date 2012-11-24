package org.uiautomation.ios.client.uiamodels.impl.configuration;

import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.configuration.CommandConfiguration;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.communication.Path;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;

public class RemoteCommandConfiguration implements CommandConfiguration {

  private final WebDriverLikeCommand command;
  private final RemoteUIADriver driver;

  public RemoteCommandConfiguration(WebDriverLikeCommand command, RemoteUIADriver driver) {
    this.command = command;
    this.driver = driver;
  }

  @Override
  public void set(String key, Object value) {
    try {
      JSONObject payload = new JSONObject().put(key, value);
      Path p = new Path(WebDriverLikeCommand.CONFIGURE);
      // session/:sessionId/configure/command/:command
      p.validateAndReplace(":sessionId", driver.getSessionId().toString());
      p.validateAndReplace(":command", command.name());
      WebDriverLikeRequest request = new WebDriverLikeRequest("POST", p, payload);
      driver.execute(request);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Override
  public Object get(String key) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object opt(String key, Object defaultValue) {
    // TODO Auto-generated method stub
    return null;
  }
}
