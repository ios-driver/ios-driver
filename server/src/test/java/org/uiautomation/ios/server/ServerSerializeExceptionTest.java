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

package org.uiautomation.ios.server;

import org.openqa.selenium.WebDriverException;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;

public class ServerSerializeExceptionTest extends BaseIOSDriverTest {

  @Test(expectedExceptions = WebDriverException.class)
  public void clientGetsServerException() {
    RemoteIOSDriver driver = null;
    try {
      IOSCapabilities cap = SampleApps.intlMountainsCap("de");
      driver = new RemoteIOSDriver(getRemoteURL(), cap);

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }

  }

}
