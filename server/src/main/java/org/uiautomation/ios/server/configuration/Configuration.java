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

package org.uiautomation.ios.server.configuration;

import org.openqa.selenium.WebDriverException;

public class Configuration {

  // TODO freynaud extract to method checking sdk ?
  public static boolean SIMULATORS_ENABLED = false;
  public static boolean BETA_FEATURE = false;

  public static void off() {
    throw new WebDriverException("You need to enable beta feature.");
  }
}
