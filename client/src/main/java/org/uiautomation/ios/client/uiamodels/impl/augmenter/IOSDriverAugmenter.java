package org.uiautomation.ios.client.uiamodels.impl.augmenter;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.AttachRemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;

public class IOSDriverAugmenter {

  public static <T> T augment(WebDriver driver) {
    Augmenter augmenter = new Augmenter();
    augmenter.addDriverAugmentation(IOSCapabilities.CONFIGURABLE, new AddConfigurable());
    augmenter.addDriverAugmentation(IOSCapabilities.ELEMENT_TREE, new AddLogElementTree());
    augmenter.addDriverAugmentation(IOSCapabilities.IOS_SEARCH_CONTEXT, new AddIOSSearchContext());
    return (T) augmenter.augment(driver);
  }

  public static RemoteUIADriver getIOSDriver(RemoteWebDriver driver) {
    if (!(driver.getCommandExecutor() instanceof HttpCommandExecutor)) {
      throw new WebDriverException("ios only supports http communication.");
    }
    HttpCommandExecutor e = (HttpCommandExecutor) driver.getCommandExecutor();
    RemoteUIADriver
        attach =
        new AttachRemoteUIADriver(e.getAddressOfRemoteServer(), driver.getSessionId());
    return attach;
  }
}
