package org.uiautomation.ios.server;

import org.uiautomation.iosdriver.DeviceDetector;
import org.uiautomation.iosdriver.DeviceInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class DeviceStore implements DeviceDetector {

  private static final Logger log = Logger.getLogger(DeviceStore.class.getName());
  private final List<RealDevice> reals = new CopyOnWriteArrayList<RealDevice>();
  private final List<SimulatorDevice> sims = new CopyOnWriteArrayList<SimulatorDevice>();

  @Override
  public void onDeviceAdded(DeviceInfo deviceInfo) {
    log.info(
        "ADDED : " + deviceInfo.getDeviceName() + ",IOS "
        + deviceInfo.getProductVersion() + "[" + deviceInfo.getUniqueDeviceID() + "]");
    reals.add(new RealDevice(deviceInfo));
  }

  @Override
  public void onDeviceRemoved(DeviceInfo deviceInfo) {
    log.info(
        "REMOVED : " + deviceInfo.getDeviceName() + "[" + deviceInfo.getUniqueDeviceID() + "]");
    reals.remove(new RealDevice(deviceInfo));
  }

  public List<Device> getDevices() {
    List<Device> all = new ArrayList<Device>();
    all.addAll(reals);
    all.addAll(sims);
    return all;
  }

  public List<RealDevice> getRealDevices() {
    return reals;
  }

  public List<SimulatorDevice> getSimulatorDevices() {
    return sims;
  }


  public void add(SimulatorDevice simulatorDevice) {
    sims.add(simulatorDevice);
  }


}
