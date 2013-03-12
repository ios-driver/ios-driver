package org.uiautomation.ios.server;

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.server.application.APPIOSApplication;

public abstract class Device {

  private volatile boolean busy = false;

  public abstract IOSCapabilities getCapability();

  public abstract boolean canRun(APPIOSApplication app);

  public static boolean canRun(IOSCapabilities desiredCapabilities,
                               IOSCapabilities deviceCapability) {

    if (desiredCapabilities.isSimulator() != deviceCapability.isSimulator()) {
      return false;
    }

    if (!deviceCapability.isSimulator()) {
      // check SDK
      if (desiredCapabilities.getSDKVersion() != null) {
        if (!deviceCapability.getSDKVersion().equals(desiredCapabilities.getSDKVersion())) {
          return false;
        }
      }
      // TODO freynaud check device family.
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
}
