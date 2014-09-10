/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
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
package org.uiautomation.ios.command.configuration;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.configuration.CommandConfiguration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandConfigurationStore implements CommandConfiguration {

  private final JSONObject config = new JSONObject();
  private static final Logger log = Logger.getLogger(CommandConfiguration.class.getName());

  @Override
  public void set(String key, Object value) {
    try {
      config.put(key, value);
    } catch (JSONException e) {
      log.log(Level.SEVERE,"format error",e);
    }

  }

  @Override
  public Object get(String key) {
    Object res = config.opt(key);
    return res;
  }

  @Override
  public Map<String, Object> getAll() {
    Map<String, Object> res = new HashMap<String, Object>();
    Iterator<String> iter = config.keys();
    while (iter.hasNext()) {
      String key = iter.next();
      Object value = config.opt(key);
      res.put(key, value);
    }
    return res;
  }

  @Override
  public Object opt(String key, Object defaultValue) {
    Object res = get(key);
    if (res == null) {
      res = defaultValue;
    }
    return res;
  }

}
