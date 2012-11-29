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
package org.uiautomation.ios.client.uiamodels.impl.configuration;

import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.configuration.CommandConfiguration;
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
