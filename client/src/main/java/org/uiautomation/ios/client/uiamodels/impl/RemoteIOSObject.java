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
package org.uiautomation.ios.client.uiamodels.impl;

import java.lang.reflect.Constructor;
import java.util.Map;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.uiautomation.ios.client.uiamodels.impl.configuration.WebDriverLikeCommandExecutor;
import org.uiautomation.ios.communication.Path;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;

/**
 * Proxy to an object cached on the server side.Each object is identified by a unique reference when
 * accessed the first time, and accessed with this reference later on.
 *
 * WARNING : Equality as per the JSON Webdriver protocol is broken : the same object can be stored
 * several times in the cache with different references if retrieved several time.
 */
public abstract class RemoteIOSObject extends RemoteWebElement {

  private final RemoteWebDriver driver;
  private final String reference;
  protected final WebDriverLikeCommandExecutor commandExecutor;

  public RemoteIOSObject(RemoteWebDriver driver, String reference) {
    this.driver = driver;
    this.reference = reference;
    setParent(driver);
    commandExecutor = new WebDriverLikeCommandExecutor(driver);
  }


  /**
   * Uses reflection to instanciate a remote object implementing the correct interface.
   *
   * @return the object. If the object is UIAElementNil, return null for a simple object, an empty
   *         list for a UIAElementArray.
   */
  public static WebElement createObject(RemoteWebDriver driver, Map<String, Object> ro) {
    String ref = ro.get("ELEMENT").toString();

    String type = (String) ro.get("type");
    if (type != null) {
      String remoteObjectName = "org.uiautomation.ios.client.uiamodels.impl.Remote" + type;

      if ("UIAElementNil".equals(type)) {
        return null;
      }

      boolean isArray = false; // uiObject.has("length");

      Object[] args = null;
      Class<?>[] argsClass = null;

      if (isArray) {
        // args = new Object[] {driver, ref, uiObject.getInt("length")};
        // argsClass = new Class[] {RemoteIOSDriver.class, String.class,
        // Integer.class};
      } else {
        args = new Object[]{driver, ref};
        argsClass = new Class[]{RemoteWebDriver.class, String.class};
      }
      try {
        Class<?> clazz = Class.forName(remoteObjectName);
        Constructor<?> c = clazz.getConstructor(argsClass);
        Object o = c.newInstance(args);
        RemoteWebElement element = (RemoteWebElement) o;
        element.setFileDetector(driver.getFileDetector());
        element.setParent(driver);
        element.setId(ref);
        return (RemoteIOSObject) o;
      } catch (Exception e) {
        throw new WebDriverException("error casting", e);
      }
    } else {
      RemoteWebElement element = new RemoteWebElement();
      element.setFileDetector(driver.getFileDetector());
      element.setId(ref);
      element.setParent(driver);
      return element;
    }


  }

  protected RemoteWebDriver getDriver() {
    return driver;
  }

  public String getReference() {
    return reference;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + ",ref:" + reference;
  }
}
