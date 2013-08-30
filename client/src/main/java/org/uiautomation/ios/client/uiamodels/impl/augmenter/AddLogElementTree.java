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
