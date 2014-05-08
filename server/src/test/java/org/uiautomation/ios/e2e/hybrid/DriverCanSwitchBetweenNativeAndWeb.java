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

package org.uiautomation.ios.e2e.hybrid;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;

import java.util.Set;

public class DriverCanSwitchBetweenNativeAndWeb extends BaseIOSDriverTest {
    
  @AfterMethod(alwaysRun = true)
  public void afterMethod() throws Exception {
    stopDriver();
  }

  @Test
  public void canSwitchToWebView() throws Exception {
    IOSCapabilities safari = IOSCapabilities.iphone("UICatalog");
    safari.setCapability(IOSCapabilities.SIMULATOR, true);

    driver = new RemoteIOSDriver(getRemoteURL(), safari);
    Set<String> handles = driver.getWindowHandles();
    Assert.assertEquals(handles.size(), 1);

    try {
      driver.switchTo().window("Web");
      Assert.fail("shouldn't work");
    } catch (NoSuchWindowException e) {
      // expected
    }
    UIAElement el =
        driver.findElement(
            new AndCriteria(new TypeCriteria(UIATableCell.class), new NameCriteria("Web",
                MatchingStrategy.starts)));
    el.tap();

    while (driver.getWindowHandles().size() != 2) {
      Thread.sleep(50);
    }
    handles = driver.getWindowHandles();
    Assert.assertEquals(handles.size(), 2);

    UIAElement back = driver
        .findElement(
        new AndCriteria(new TypeCriteria(UIAButton.class), new NameCriteria("Back")));
    back.tap();
    long deadline = System.currentTimeMillis() + 5000;
    while (driver.getWindowHandles().size() != 1) {
      Thread.sleep(50);
      if (System.currentTimeMillis() > deadline) {
        break;
      }
    }
    Assert.assertEquals(driver.getWindowHandles().size(), 1);
  }

  @Test
  public void canSwitchToTheWebViewAndFindByCSS() throws Exception {
    IOSCapabilities safari = IOSCapabilities.iphone("UICatalog");
    safari.setCapability(IOSCapabilities.TIME_HACK, false);

    driver = new RemoteIOSDriver(getRemoteURL(), SampleApps.uiCatalogCap());
    Set<String> handles = driver.getWindowHandles();
    Assert.assertEquals(handles.size(), 1);
    UIAElement webCell =
        driver.findElement(new AndCriteria(new TypeCriteria(UIATableCell.class), new NameCriteria(
            "Web", MatchingStrategy.starts)));
    webCell.tap();

    handles = driver.getWindowHandles();
    Assert.assertEquals(handles.size(), 2);
    driver.switchTo().window("Web");
    // TODO fix that server side. Accessing a webview currently loading isn't handled ?
    Thread.sleep(1000);

    final By by = By.cssSelector("a[href='http://store.apple.com/']");

    long end = System.currentTimeMillis() + 10000;
    WebElement el;
    while (System.currentTimeMillis() < end) {
      try {
        el = driver.findElement(by);
        break;
      } catch (NoSuchElementException e) {
        // ignore
      }
    }
    el = driver.findElement(by);
    //el.click();
    Assert.assertEquals(el.getAttribute("href"), "http://store.apple.com/");
    WebElement body = driver.findElement(By.cssSelector("body"));

    driver.get("http://ebay.co.uk");
    WebElement search = driver.findElement(By.id("kw"));

    search.sendKeys("ipod");
    body = driver.findElement(By.cssSelector("body"));
  }

  @Test
  //(enabled = false)
  public void nativeWebViewSeesNewPages() throws Exception {
    IOSCapabilities safari = IOSCapabilities.iphone("UICatalog");
    safari.setCapability(IOSCapabilities.TIME_HACK, false);

    driver = new RemoteIOSDriver(getRemoteURL(), SampleApps.uiCatalogCap());
    Set<String> handles = driver.getWindowHandles();
    Assert.assertEquals(handles.size(), 1);
    UIAElement webCell =
        driver.findElement(new AndCriteria(new TypeCriteria(UIATableCell.class), new NameCriteria(
            "Web", MatchingStrategy.starts)));
    webCell.tap();

    Thread.sleep(5000);
    driver.switchTo().window("Web");
    Thread.sleep(5000);
    driver.get("http://ebay.co.uk");
    WebElement body = driver.findElement(By.cssSelector("body"));
  }

}
