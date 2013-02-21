package org.uiautomation.ios.e2e.uicatalogapp;

import org.json.JSONObject;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAAlert;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAStaticText;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;

import java.net.URL;


public class AlertTest extends BaseIOSDriverTest {

  private static final String actionSheet = "(//UIAStaticText[@name='Show Simple'])[1]";
  private static final String alertOK = "(//UIAStaticText[@name='Show Simple'])[2]";
  private static final String alertOKCancel = "(//UIAStaticText[@name='Show OK-Cancel'])[2]";
  private static final String alert3Buttons = "//UIAStaticText[@name='Show Custom']";
  private static final String alertPassword = "//UIAStaticText[@name='Secure Text Input']";


  private RemoteUIADriver driver;

  public static void main(String[] args) throws Exception {
    AlertTest t = new AlertTest();
    t.startServer();
    RemoteWebDriver
        driver =
        new RemoteUIADriver(new URL("http://localhost:4444/wd/hub"), SampleApps.uiCatalogCap());

    WebElement alert = driver.findElement(By.className("UIAAlert"));
    alert.findElement(By.className("UIASecureTextField")).sendKeys("password");
    alert.findElement(By.xpath("//UIAButton[@name='OK']")).click();

    driver.quit();
    t.stopServer();
  }

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
  public void alertGetText() throws Exception {
    RemoteWebDriver d = (RemoteWebDriver) driver;
    By b = By.xpath(alertOK);
    WebElement el = driver.findElement(b);
    el.click();

    Alert alert = waitForAlert(driver);
    Assert.assertEquals(alert.getText(), "<Alert message>");
    alert.dismiss();
  }

  @Test(expectedExceptions = UnhandledAlertException.class)
  public void cannotInteractWithAppWhenAlertOpen() throws Exception {
    Criteria
        c =
        new AndCriteria(new TypeCriteria(UIAStaticText.class), new NameCriteria("Show Simple"));
    UIAElement el = driver.findElements(c).get(1);
    // opens an alert.
    el.tap();
    try {
      el.tap();
      Assert.fail("shouldn't click behind alerts.");
    } finally {
      driver.getAlert().getCancelButton().tap();
    }
  }

  @Test
  public void canSeeAlertsAsWebElements() throws Exception {
    RemoteWebDriver d = (RemoteWebDriver) driver;
    Criteria
        c =
        new AndCriteria(new TypeCriteria(UIAStaticText.class), new NameCriteria("Show Simple"));
    UIAElement el = driver.findElements(c).get(1);
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
    WebElement element = rwe.findElement(By.className("UIAButton"));
    element.click();

  }


  @Test
  public void switchToAlert() throws Exception {
    RemoteWebDriver d = (RemoteWebDriver) driver;
    By b = By.xpath("//UIAStaticText[@name='Secure Text Input']");
    WebElement el = driver.findElement(b);
    try {
      d.switchTo().alert();
      Assert.fail("should have thrown");
    } catch (NoAlertPresentException e) {
      // expected
    }
    el.click();
    Alert alert = d.switchTo().alert();
    alert.sendKeys("test");
    alert.accept();
  }


  @DataProvider(name = "allAlerts")
  public Object[][] createData1() {
    return new Object[][]{
        {alertOK},
        {alertOKCancel},
        {alert3Buttons},
        {alertPassword},
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
    alert.sendKeys("test");
    Assert.assertEquals(field.getAttribute("value"), "••••");
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
        new AndCriteria(new TypeCriteria(UIAStaticText.class), new NameCriteria("Show Simple"));
    UIAElement el = driver.findElements(c).get(1);
    // opens an alert.
    el.tap();

    UIAAlert alert = driver.getAlert();

    // check the alert has all its elements
    alert.findElement(UIAStaticText.class, new NameCriteria("UIAlertView"));
    alert.findElement(UIAStaticText.class, new NameCriteria("<Alert message>"));
    UIAButton ok = alert.findElement(UIAButton.class, new NameCriteria("OK"));

    ok.tap();

    driver.getAlert();
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
