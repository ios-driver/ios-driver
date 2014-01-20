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

package org.uiautomation.ios.e2e.uicatalogapp;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;

public class ActionSheetTest extends BaseIOSDriverTest {

  private static final String actionOk = "(//UIAStaticText[@name='Show Simple'])[1]";
  private static final String actionOKCancel = "(//UIAStaticText[@name='Show OK-Cancel'])[1]";


  private RemoteIOSDriver driver;


  @BeforeClass
  public void startDriver() {
    driver = getDriver(SampleApps.uiCatalogCap());
    goToAlertScreen();
  }

  @AfterClass(alwaysRun = true)
  public void stopDriver() {
    if (driver != null) {
      driver.quit();
    }
  }

  private void goToAlertScreen() {
    String name = "Alerts";
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria(name, MatchingStrategy.starts);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = driver.findElement(c);
    element.tap();
  }

  @Test
  public void cannotInteractIfActionSheet() throws Exception {
    RemoteWebDriver d = (RemoteWebDriver) driver;
    By b = By.xpath(actionOk);
    WebElement el = driver.findElement(b);
    el.click();

    try {
      el.getAttribute("name");
      Assert.fail("cannot interact while action sheet is opened");
    } catch (UnhandledAlertException e) {
    }
    Alert alert = driver.switchTo().alert();
    alert.dismiss();
  }

  @Test
  public void okCancel() throws Exception {
    RemoteWebDriver d = (RemoteWebDriver) driver;
    By b = By.xpath(actionOKCancel);
    WebElement el = driver.findElement(b);
    el.click();

    Alert alert = driver.switchTo().alert();
    alert.dismiss();
    try {
      driver.switchTo().alert();
      Assert.fail();
    } catch (NoAlertPresentException e) {
      // ignore
    }
  }

  @Test
  public void findWhenAlertAreGone() throws Exception {
    RemoteWebDriver d = (RemoteWebDriver) driver;
    By b = By.xpath(actionOKCancel);
    WebElement el = driver.findElement(b);
    el.click();

    Alert alert = driver.switchTo().alert();
    try {
      el.click();
      Assert.fail();
    } catch (UnhandledAlertException e) {
      //expected
    }
    alert.accept();
    el.click();
    driver.switchTo().alert();
    alert.accept();

  }


  private Alert waitForAlert(WebDriver driver) {
    long timeoutInMs = 2000;
    long deadline = System.currentTimeMillis() + timeoutInMs;
    Alert res = null;
    while (res == null) {
      try {
        res = driver.switchTo().alert();
        return res;
      } catch (NoAlertPresentException e) {
        // ignore
        if (System.currentTimeMillis() > deadline) {
          throw e;
        }
      }

    }
    throw new Error("should have found an alert or timeout.");
  }
}