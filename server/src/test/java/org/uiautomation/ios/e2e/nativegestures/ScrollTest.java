package org.uiautomation.ios.e2e.nativegestures;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.ScrollDirection;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAScrollView;


public class ScrollTest extends BaseIOSDriverTest {

  private RemoteIOSDriver driver = null;

  @BeforeClass
  public void startDriver() {
    driver = new RemoteIOSDriver(getRemoteURL(), SampleApps.ppNQASampleAppCap());


  }

  @AfterClass
  public void stopDriver() {
    if (driver != null) {
      driver.quit();
    }
  }

  public void waitForElement(org.openqa.selenium.By by, long timeOut) {

    WebElement element = (new WebDriverWait(driver, timeOut)).until(ExpectedConditions.visibilityOfElementLocated(by));

  }


  @Test
  public void testVerticalScrollingDown() {

    Assert.assertTrue(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 1')])[1]").isDisplayed());
    Assert.assertFalse(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 2')])[1]").isDisplayed());
    Assert.assertFalse(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 3')])[1]").isDisplayed());

    ((RemoteUIAScrollView) driver.findElementsByTagName("UIAScrollView").get(2)).scroll(ScrollDirection.DOWN);

    waitForElement(By.xpath("(//UIAStaticText[contains(@name, 'View 2')])[1]"), 6);
    Assert.assertTrue(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 2')])[1]").isDisplayed());
    Assert.assertFalse(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 1')])[1]").isDisplayed());
    Assert.assertFalse(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 3')])[1]").isDisplayed());

    ((RemoteUIAScrollView) driver.findElementsByTagName("UIAScrollView").get(1)).scroll(ScrollDirection.UP);


  }

  @Test
  public void testVerticalScrollingUp() {
    Assert.assertTrue(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 1')])[1]").isDisplayed());
    Assert.assertFalse(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 2')])[1]").isDisplayed());
    Assert.assertFalse(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 3')])[1]").isDisplayed());

    ((RemoteUIAScrollView) driver.findElementsByTagName("UIAScrollView").get(2)).scroll(ScrollDirection.DOWN);

    ((RemoteUIAScrollView) driver.findElementsByTagName("UIAScrollView").get(1)).scroll(ScrollDirection.DOWN);

    ((RemoteUIAScrollView) driver.findElementsByTagName("UIAScrollView").get(1)).scroll(ScrollDirection.UP);

    waitForElement(By.xpath("(//UIAStaticText[contains(@name, 'View 2')])[1]"), 6);
    Assert.assertTrue(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 2')])[1]").isDisplayed());
    Assert.assertFalse(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 3')])[1]").isDisplayed());
    Assert.assertFalse(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 1')])[1]").isDisplayed());

    ((RemoteUIAScrollView) driver.findElementsByTagName("UIAScrollView").get(1)).scroll(ScrollDirection.UP);
  }


  @Test
  public void testHorizontalScrollingRight() {

    Assert.assertTrue(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 1')])[2]").isDisplayed());
    Assert.assertFalse(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 2')])[2]").isDisplayed());
    Assert.assertFalse(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 3')])[2]").isDisplayed());

    ((RemoteUIAScrollView) driver.findElementsByTagName("UIAScrollView").get(1)).scroll(ScrollDirection.RIGHT);

    waitForElement(By.xpath("(//UIAStaticText[contains(@name, 'View 2')])[2]"), 6);

    Assert.assertTrue(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 2')])[2]").isDisplayed());
    Assert.assertFalse(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 1')])[2]").isDisplayed());
    Assert.assertFalse(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 3')])[2]").isDisplayed());

    ((RemoteUIAScrollView) driver.findElementsByTagName("UIAScrollView").get(2)).scroll(ScrollDirection.LEFT);

  }

  @Test
  public void testHorizontalScrollingLeft() {

    Assert.assertTrue(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 1')])[2]").isDisplayed());
    Assert.assertFalse(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 2')])[2]").isDisplayed());
    Assert.assertFalse(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 3')])[2]").isDisplayed());

    ((RemoteUIAScrollView) driver.findElementsByTagName("UIAScrollView").get(1)).scroll(ScrollDirection.RIGHT);

    ((RemoteUIAScrollView) driver.findElementsByTagName("UIAScrollView").get(2)).scroll(ScrollDirection.RIGHT);

    ((RemoteUIAScrollView) driver.findElementsByTagName("UIAScrollView").get(3)).scroll(ScrollDirection.LEFT);

    waitForElement(By.xpath("(//UIAStaticText[contains(@name, 'View 2')])[2]"), 6);
    Assert.assertTrue(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 2')])[2]").isDisplayed());
    Assert.assertFalse(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 3')])[2]").isDisplayed());
    Assert.assertFalse(driver.findElementByXPath("(//UIAStaticText[contains(@name, 'View 1')])[2]").isDisplayed());

    ((RemoteUIAScrollView) driver.findElementsByTagName("UIAScrollView").get(2)).scroll(ScrollDirection.LEFT);

  }
}
