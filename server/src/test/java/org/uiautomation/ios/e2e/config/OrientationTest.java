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

package org.uiautomation.ios.e2e.config;

import org.openqa.selenium.Rotatable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;

import java.net.MalformedURLException;

import static org.openqa.selenium.ScreenOrientation.LANDSCAPE;
import static org.openqa.selenium.ScreenOrientation.PORTRAIT;


public class OrientationTest extends BaseIOSDriverTest {

  @Test
  public void canSetAndGetOrientationUsingTheWebDriverAPI() throws MalformedURLException {
    RemoteWebDriver driver = null;
    try {
      driver = new RemoteWebDriver(getRemoteURL(), IOSCapabilities.iphone("Safari"));
      WebDriver d = new Augmenter().augment(driver);
      Rotatable rotator = ((Rotatable) d);
      rotator.rotate(LANDSCAPE);
      Assert.assertEquals(rotator.getOrientation(), LANDSCAPE);
      rotator.rotate(PORTRAIT);
      Assert.assertEquals(rotator.getOrientation(), PORTRAIT);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test(expectedExceptions = WebDriverException.class)
  public void detectUnsupportedOrientationWithTheWebDriverAPI() throws MalformedURLException {
    RemoteWebDriver driver = null;
    try {
      driver = new RemoteWebDriver(getRemoteURL(), SampleApps.uiCatalogCap());
      WebDriver d = new Augmenter().augment(driver);
      Rotatable rotator = ((Rotatable) d);
      Assert.assertEquals(rotator.getOrientation(), PORTRAIT);
      rotator.rotate(LANDSCAPE);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }


  @Test
  public void canSetAndGetOrientationUsingTheNativeDriverAPI() throws MalformedURLException {
    RemoteIOSDriver driver = null;
    try {
      driver = new RemoteIOSDriver(getRemoteURL(), IOSCapabilities.iphone("Safari"));
      driver.rotate(LANDSCAPE);
      Assert.assertEquals(driver.getOrientation(), LANDSCAPE);

      driver.rotate(Orientation.UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT);
      Assert.assertEquals(driver.getNativeOrientation(),
                          Orientation.UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

}
