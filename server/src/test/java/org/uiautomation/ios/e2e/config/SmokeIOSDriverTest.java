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

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.client.uiamodels.impl.augmenter.IOSDriverAugmenter;
import org.uiautomation.ios.utils.ClassicCommands;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;


public class SmokeIOSDriverTest extends BaseIOSDriverTest {

  @BeforeClass
  public void startDriver() {
    driver = new RemoteIOSDriver(getRemoteURL(), SampleApps.uiCatalogCap());
  }

  @Test
  public void castElementToWhatTheyAre() throws MalformedURLException {
    String expected = "UIATableCell";
    WebElement element = IOSDriverAugmenter.getIOSDriver(driver).findElement(By.tagName(expected));
    Assert.assertTrue(element instanceof UIATableCell);
  }

  @Test
  public void castElementsToWhatTheyAre() throws MalformedURLException {
    String expected = "UIATableCell";
    List<WebElement> elements = IOSDriverAugmenter.getIOSDriver(driver).findElements(
        By.tagName(expected));
    for (WebElement el : elements) {
      Assert.assertTrue(el instanceof UIATableCell, "was " + el.getClass());
    }
  }

  @Test
  public void targetAttributesTests() throws MalformedURLException {
    String sdk = SampleApps.uiCatalogCap().getSDKVersion();
    if (sdk == null) {
      sdk = ClassicCommands.getDefaultSDK();
    }
    Capabilities actual = driver.getCapabilities();

    Assert.assertEquals(actual.getCapability(IOSCapabilities.DEVICE), "iphone");
    Assert.assertEquals(actual.getCapability(IOSCapabilities.UI_NAME), "iPhone Simulator");
    Assert.assertEquals(actual.getCapability(IOSCapabilities.UI_SYSTEM_NAME), "iPhone OS");
    Assert.assertEquals(actual.getCapability(IOSCapabilities.UI_SDK_VERSION), sdk);
    Assert.assertNull(actual.getCapability(IOSCapabilities.UI_VERSION));
    Assert
        .assertEquals(actual.getCapability(IOSCapabilities.BUNDLE_ID), "com.yourcompany.UICatalog");
    Assert.assertEquals(actual.getCapability(IOSCapabilities.BUNDLE_VERSION), "2.10");
  }

  @Test
  public void screenshot() throws Exception {
    File to = new File("ss.png");
    to.delete();
    Assert.assertFalse(to.exists());
    TakesScreenshot d = (TakesScreenshot) (IOSDriverAugmenter.augment(driver));
    to = d.getScreenshotAs(OutputType.FILE);
    Assert.assertTrue(to.exists());
  }
}