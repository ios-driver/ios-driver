package org.uiautomation.ios.e2e.config;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;

/**
 * Checks that one can use a URL as the argument to -aut.
 */
public final class AutAsUrlTest extends BaseIOSDriverTest {


  @Test
  public void canUseUrlInAut() {
    RemoteWebDriver driver = null;
    try {
      driver = new RemoteWebDriver(getRemoteURL(), SampleApps.uiCatalogCap());
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
