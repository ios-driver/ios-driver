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

import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.*;
import org.uiautomation.ios.UIAModels.predicate.*;
import org.uiautomation.ios.utils.IOSVersion;


public class AlertTest extends BaseIOSDriverTest {

  private static final String alertOK = "(//UIAStaticText[@name='Simple'])[1]";
  private static final String alertOKCancel = "(//UIAStaticText[@name='Okay / Cancel'])[1]";
  private static final String alert3Buttons = "(//UIAStaticText[@name='Other'])[1]";
  private static final String alertPassword = "//UIAStaticText[@name='Secure Text Entry']";
  private static final String alertEntry = "//UIAStaticText[@name='Text Entry']";

  @BeforeClass
  public void startDriver() {
    driver = getDriver(SampleApps.uiCatalogCap());
    driver.findElement(By.xpath("//UIAButton[@name='UICatalog']")).click();
    goToAlertScreen();
  }

  private void goToAlertScreen() {
    String name = "Alert Controller";
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria(name, MatchingStrategy.starts);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = driver.findElement(c);
    element.tap();
  }

  @Test
  public void alertGetText() throws Exception {
    By b = By.xpath(alertOK);
    WebElement el = driver.findElement(b);
    el.click();

    Alert alert = waitForAlert(driver);
    Assert.assertEquals(alert.getText(), "A message should be a short, complete sentence.");
    alert.dismiss();
    waitForAlertToBeGone();
  }

  private void waitForAlertToBeGone() throws InterruptedException {
    // TODO freynaud dismiss should block until alert is gone.
    Thread.sleep(500);
  }

  @Test(expectedExceptions = UnhandledAlertException.class)
  public void cannotInteractWithAppWhenAlertOpen() throws Exception {
    Criteria
        c =
        new AndCriteria(new TypeCriteria(UIAStaticText.class), new NameCriteria("Simple"));
    UIAElement el = driver.findElements(c).get(0);
    // opens an alert.
    el.tap();
    try {
      el.tap();
      Assert.fail("shouldn't click behind alerts.");
    } finally {
      driver.switchTo().alert().dismiss();
      waitForAlertToBeGone();
    }
  }

  @Test
  public void canFindElementInAlertIfAlertOpened() throws Exception {
    Criteria
        c =
        new AndCriteria(new TypeCriteria(UIAStaticText.class), new NameCriteria("Simple"));
    UIAElement el = driver.findElements(c).get(0);
    // opens an alert.
    el.tap();

    try {
      driver.findElement(By.name("Back"));
      Assert.fail("cannot select when alert opened");
    } catch (UnhandledAlertException e) {
      // expected
    }
    // also check for xpath, as xpath logic is not done by instruments, but by the server.
    try {
      driver.findElement(By.xpath("//*[@name='Back']"));
      Assert.fail("cannot select when alert opened");
    } catch (UnhandledAlertException e) {
      // expected
    }
    WebElement alert = driver.findElement(By.xpath("//UIAAlert"));
    driver.switchTo().alert().dismiss();

  }

  @Test
  public void canSeeAlertsAsWebElements() throws Exception {
    RemoteWebDriver d = (RemoteWebDriver) driver;
    Criteria
        c =
        new AndCriteria(new TypeCriteria(UIAStaticText.class), new NameCriteria("Simple"));
    UIAElement el = driver.findElements(c).get(0);
    try {
      d.findElement(By.className("UIAAlert"));
      Assert.fail("should not find alert when there isn't any");
    } catch (NoSuchElementException e) {
      //ignore
    }

    // opens an alert.
    el.tap();

    WebElement rwe = d.findElement(By.className("UIAAlert"));
    String json = rwe.getAttribute("tree");
    JSONObject tree = new JSONObject(json);
    Assert.assertEquals(tree.getString("type"), "UIAAlert");
    // IOS 6. Alert OK buttons are buttons.
    String version = driver.getCapabilities().getSDKVersion();
    IOSVersion v = new IOSVersion(version);

    By selector = By.className("UIAButton");

    WebElement element = rwe.findElement(selector);
    element.click();
    waitForAlertToBeGone();
  }


