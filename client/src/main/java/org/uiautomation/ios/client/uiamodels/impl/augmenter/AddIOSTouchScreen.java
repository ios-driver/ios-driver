/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uiautomation.ios.client.uiamodels.impl.augmenter;

import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.AugmenterProvider;
import org.openqa.selenium.remote.ExecuteMethod;
import org.openqa.selenium.remote.InterfaceImplementation;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.client.uiamodels.impl.configuration.WebDriverLikeCommandExecutor;

import java.lang.reflect.Method;

public class AddIOSTouchScreen implements AugmenterProvider {
  @Override
  public Class<?> getDescribedInterface() {
    return IOSTouchScreen.class;
  }

  @Override
  public InterfaceImplementation getImplementation(Object value) {
    return new InterfaceImplementation() {

      public Object invoke(ExecuteMethod exec, Object self, Method method, Object... args) {
        RemoteWebDriver driver = (RemoteWebDriver) self;
        WebDriverLikeCommandExecutor executor = new WebDriverLikeCommandExecutor(driver);

        if ("dragFromToForDuration".equals(method.getName())) {
          Point from = (Point) args[0];
          Point to = (Point) args[1];
          int duration = (Integer) args[2];
          RemoteIOSDriver.dragFromToForDuration(executor, from, to, duration);
          return null;
        } else if ("pinchCloseFromToForDuration".equals(method.getName())) {
          Point from = (Point) args[0];
          Point to = (Point) args[1];
          int duration = (Integer) args[2];
          RemoteIOSDriver.pinchCloseFromToForDuration(executor, from, to, duration);
          return null;
        } else if ("pinchOpenFromToForDuration".equals(method.getName())) {
          Point from = (Point) args[0];
          Point to = (Point) args[1];
          int duration = (Integer) args[2];
          RemoteIOSDriver.pinchOpenFromToForDuration(executor, from, to, duration);
          return null;
        } else {
          throw new WebDriverException(method.getName() + " isn't recognized for IOSTouchScreen");
        }
      }
    };
  }
}
