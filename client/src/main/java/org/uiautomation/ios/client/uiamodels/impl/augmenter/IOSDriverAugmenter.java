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

package org.uiautomation.ios.client.uiamodels.impl.augmenter;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.AttachRemoteIOSDriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;

public class IOSDriverAugmenter {

  /**
   * Add all the IOS specific interfaces ios-driver supports.
   */
  public static <T> T augment(WebDriver driver) {
    Augmenter augmenter = new Augmenter();
    augmenter.addDriverAugmentation(IOSCapabilities.CONFIGURABLE, new AddConfigurable());
    augmenter.addDriverAugmentation(IOSCapabilities.ELEMENT_TREE, new AddLogElementTree());
    augmenter.addDriverAugmentation(IOSCapabilities.IOS_SEARCH_CONTEXT, new AddIOSSearchContext());
    augmenter.addDriverAugmentation(IOSCapabilities.IOS_TOUCH_SCREEN, new AddIOSTouchScreen());
    return (T) augmenter.augment(driver);
  }


  /*
   * Most actions can be performed using the normal augmented driver, but for findElement, the
   * findElement(s) must be override, so need to create a new object from scratch.
   */
  public static RemoteIOSDriver getIOSDriver(RemoteWebDriver driver) {
    if (!(driver.getCommandExecutor() instanceof HttpCommandExecutor)) {
      throw new WebDriverException("ios only supports http communication.");
    }
    HttpCommandExecutor e = (HttpCommandExecutor) driver.getCommandExecutor();
    RemoteIOSDriver
        attach =
        new AttachRemoteIOSDriver(e.getAddressOfRemoteServer(), driver.getSessionId());
    return attach;
  }
}
