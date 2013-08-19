package org.uiautomation.ios.server;

import com.google.common.collect.ImmutableList;

import org.libimobiledevice.DeviceDetectionCallback;
import org.libimobiledevice.binding.raw.DeviceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class DeviceStore implements DeviceDetectionCallback {

  private static final Logger log = Logger.getLogger(DeviceStore.class.getName());
  private final List<RealDevice> reals = new CopyOnWriteArrayList<RealDevice>();
  private final List<SimulatorDevice> sims = new CopyOnWriteArrayList<SimulatorDevice>();


  @Override
  public void onAdded(String uuid) {
    DeviceInfo info = new DeviceInfo(uuid);
    RealDevice d = new RealDevice(info);
    log.info("new device detected (" + uuid + ")" + info.getDeviceName());
    reals.add(d);
  }

  @Override
  public void onRemoved(String uuid) {
    for (RealDevice d : reals) {
      if (d.getUuid().equals(uuid)) {
        log.info("Removing " + uuid + " for the devices pool");
        reals.remove(d);

      }
    }
    log.warning("device " + uuid + " has been unplugged, but was never there ?");
  }

  /**
   * @return immutable copy of the currently available devices.
   */
  public List<Device> getDevices() {
    List<Device> all = new ArrayList<Device>();
    all.addAll(reals);
    all.addAll(sims);
    return ImmutableList.copyOf(all);
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
