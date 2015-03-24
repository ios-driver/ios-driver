/*
 * Copyright 2012-2014 eBay Software Foundation and ios-driver committers
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

package org.uiautomation.ios.setup;


import org.libimobiledevice.ios.driver.binding.exceptions.SDKException;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.ServerSideSession;

import java.io.NotActiveException;

public class IOSDeviceManagerFactory {


  public static IOSDeviceManager create(ServerSideSession session) throws NotActiveException {
    boolean sim = session.getCapabilities().isSimulator();
    boolean safari = session.getApplication().isSafari();

    if (sim) {
      if (safari) {
        return new IOSSafariSimulatorManager(session);
      } else {
        return new IOSSimulatorManager(session);
      }
    } else {
      try {
        return new IOSRealDeviceManager(session);
      } catch (SDKException e) {
        throw new WebDriverException(e);
      }
    }
  }
}
