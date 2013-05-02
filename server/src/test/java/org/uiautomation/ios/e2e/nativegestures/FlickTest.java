package org.uiautomation.ios.e2e.nativegestures;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAPoint;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWebView;

public class FlickTest extends BaseIOSDriverTest {

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
  public void testRightFlick(){
    UIAPoint startPoint = new UIAPoint(0, 0.5);
    UIAPoint endPoint = new UIAPoint(1, 0.5);
    ((RemoteUIAWebView) driver.findElementsByTagName("UIAWebView").get(0)).flickInsideWithOptions(1, startPoint, endPoint);   //RIGHT

    waitForElement(driver, By.xpath("//UIAStaticText[contains(@name, 'Swiped Right')]"), 6);
    Assert.assertTrue(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Swiped Right')]").isDisplayed());

  }

  //TODO : Fix this test
  @Test(enabled = false)
  public void testLeftFlick(){
    UIAPoint startLeftPoint= new UIAPoint(0.8, 0.5);
    UIAPoint endLeftPoint = new UIAPoint(0.2, 0.5);
    ((RemoteUIAWebView) driver.findElementsByTagName("UIAWebView").get(0)).flickInsideWithOptions(1, startLeftPoint, endLeftPoint);   //Left

    waitForElement(driver, By.xpath("//UIAStaticText[contains(@name, 'Swiped Left')]"), 6);
    Assert.assertTrue(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Swiped Left')]").isDisplayed());
  }

  @Test
  public void testFlickUp(){
    UIAPoint startUpPoint = new UIAPoint(0.5, 0.8);
    UIAPoint endUpPoint = new UIAPoint(0.5, 0.4);
    ((RemoteUIAWebView) driver.findElementsByTagName("UIAWebView").get(0)).flickInsideWithOptions(1, startUpPoint, endUpPoint);   //Up

    waitForElement(driver, By.xpath("//UIAStaticText[contains(@name, 'Swiped Up')]"), 6);
    Assert.assertTrue(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Swiped Up')]").isDisplayed());
  }

  //TODO : Fix this test
  @Test(enabled = false)
  public void testFlickDown(){
    UIAPoint startDownPoint = new UIAPoint(0.5, 0.3);
    UIAPoint endDownPoint = new UIAPoint(0.5, 0.7);
    ((RemoteUIAWebView) driver.findElementsByTagName("UIAWebView").get(0)).flickInsideWithOptions(3, startDownPoint, endDownPoint);   //Down

    waitForElement(driver, By.xpath("//UIAStaticText[contains(@name, 'Swiped Down')]"), 6);
    Assert.assertTrue(driver.findElementByXPath("//UIAStaticText[contains(@name, 'Swiped Down')]").isDisplayed());
  }


}
