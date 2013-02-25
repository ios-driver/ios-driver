package org.uiautomation.ios.client.uiamodels.impl.augmenter;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.AugmenterProvider;
import org.openqa.selenium.remote.ExecuteMethod;
import org.openqa.selenium.remote.InterfaceImplementation;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.client.uiamodels.impl.configuration.WebDriverLikeCommandExecutor;

import java.lang.reflect.Method;

public class AddIOSSearchContext implements AugmenterProvider {

  public Class<?> getDescribedInterface() {
    return IOSSearchContext.class;
  }

  public InterfaceImplementation getImplementation(Object ignored) {
    return new InterfaceImplementation() {

      public Object invoke(ExecuteMethod exec, Object self, Method method, Object... args) {
        RemoteWebDriver driver = (RemoteWebDriver) self;
        WebDriverLikeCommandExecutor executor = new WebDriverLikeCommandExecutor(driver);
        Criteria criteria = (Criteria) args[0];

        if ("findElement".equals(method.getName())) {
          return RemoteIOSDriver.findElement(executor, criteria);

        } else if ("findElements".equals(method.getName())) {
          return RemoteIOSDriver.findElements(executor, criteria);
        } else {
          throw new WebDriverException(method.getName() + " isn't recognized for Configurable");
        }
      }
    };
  }
}


