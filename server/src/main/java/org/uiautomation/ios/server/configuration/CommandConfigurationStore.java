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
package org.uiautomation.ios.server.configuration;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.configuration.CommandConfiguration;

public class CommandConfigurationStore implements CommandConfiguration {

  private final JSONObject config = new JSONObject();

  @Override
  public void set(String key, Object value) {
    try {
      config.put(key, value);
    } catch (JSONException e) {
      e.printStackTrace();
    }

  }

  @Override
  public Object get(String key) {
    Object res = config.opt(key);
    return res;
  }

  @Override
  public Object opt(String key, Object defaultValue) {
    Object res = get(key);
    if (res == null){
      res = defaultValue;
    }
    return res;
  }

}
