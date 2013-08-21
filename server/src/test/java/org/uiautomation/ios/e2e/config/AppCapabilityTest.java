package org.uiautomation.ios.e2e.config;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;

/**
 * Checks that one can use a URL as the argument to -aut.
 */
public final class AppCapabilityTest extends BaseIOSDriverTest {

  @Test(enabled = false)
  public void canSpecifyAppToUseAsCapability() {
    IOSCapabilities caps = SampleApps.uiCatalogCap();
    caps.setCapability("app", SampleApps.getUICatalogZipURL());
    RemoteIOSDriver driver = null;
    try {
      driver = getDriver(caps);
      String expected = "UIATableCell";
      WebElement element = driver.findElement(By.tagName(expected));
      Assert.assertEquals(element.getClass(), RemoteWebElement.class);
      Assert.assertEquals(element.getTagName(), expected);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
}
