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
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;

public class ActionSheetTest extends BaseIOSDriverTest {

  private static final String actionOk = "(//UIAStaticText[@name='Show Simple'])[1]";
  private static final String actionOKCancel = "(//UIAStaticText[@name='Show OK-Cancel'])[1]";


  private RemoteUIADriver driver;


  @BeforeClass
  public void startDriver() {
    driver = new RemoteUIADriver(getRemoteURL(), SampleApps.uiCatalogCap());
    goToAlertScreen();
  }

  @AfterClass
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
    System.out.println(alert.getText());
    alert.dismiss();
    try {
      driver.switchTo().alert();
      Assert.fail();
    } catch (NoAlertPresentException e) {
      // ignore
    }
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