package org.uiautomation.ios.e2e.config;

import org.openqa.selenium.By;
import org.openqa.selenium.Rotatable;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIATableCell;
import org.uiautomation.ios.client.uiamodels.impl.augmenter.Configurable;
import org.uiautomation.ios.client.uiamodels.impl.augmenter.ElementTree;
import org.uiautomation.ios.client.uiamodels.impl.augmenter.IOSDriverAugmenter;
import org.uiautomation.ios.client.uiamodels.impl.augmenter.IOSSearchContext;
import org.uiautomation.ios.communication.WebDriverLikeCommand;

import java.util.Map;

public class AugmenterTest extends BaseIOSDriverTest {

  RemoteWebDriver driver;

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
  public void configuration() {
    Configurable config = IOSDriverAugmenter.augment(driver);
    config.setConfiguration(WebDriverLikeCommand.ACCEPT_ALERT, "ok", true);
    Map<String, Object> conf = config.getConfiguration(WebDriverLikeCommand.ACCEPT_ALERT);
    Assert.assertEquals(conf.get("ok"), true);
  }

  private String buttonName = "Buttons, Various uses of UIButton";

  @Test
  public void iosSearchContext() {
    IOSSearchContext finder = IOSDriverAugmenter.augment(driver);
    WebElement element = finder.findElement(new NameCriteria(buttonName));
    Assert.assertTrue(element instanceof UIATableCell);
  }

  @Test
  public void logElementTree() {
    WebElement
        element = driver.findElement(By.linkText("name=" + buttonName));
    System.out.println(element.getClass());
    ElementTree tree = IOSDriverAugmenter.augment(driver);
    System.out.println(tree.logElementTree(null, false));
  }

  @Test
  public void rotatable() {
    Rotatable rotator = IOSDriverAugmenter.augment(driver);
    rotator.rotate(ScreenOrientation.PORTRAIT);
  }

  @Test
  public void cast() {
    RemoteUIADriver iosdriver = IOSDriverAugmenter.getIOSDriver(driver);
    WebElement
        element = iosdriver.findElement(By.linkText("name=" + buttonName));
    Assert.assertTrue(element instanceof RemoteUIATableCell);
  }
}
