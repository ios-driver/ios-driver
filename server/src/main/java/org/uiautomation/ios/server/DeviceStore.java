package org.uiautomation.ios.server;

import org.uiautomation.iosdriver.DeviceDetector;
import org.uiautomation.iosdriver.DeviceInfo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class DeviceStore implements DeviceDetector {

  private static final Logger log = Logger.getLogger(DeviceStore.class.getName());
  private final List<RealDevice> all = new CopyOnWriteArrayList<RealDevice>();

  @Override
  public void onDeviceAdded(DeviceInfo deviceInfo) {
    log.info(
        "ADDED : " + deviceInfo.getDeviceName() + ",IOS "
        + deviceInfo.getProductVersion() + "[" + deviceInfo.getUniqueDeviceID() + "]");
    all.add(new RealDevice(deviceInfo));
  }

  @Override
  public void onDeviceRemoved(DeviceInfo deviceInfo) {
    log.info(
        "REMOVED : " + deviceInfo.getDeviceName() + "[" + deviceInfo.getUniqueDeviceID() + "]");
    all.remove(new RealDevice(deviceInfo));
  }

  public List<RealDevice> getDevices() {
    return all;
  }


}
