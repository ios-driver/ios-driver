package org.uiautomation.ios.e2e.config;

import org.openqa.selenium.Rotatable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;

import java.net.MalformedURLException;

import static org.openqa.selenium.ScreenOrientation.LANDSCAPE;
import static org.openqa.selenium.ScreenOrientation.PORTRAIT;


public class OrientationTest extends BaseIOSDriverTest {

  @Test
  public void canSetAndGetOrientationUsingTheWebDriverAPI() throws MalformedURLException {
    RemoteWebDriver driver = null;
    try {
      driver = new RemoteWebDriver(getRemoteURL(), IOSCapabilities.iphone("Safari"));
      WebDriver d = new Augmenter().augment(driver);
      Rotatable rotator = ((Rotatable) d);
      rotator.rotate(LANDSCAPE);
      Assert.assertEquals(rotator.getOrientation(), LANDSCAPE);
      rotator.rotate(PORTRAIT);
      Assert.assertEquals(rotator.getOrientation(), PORTRAIT);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test(expectedExceptions = WebDriverException.class)
  public void detectUnsupportedOrientationWithTheWebDriverAPI() throws MalformedURLException {
    RemoteWebDriver driver = null;
    try {
      driver = new RemoteWebDriver(getRemoteURL(), SampleApps.uiCatalogCap());
      WebDriver d = new Augmenter().augment(driver);
      Rotatable rotator = ((Rotatable) d);
      Assert.assertEquals(rotator.getOrientation(), PORTRAIT);
      rotator.rotate(LANDSCAPE);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }


  @Test
  public void canSetAndGetOrientationUsingTheNativeDriverAPI() throws MalformedURLException {
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(getRemoteURL(), IOSCapabilities.iphone("Safari"));
      driver.rotate(LANDSCAPE);
      Assert.assertEquals(driver.getOrientation(), LANDSCAPE);

      driver.rotate(Orientation.UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT);
      Assert.assertEquals(driver.getNativeOrientation(),
                          Orientation.UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

}
