package org.uiautomation.ios.server.augmenter;

import com.google.common.collect.ImmutableMap;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.AugmenterProvider;
import org.openqa.selenium.remote.ExecuteMethod;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.InterfaceImplementation;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.UIAModels.UIADriver;
import org.uiautomation.ios.client.uiamodels.impl.configuration.WebDriverLikeCommandExecutor;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;

public class AddIOSDriver implements AugmenterProvider {

  public Class<?> getDescribedInterface() {
    return UIADriver.class;
  }

  public InterfaceImplementation getImplementation(Object ignored) {
    return new InterfaceImplementation() {

      public Object invoke(ExecuteMethod exec, Object self, Method method, Object... args) {
        RemoteWebDriver driver = (RemoteWebDriver) self;
        WebDriverLikeCommandExecutor executor = new WebDriverLikeCommandExecutor(driver);
        URL remoteServer;
        if (driver.getCommandExecutor() instanceof HttpCommandExecutor) {
          HttpCommandExecutor e = (HttpCommandExecutor) driver.getCommandExecutor();
          remoteServer = e.getAddressOfRemoteServer();
        } else {
          throw new WebDriverException("ios-driver only supports http communication.");
        }

        if ("setConfiguration".equals(method.getName())) {
          WebDriverLikeCommand command = (WebDriverLikeCommand) args[0];
          String key = (String) args[1];
          Object value = args[2];

          WebDriverLikeRequest
              request =
              executor
                  .buildRequest(WebDriverLikeCommand.CONFIGURE, null, ImmutableMap.of(key, value),
                                ImmutableMap.of("command", command.toString()));
          return executor.execute(request);
        } else if ("getConfiguration".equals(method.getName())) {
          WebDriverLikeCommand command = (WebDriverLikeCommand) args[0];

          WebDriverLikeRequest request = executor.buildRequest(
              WebDriverLikeCommand.GET_CONFIGURATION,
              null,
              new HashMap<String, Object>(),
              ImmutableMap.of("command", command.toString()));

          return executor.execute(request);
        } else if ("findElement2".equals(method.getName())) {
          WebDriverLikeCommand command = (WebDriverLikeCommand) args[0];
          String key = (String) args[1];
          Object value = args[2];
          return null;
        } else {
          throw new WebDriverException(method.getName() + " isn't recognized for Configurable");
        }
      }
    };
  }
}
