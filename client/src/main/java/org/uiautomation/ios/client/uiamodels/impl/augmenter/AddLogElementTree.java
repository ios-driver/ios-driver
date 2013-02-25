package org.uiautomation.ios.client.uiamodels.impl.augmenter;

import org.openqa.selenium.remote.AugmenterProvider;
import org.openqa.selenium.remote.ExecuteMethod;
import org.openqa.selenium.remote.InterfaceImplementation;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.client.uiamodels.impl.configuration.WebDriverLikeCommandExecutor;

import java.io.File;
import java.lang.reflect.Method;

public class AddLogElementTree implements AugmenterProvider {

  public Class<?> getDescribedInterface() {
    return ElementTree.class;
  }

  public InterfaceImplementation getImplementation(Object ignored) {
    return new InterfaceImplementation() {

      public Object invoke(ExecuteMethod exec, Object self, Method method, Object... args) {
        RemoteWebDriver driver = (RemoteWebDriver) self;
        WebDriverLikeCommandExecutor executor = new WebDriverLikeCommandExecutor(driver);

        // logElementTree only has 1 method.
        File screenshot = (File) args[0];
        Boolean translation = (Boolean) args[1];

        return RemoteIOSDriver.logElementTree(executor, screenshot, translation);
      }
    };
  }

}
