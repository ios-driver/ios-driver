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

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.server.application.APPIOSApplication;

public abstract class Device {

  private volatile boolean busy = false;

  public abstract IOSCapabilities getCapability();

  public abstract boolean canRun(APPIOSApplication app);

  public static boolean canRun(IOSCapabilities desiredCapabilities,
                               IOSCapabilities deviceCapability) {

    if (desiredCapabilities.getCapability(IOSCapabilities.SIMULATOR) != null &&
        desiredCapabilities.isSimulator() != deviceCapability.isSimulator()) {
      return false;
    }

    if (deviceCapability.isSimulator() == false) {
      // check SDK
      if (desiredCapabilities.getSDKVersion() != null) {
        if (!deviceCapability.getSDKVersion().equals(desiredCapabilities.getSDKVersion())) {
          return false;
        }
      }
      // TODO freynaud check device family.
    }

    if (desiredCapabilities.getDeviceUUID() != null &&
        !desiredCapabilities.getDeviceUUID().equals(deviceCapability.getDeviceUUID())) {
      return false;
    }

    return true;
  }

  public synchronized Device reserve() {
    if (busy) {
      return null;
    } else {
      busy = true;
      return this;
    }
  }

  public synchronized void release() {
    busy = false;
  }
  
  public final boolean isBusy() {
      return busy;
  }
}
