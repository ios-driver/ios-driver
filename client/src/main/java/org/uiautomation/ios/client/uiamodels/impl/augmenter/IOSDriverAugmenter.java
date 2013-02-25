package org.uiautomation.ios.client.uiamodels.impl.augmenter;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.uiautomation.ios.IOSCapabilities;

public class IOSDriverAugmenter {

  public static <T> T augment(WebDriver driver) {
    Augmenter augmenter = new Augmenter();
    augmenter.addDriverAugmentation(IOSCapabilities.CONFIGURABLE, new AddConfigurable());
    augmenter.addDriverAugmentation(IOSCapabilities.ELEMENT_TREE, new AddLogElementTree());
    augmenter.addDriverAugmentation(IOSCapabilities.IOS_SEARCH_CONTEXT, new AddIOSSearchContext());
    return (T) augmenter.augment(driver);
  }
}
