package org.uiautomation.ios.client.uiamodels.impl.augmenter;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.AttachRemoteIOSDriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;

public class IOSDriverAugmenter {

  /**
   * Add all the IOS specific interfaces ios-driver supports.
   */
  public static <T> T augment(WebDriver driver) {
    Augmenter augmenter = new Augmenter();
    augmenter.addDriverAugmentation(IOSCapabilities.CONFIGURABLE, new AddConfigurable());
    augmenter.addDriverAugmentation(IOSCapabilities.ELEMENT_TREE, new AddLogElementTree());
    augmenter.addDriverAugmentation(IOSCapabilities.IOS_SEARCH_CONTEXT, new AddIOSSearchContext());
    augmenter.addDriverAugmentation(IOSCapabilities.IOS_TOUCH_SCREEN, new AddIOSTouchScreen());
    return (T) augmenter.augment(driver);
  }


  /*
   * Most actions can be performed using the normal augmented driver, but for findElement, the
   * findElement(s) must be override, so need to create a new object from scratch.
   */
  public static RemoteIOSDriver getIOSDriver(RemoteWebDriver driver) {
    if (!(driver.getCommandExecutor() instanceof HttpCommandExecutor)) {
      throw new WebDriverException("ios only supports http communication.");
    }
    HttpCommandExecutor e = (HttpCommandExecutor) driver.getCommandExecutor();
    RemoteIOSDriver
        attach =
        new AttachRemoteIOSDriver(e.getAddressOfRemoteServer(), driver.getSessionId());
    return attach;
  }
}
