package org.uiautomation.ios.e2e.nativegestures;

import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.client.uiamodels.impl.augmenter.IOSDriverAugmenter;
import org.uiautomation.ios.client.uiamodels.impl.augmenter.IOSTouchScreen;

public class PinchTest extends BaseIOSDriverTest {

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

  @Test
  public void testPinchClose(){

    Point closeFrom = new Point(756, 612);
    Point closeTo = new Point(856, 612);

    IOSTouchScreen iosTouchScreen = IOSDriverAugmenter.augment(driver);
    iosTouchScreen.pinchCloseFromToForDuration(closeFrom, closeTo, 3);

    waitForElement(driver, By.xpath("//UIAStaticText[contains(@name, 'scale: 0.011 - velocity: 0.000')]"), 6);

    Assert.assertTrue(driver.findElementByXPath("//UIAStaticText[contains(@name, 'scale: 0.011 - velocity: 0.000')]").isDisplayed());

  }

  @Test
  public void testPinchOpen(){

    Point openFrom = new Point(856, 612);
    Point openTo = new Point(756, 612);

    IOSTouchScreen iosTouchScreen = IOSDriverAugmenter.augment(driver);
    iosTouchScreen.pinchOpenFromToForDuration(openFrom, openTo, 1);

    waitForElement(driver, By.xpath("//UIAStaticText[contains(@name, 'scale: 10.000')]"), 6);

    Assert.assertTrue(driver.findElementByXPath("//UIAStaticText[contains(@name, 'scale: 10.000')]").isDisplayed());

  }
}
