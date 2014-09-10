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
package org.uiautomation.ios.inspector;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.application.APPIOSApplication;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Model {

  private static final Logger log = Logger.getLogger(Model.class.getName());

  private RemoteIOSDriver driver;
  private APPIOSApplication app;

  private JSONObject cache;

  public RemoteIOSDriver getDriver() {
    return driver;
  }

  public void setDriver(RemoteIOSDriver driver) {
    if (this.driver != null) {
      throw new WebDriverException("driver already instantiated.");
    }
    this.driver = driver;
  }

  public JSONObject getCache() {
    return cache;
  }

  public JSONObject getObjectTree() {
    return cache.optJSONObject("tree");
  }

  public void refresh() {
    cache = driver.logElementTree(null, false);
  }

  public InputStream getLastScreenshotInputStream() {
    try {
      File f = new File(cache.optString("path"));
      while (!f.exists()) {
        log.warning("cannot find " + f + ".Last screenshot cannot be found.");
        Thread.sleep(250);
      }
      return new FileInputStream(f);
    } catch (Exception e) {
      throw new WebDriverException(e);
    }
  }

  public void stop() {
    RemoteIOSDriver d = getDriver();
    if (d == null) {
      throw new WebDriverException("driver not active.");
    } else {
      driver.quit();
      driver = null;
    }
  }

  public String getLanguage() {
    try {
      return cache.getString("language");
    } catch (JSONException e) {
      log.log(Level.SEVERE, "cannot find language : defaulting to English", e);
      return "English";
    }
  }

  public APPIOSApplication getApplication() {
    return app;
  }

  public void setAUT(APPIOSApplication app) {
    this.app = app;
  }
}
