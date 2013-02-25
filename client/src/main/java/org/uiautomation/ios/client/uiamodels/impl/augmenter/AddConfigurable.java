package org.uiautomation.ios.client.uiamodels.impl.augmenter;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.AugmenterProvider;
import org.openqa.selenium.remote.ExecuteMethod;
import org.openqa.selenium.remote.InterfaceImplementation;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.configuration.WebDriverLikeCommandExecutor;
import org.uiautomation.ios.communication.WebDriverLikeCommand;

import java.lang.reflect.Method;

public class AddConfigurable implements AugmenterProvider {

  public Class<?> getDescribedInterface() {
    return Configurable.class;
  }

  public InterfaceImplementation getImplementation(Object ignored) {
    return new InterfaceImplementation() {

      public Object invoke(ExecuteMethod exec, Object self, Method method, Object... args) {
        RemoteWebDriver driver = (RemoteWebDriver) self;
        WebDriverLikeCommandExecutor executor = new WebDriverLikeCommandExecutor(driver);

        if ("setConfiguration".equals(method.getName())) {
          WebDriverLikeCommand command = (WebDriverLikeCommand) args[0];
          String key = (String) args[1];
          Object value = args[2];
          RemoteUIADriver.setConfiguration(executor, command, key, value);
          return null;
        } else if ("getConfiguration".equals(method.getName())) {
          WebDriverLikeCommand command = (WebDriverLikeCommand) args[0];
          return RemoteUIADriver.getConfiguration(executor, command);
        } else {
          throw new WebDriverException(method.getName() + " isn't recognized for Configurable");
        }
      }
    };
  }
}
