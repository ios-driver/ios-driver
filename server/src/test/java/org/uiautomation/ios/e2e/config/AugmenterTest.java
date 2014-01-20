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
import org.openqa.selenium.Rotatable;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIATableCell;
import org.uiautomation.ios.client.uiamodels.impl.augmenter.Configurable;
import org.uiautomation.ios.client.uiamodels.impl.augmenter.ElementTree;
import org.uiautomation.ios.client.uiamodels.impl.augmenter.IOSDriverAugmenter;
import org.uiautomation.ios.client.uiamodels.impl.augmenter.IOSSearchContext;
import org.uiautomation.ios.communication.WebDriverLikeCommand;

import java.util.Map;

public class AugmenterTest extends BaseIOSDriverTest {

  RemoteWebDriver driver;

  @BeforeClass
  public void startDriver() {
    driver = new RemoteWebDriver(getRemoteURL(), SampleApps.uiCatalogCap());
  }

  @AfterClass(alwaysRun = true)
  public void stopDriver() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Test
  public void configuration() {
    Configurable config = IOSDriverAugmenter.augment(driver);
    config.setConfiguration(WebDriverLikeCommand.ACCEPT_ALERT, "ok", true);
    Map<String, Object> conf = config.getConfiguration(WebDriverLikeCommand.ACCEPT_ALERT);
    Assert.assertEquals(conf.get("ok"), true);
  }

  private String buttonName = "Buttons, Various uses of UIButton";

  @Test
  public void iosSearchContext() {
    IOSSearchContext finder = IOSDriverAugmenter.augment(driver);
    WebElement element = finder.findElement(new NameCriteria(buttonName));
    Assert.assertTrue(element instanceof UIATableCell);
  }

  @Test
  public void logElementTree() {
    WebElement
        element = driver.findElement(By.linkText("name=" + buttonName));
    ElementTree tree = IOSDriverAugmenter.augment(driver);
  }

  @Test
  public void rotatable() {
    Rotatable rotator = IOSDriverAugmenter.augment(driver);
    rotator.rotate(ScreenOrientation.PORTRAIT);
  }

  @Test
  public void cast() {
    RemoteIOSDriver iosdriver = IOSDriverAugmenter.getIOSDriver(driver);
    WebElement
        element = iosdriver.findElement(By.linkText("name=" + buttonName));
    Assert.assertTrue(element instanceof RemoteUIATableCell);
  }
}
