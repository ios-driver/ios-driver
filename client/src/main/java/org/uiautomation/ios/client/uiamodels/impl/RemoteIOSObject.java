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
package org.uiautomation.ios.client.uiamodels.impl;

import java.lang.reflect.Constructor;
import java.util.Map;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
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

  private final RemoteUIADriver driver;
  private final String reference;

  public RemoteIOSObject(RemoteUIADriver driver, String reference) {
    this.driver = driver;
    this.reference = reference;
    setParent(driver);
  }

  /**
   * @see #get(WebDriverLikeCommand, JSONObject)
   * @param command
   * @return
   */
  /*public RemoteIOSObject getRemoteObject(WebDriverLikeCommand command) {
    return getRemoteObject(command, new JSONObject());
  }*/

  /**
   * retrieves an object in the element tree of the current RemoteObject, using
   * the given command.
   *
   * @param command
   *          the command to execute, for instance findElement.
   * @param payload
   *          the optional parameters, criteria for instance.
   * @return a lazy loaded remote object.
   */
  /*public RemoteIOSObject getRemoteObject(WebDriverLikeCommand command, JSONObject payload) {
    try {
      Path p = new Path(command).withSession(driver.getSessionId()).withReference(getReference());

      WebDriverLikeRequest request = new WebDriverLikeRequest(command.method(), p, payload);
      Response response = execute(request);
      JSONObject uiaObject = ((JSONObject) response.getValue());

      return createObject(getDriver(), uiaObject, command.returnType());
    } catch (IOSAutomationException e) {
      throw e;
    } catch (Exception e) {
      throw new IOSAutomationException("bug", e);
    }
  }*/

  /**
   * Uses reflection to instanciate a remote object implementing the correct interface.
   *
   * @param uiObject the json object returned by the server.
   * @param expected the UIAutomation object type.
   * @return the object. If the object is UIAElementNil, return null for a simple object, an empty
   *         list for a UIAElementArray.
   */
  public static WebElement createObject(RemoteUIADriver driver, Map<String, Object> ro) {
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
        // argsClass = new Class[] {RemoteUIADriver.class, String.class,
        // Integer.class};
      } else {
        args = new Object[]{driver, ref};
        argsClass = new Class[]{RemoteUIADriver.class, String.class};
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

  /**
   * return the dimension of an element.
   *
   * @param command
   * @return
   */
  /*public UIARect getUIARect(WebDriverLikeCommand command) {
    try {
      Path p = new Path(command).withSession(driver.getSessionId()).withReference(getReference());
      WebDriverLikeRequest request = new WebDriverLikeRequest(command.method(), p, null);
      Response response = execute(request);
      JSONObject rect = (JSONObject) response.getValue();
      return new UIARect(rect);
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }

  }*/

  /**
   * Execute the command.
   *
   * @see #execute(WebDriverLikeCommand, JSONObject)
   * @param command
   */
  /*public void execute(WebDriverLikeCommand command) {
    execute(command, new JSONObject());
  }*/

  /**
   * execute the command with the given parameters.
   *
   * @param command
   * @param payload
   */
  /*public void execute(WebDriverLikeCommand command, JSONObject payload) {
    Path p = new Path(command).withSession(driver.getSessionId()).withReference(getReference());
    WebDriverLikeRequest request = new WebDriverLikeRequest(command.method(), p, payload);
    execute(request);
  }*/

  /**
   *
   * @param command
   * @return
   */
  @SuppressWarnings("unchecked")
  /*protected <T> T getObject(WebDriverLikeCommand command) {
    Path p = new Path(command).withSession(driver.getSessionId()).withReference(getReference());
    WebDriverLikeRequest request = new WebDriverLikeRequest(command.method(), p, null);
    Response response = execute(request);
    if (response.getValue() == JSONObject.NULL) {
      return null;
    } else {
      try {
        return (T) response.getValue();
      } catch (ClassCastException castException) {
        throw new IOSAutomationException("couldn't cast from " + response.getValue().getClass());
      }
    }
  }*/

  protected RemoteUIADriver getDriver() {
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
