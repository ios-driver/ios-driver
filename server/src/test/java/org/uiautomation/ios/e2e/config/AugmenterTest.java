package org.uiautomation.ios.e2e.config;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIADriver;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.server.augmenter.AddIOSDriver;

import java.util.Map;

public class AugmenterTest extends BaseIOSDriverTest {

  @Test
  public void configuration() {
    RemoteWebDriver driver = null;
    try {
      driver = new RemoteWebDriver(getRemoteURL(), SampleApps.uiCatalogCap());

      Augmenter augmenter = new Augmenter();
      augmenter.addDriverAugmentation(IOSCapabilities.CONFIGURABLE, new AddIOSDriver());
      UIADriver cdriver = (UIADriver) augmenter.augment(driver);
      cdriver.setConfiguration(WebDriverLikeCommand.ACCEPT_ALERT, "ok", true);
      Map<String, Object> conf = cdriver.getConfiguration(WebDriverLikeCommand.ACCEPT_ALERT);
      System.out.println(conf);

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  private String buttonName = "Buttons, Various uses of UIButton";

  @Test
  public void elementCast() {
    RemoteWebDriver driver = null;
    try {
      driver = new RemoteWebDriver(getRemoteURL(), SampleApps.uiCatalogCap());

      WebElement
          element = driver.findElement(By.linkText("name=" + buttonName));
      System.out.println(element.getClass());
      Augmenter augmenter = new Augmenter();
      augmenter.addDriverAugmentation(IOSCapabilities.CONFIGURABLE, new AddIOSDriver());
      UIADriver cdriver = (UIADriver) augmenter.augment(driver);
      element = cdriver.findElement2(By.linkText("name=" + buttonName));
      System.out.println(element.getClass());

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
}
