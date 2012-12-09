package org.uiautomation.ios.e2e.config;

import java.io.File;
import java.net.MalformedURLException;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.server.utils.ClassicCommands;

public class SmokeWebDriverTest extends BaseIOSDriverTest {

  private RemoteWebDriver driver;

  @BeforeClass
  public void startDriver() {
    driver = new RemoteWebDriver(getRemoteURL(), SampleApps.uiCatalogCap());
  }

  @AfterClass
  public void stopDriver() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Test
  public void returnWebElements() throws MalformedURLException {
    String expected = "UIATableCell";
    WebElement element = driver.findElement(By.tagName(expected));
    Assert.assertEquals(element.getClass(), RemoteWebElement.class);
    String clazz = element.getTagName();
    Assert.assertEquals(clazz, expected);
  }

  @Test
  public void targetAttributesTests() throws MalformedURLException {
    String sdk = SampleApps.uiCatalogCap().getSDKVersion();
    if (sdk == null) {
      sdk = ClassicCommands.getDefaultSDK();
    }
    Capabilities actual = driver.getCapabilities();

    Assert.assertEquals(actual.getCapability(IOSCapabilities.DEVICE), "iphone");
    Assert.assertEquals(actual.getCapability(IOSCapabilities.UI_NAME), "iPhone Simulator");
    Assert.assertEquals(actual.getCapability(IOSCapabilities.UI_SYSTEM_NAME), "iPhone OS");
    Assert.assertEquals(actual.getCapability(IOSCapabilities.UI_SDK_VERSION), sdk);
    Assert.assertNull(actual.getCapability(IOSCapabilities.UI_VERSION));
    Assert.assertEquals(actual.getCapability(IOSCapabilities.BUNDLE_ID), "com.yourcompany.UICatalog");
    Assert.assertEquals(actual.getCapability(IOSCapabilities.BUNDLE_VERSION), "2.10");

  }

  @Test
  public void screenshot() throws Exception {
    File to = new File("ss.png");
    to.delete();

    Assert.assertFalse(to.exists());

    WebDriver screenshotter = new Augmenter().augment(driver);
    to = ((TakesScreenshot) screenshotter).getScreenshotAs(OutputType.FILE);
    Assert.assertTrue(to.exists());
  }

}
