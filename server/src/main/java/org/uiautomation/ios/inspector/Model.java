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
package org.uiautomation.ios.inspector;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.UIAModels.UIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.server.application.IOSApplication;

public class Model {

  private static final Logger log = Logger.getLogger(Model.class.getName());

  private RemoteUIADriver driver;
  private IOSApplication app;

  private JSONObject cache;

  public RemoteUIADriver getDriver() {
    return driver;
  }

  public void setDriver(RemoteUIADriver driver) {
    if (this.driver != null) {
      throw new WebDriverException("driver already instanciated.");
    }
    this.driver = driver;
  }

  public JSONObject getCache() {
    return cache;
  }

  public JSONObject getObjectTree() {
    JSONObject res = cache.optJSONObject("tree");
    return res;
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
      InputStream is = new FileInputStream(f);
      return is;
    } catch (Exception e) {
      throw new WebDriverException(e);
    }

  }

  public void stop() {
    UIADriver d = getDriver();
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
      // TODO Auto-generated catch block
      e.printStackTrace();
      return "English";
    }
  }

  public IOSApplication getApplication() {
    return app;
  }

  public void setAUT(IOSApplication app) {
    this.app = app;

  }

}