  @Test
  public void switchToAlert() throws Exception {
    RemoteWebDriver d = (RemoteWebDriver) driver;
    By b = By.xpath("//UIAStaticText[@name='Secure Text Entry']");
    WebElement el = driver.findElement(b);
    try {
      d.switchTo().alert();
      Assert.fail("should have thrown");
    } catch (NoAlertPresentException e) {
      // expected
    }
    el.click();
    Alert alert = d.switchTo().alert();
    alert.sendKeys("test1");
    alert.accept();
  }


  @DataProvider(name = "allAlerts")
  public Object[][] createData1() {
    return new Object[][]{
        {alertOK},
        {alertOKCancel},
        {alert3Buttons},
        {alertEntry},
    };
  }

  @Test(dataProvider = "allAlerts")
  public void dismissAlert(String alertLocator) throws Exception {
    RemoteWebDriver d = (RemoteWebDriver) driver;
    By b = By.xpath(alertLocator);
    WebElement el = driver.findElement(b);
    el.click();

    Alert alert = waitForAlert(driver);
    alert.dismiss();

    try {
      d.switchTo().alert();
      Assert.fail("alert should be gone.");
    } catch (NoAlertPresentException e) {
      // expected
    }
  }

  @Test(dataProvider = "allAlerts")
  public void acceptAlert(String alertLocator) throws Exception {
    RemoteWebDriver d = (RemoteWebDriver) driver;
    By b = By.xpath(alertLocator);
    WebElement el = driver.findElement(b);
    el.click();

    Alert alert = waitForAlert(driver);
    alert.accept();

    try {
      d.switchTo().alert();
      Assert.fail("alert should be gone.");
    } catch (NoAlertPresentException e) {
      // expected
    }
  }

  @DataProvider(name = "non-input")
  public Object[][] createData2() {
    return new Object[][]{
        {alertOK},
        {alertOKCancel},
        {alert3Buttons},
    };
  }

  @Test(dataProvider = "non-input")
  public void sendKeysAlertThrowsIfNoInput(String alertLocator) throws Exception {
    RemoteWebDriver d = (RemoteWebDriver) driver;
    By b = By.xpath(alertLocator);
    WebElement el = driver.findElement(b);
    el.click();

    Alert alert = waitForAlert(driver);
    try {
      alert.sendKeys("test");
      Assert.fail("should crash on sendKeys");
    } catch (InvalidElementStateException e) {
      //expected
    }
    alert.dismiss();
  }

  @Test
  public void sendKeysAlert() throws Exception {
    RemoteWebDriver d = (RemoteWebDriver) driver;
    By b = By.xpath(alertPassword);
    WebElement el = driver.findElement(b);
    el.click();

    Alert alert = waitForAlert(driver);
    WebElement field = driver.findElement(By.xpath("//UIAAlert//UIASecureTextField"));
    Assert.assertEquals(field.getAttribute("value"), "");
    // password must have length >=5
    alert.sendKeys("test1");
    Assert.assertEquals(field.getAttribute("value"), "•••••");
    alert.accept();

    try {
      d.switchTo().alert();
      Assert.fail("alert should be gone.");
    } catch (NoAlertPresentException e) {
      // expected
    }
  }


  @Test(expectedExceptions = NoAlertPresentException.class)
  public void checkUIAlertView() throws Exception {

    Criteria
        c =
        new AndCriteria(new TypeCriteria(UIAStaticText.class), new NameCriteria("Simple"));
    UIAElement el = driver.findElements(c).get(0);
    // opens an alert.
    el.tap();

    UIAAlert alert = driver.findElement(new TypeCriteria(UIAAlert.class));

    // check the alert has all its elements
    alert.findElement(UIAStaticText.class, new NameCriteria("A Short Title Is Best"));
    alert.findElement(UIAStaticText.class, new NameCriteria("A message should be a short, complete sentence."));

    String version = driver.getCapabilities().getSDKVersion();
    IOSVersion v = new IOSVersion(version);
    Class type;
    type = UIAButton.class;

    UIAElement ok = alert.findElement(type, new NameCriteria("OK"));

    ok.tap();
    // wait for the alert to disappear.
    Thread.sleep(500);
    driver.switchTo().alert();
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
