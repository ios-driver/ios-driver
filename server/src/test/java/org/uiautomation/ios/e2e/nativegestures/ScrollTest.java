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

package org.uiautomation.ios.e2e.nativegestures;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.ScrollDirection;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAScrollView;


public class ScrollTest extends BaseIOSDriverTest {

  @BeforeClass
  public void startDriver() {
    driver = new RemoteIOSDriver(getRemoteURL(), SampleApps.ppNQASampleAppCap());
  }

  @Test
  public void testVerticalScrollingDown() {
    Assert.assertTrue(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Vertical View 1')]").isDisplayed());
//    Assert.assertFalse(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Vertical View 2')]").isDisplayed());
//    Assert.assertFalse(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Vertical View 3')]").isDisplayed());

    ((RemoteUIAScrollView) driver.findElementByXPath("//UIAScrollView[contains(@name, 'Vertical Name')]")).scroll(ScrollDirection.DOWN);

    waitForElement(driver, By.xpath("//UIAStaticText[contains(@name, 'Vertical View 2')]"), 6);
    Assert.assertTrue(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Vertical View 2')]").isDisplayed());
//    Assert.assertFalse(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Vertical View 1')]").isDisplayed());
//    Assert.assertFalse(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Vertical View 3')]").isDisplayed());

    ((RemoteUIAScrollView) driver.findElementByXPath("//UIAScrollView[contains(@name, 'Vertical Name')]")).scroll(ScrollDirection.UP);
  }

  @Test
  public void testVerticalScrollingUp() {
    Assert.assertTrue(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Vertical View 1')]").isDisplayed());
//    Assert.assertFalse(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Vertical View 2')]").isDisplayed());
//    Assert.assertFalse(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Vertical View 3')]").isDisplayed());

    ((RemoteUIAScrollView) driver.findElementByXPath("//UIAScrollView[contains(@name, 'Vertical Name')]")).scroll(ScrollDirection.DOWN);
    ((RemoteUIAScrollView) driver.findElementByXPath("//UIAScrollView[contains(@name, 'Vertical Name')]")).scroll(ScrollDirection.DOWN);
    ((RemoteUIAScrollView) driver.findElementByXPath("//UIAScrollView[contains(@name, 'Vertical Name')]")).scroll(ScrollDirection.UP);

    waitForElement(driver, By.xpath("//UIAStaticText[contains(@name, 'Vertical View 2')]"), 6);
    Assert.assertTrue(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Vertical View 2')]").isDisplayed());
//    Assert.assertFalse(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Vertical View 1')]").isDisplayed());
//    Assert.assertFalse(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Vertical View 3')]").isDisplayed());
      ((RemoteUIAScrollView) driver.findElementByXPath("//UIAScrollView[contains(@name, 'Vertical Name')]")).scroll(ScrollDirection.UP);
  }

  @Test
  public void testHorizontalScrollingRight() {
    Assert.assertTrue(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Horizontal View 1')]").isDisplayed());
//    Assert.assertFalse(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Horizontal View 2')]").isDisplayed());
//    Assert.assertFalse(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Horizontal View 3')]").isDisplayed());

    ((RemoteUIAScrollView) driver.findElementByXPath("//UIAScrollView[contains(@name, 'Horizontal Name')]")).scroll(ScrollDirection.RIGHT);

    waitForElement(driver, By.xpath("//UIAStaticText[contains(@name, 'Horizontal View 2')]"), 6);

    Assert.assertTrue(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Horizontal View 2')]").isDisplayed());
//    Assert.assertFalse(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Horizontal View 1')]").isDisplayed());
//    Assert.assertFalse(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Horizontal View 3')]").isDisplayed());

    ((RemoteUIAScrollView) driver.findElementByXPath("//UIAScrollView[contains(@name, 'Horizontal Name')]")).scroll(ScrollDirection.LEFT);
  }

  @Test
  public void testHorizontalScrollingLeft() {
    Assert.assertTrue(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Horizontal View 1')]").isDisplayed());
//    Assert.assertFalse(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Horizontal View 2')]").isDisplayed());
//    Assert.assertFalse(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Horizontal View 3')]").isDisplayed());

    ((RemoteUIAScrollView) driver.findElementByXPath("//UIAScrollView[contains(@name, 'Horizontal Name')]")).scroll(ScrollDirection.RIGHT);
    ((RemoteUIAScrollView) driver.findElementByXPath("//UIAScrollView[contains(@name, 'Horizontal Name')]")).scroll(ScrollDirection.RIGHT);
    ((RemoteUIAScrollView) driver.findElementByXPath("//UIAScrollView[contains(@name, 'Horizontal Name')]")).scroll(ScrollDirection.LEFT);

    waitForElement(driver, By.xpath("//UIAStaticText[contains(@name, 'Horizontal View 2')]"), 6);

    Assert.assertTrue(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Horizontal View 2')]").isDisplayed());
//    Assert.assertFalse(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Horizontal View 1')]").isDisplayed());
//    Assert.assertFalse(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Horizontal View 3')]").isDisplayed());

    ((RemoteUIAScrollView) driver.findElementByXPath("//UIAScrollView[contains(@name, 'Horizontal Name')]")).scroll(ScrollDirection.LEFT);
  }
}
