package org.uiautomation.ios.server;

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.application.IPAApplication;
import org.uiautomation.ios.utils.ClassicCommands;

public class SimulatorDevice extends Device {

  @Override
  public IOSCapabilities getCapability() {
    IOSCapabilities res = new IOSCapabilities();
    res.setCapability(IOSCapabilities.SIMULATOR, true);
    res.setCapability(IOSCapabilities.UI_SDK_VERSION, ClassicCommands.getDefaultSDK());
    res.setCapability(IOSCapabilities.UI_SDK_VERSION + "_Alt",
                      new String[]{"5.0", "5.1", "6.0", "6.1"});
    res.setCapability(IOSCapabilities.DEVICE, DeviceType.iphone);
    res.setCapability(IOSCapabilities.DEVICE + "_Alt",
                      new DeviceType[]{DeviceType.iphone, DeviceType.ipad});

    return res;
  }

  @Override
  public boolean canRun(IOSApplication app) {
    if (app instanceof IPAApplication) {
      return false;
    }
    return true;
  }
}
