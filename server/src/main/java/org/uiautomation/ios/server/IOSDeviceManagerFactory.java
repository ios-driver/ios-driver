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

package org.uiautomation.ios.server;


import org.libimobiledevice.ios.driver.binding.exceptions.LibImobileException;
import org.libimobiledevice.ios.driver.binding.exceptions.SDKException;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.server.instruments.IOSDeviceManager;
import org.uiautomation.ios.server.simulator.IOSRealDeviceManager;
import org.uiautomation.ios.server.simulator.IOSSafariSimulatorManager;
import org.uiautomation.ios.server.simulator.IOSSimulatorManager;

public class IOSDeviceManagerFactory {


  public static IOSDeviceManager create(ServerSideSession session) {
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
      } catch (LibImobileException e) {
        e.printStackTrace();
      } catch (SDKException e) {
        throw new WebDriverException(e);
      }
    }

    throw new WebDriverException("Cannot instantiate the DeviceManager for " + session);
  }
}
