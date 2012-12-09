package org.uiautomation.ios.e2e.config;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.server.utils.ClassicCommands;

public class SmokeIOSDriverTest extends BaseIOSDriverTest {

  private RemoteUIADriver driver;

  @BeforeClass
  public void startDriver() {
    driver = new RemoteUIADriver(getRemoteURL(), SampleApps.uiCatalogCap());
  }

  @AfterClass
  public void stopDriver() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Test
  public void castElementToWhatTheyAre() throws MalformedURLException {
    String expected = "UIATableCell";
    WebElement element = driver.findElement(By.tagName(expected));
    Assert.assertTrue(element instanceof UIATableCell);
  }
  
  @Test
  public void castElementsToWhatTheyAre() throws MalformedURLException {
    String expected = "UIATableCell";
    List<WebElement> elements = driver.findElements(By.tagName(expected));
    for (WebElement el : elements){
      Assert.assertTrue(el instanceof UIATableCell,"was "+el.getClass());
    }
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
    to = driver.getScreenshotAs(OutputType.FILE);
    Assert.assertTrue(to.exists());

  }
}