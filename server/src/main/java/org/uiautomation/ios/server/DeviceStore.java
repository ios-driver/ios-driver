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

import com.google.common.collect.ImmutableList;

import org.libimobiledevice.ios.driver.binding.exceptions.LibImobileException;
import org.libimobiledevice.ios.driver.binding.exceptions.SDKException;
import org.libimobiledevice.ios.driver.binding.model.ApplicationInfo;
import org.libimobiledevice.ios.driver.binding.model.DeviceInfo;
import org.libimobiledevice.ios.driver.binding.services.DeviceCallBack;
import org.libimobiledevice.ios.driver.binding.services.DeviceService;
import org.libimobiledevice.ios.driver.binding.services.IOSDevice;
import org.libimobiledevice.ios.driver.binding.services.InstallerService;
import org.uiautomation.ios.server.application.IPAShellApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class DeviceStore extends DeviceCallBack {

  private static final Logger log = Logger.getLogger(DeviceStore.class.getName());
  private final List<RealDevice> reals = new CopyOnWriteArrayList<RealDevice>();
  private final List<SimulatorDevice> sims = new CopyOnWriteArrayList<SimulatorDevice>();
  private final ApplicationStore apps;
  private final Set<String> uuidWhitelist;

  public DeviceStore(ApplicationStore apps, Set<String> uuidWhitelist) {
    super();
    this.apps = apps;
    this.uuidWhitelist = uuidWhitelist;
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


  @Override
  protected void onDeviceAdded(String uuid) {
    if (!uuidWhitelist.isEmpty() && !uuidWhitelist.contains(uuid)) {
      log.info("device detected but not whitelisted");
      return;
    }

    try {
      IOSDevice device = DeviceService.get(uuid);
      DeviceInfo info = new DeviceInfo(uuid);
      RealDevice d = new RealDevice(info);
      log.info("new device detected (" + uuid + ") " + info.getDeviceName());
      reals.add(d);
      InstallerService s = new InstallerService(device);
      String id = "com.apple.mobilesafari";
      ApplicationInfo safari = s.getApplication(id);
      String v = (String) safari.getProperty("CFBundleVersion");
      log.info("device " + info.getDeviceName() + " = safari " + v);

      IPAShellApplication ipa = new IPAShellApplication(id, v,safari);
      apps.add(ipa);

    } catch (SDKException e) {
      e.printStackTrace();
    } catch (LibImobileException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void onDeviceRemoved(String uuid) {
    if (!uuidWhitelist.isEmpty() && !uuidWhitelist.contains(uuid)) {
      log.info("device removed but not whitelisted");
      return;
    }

    for (RealDevice d : reals) {
      if (d.getUuid().equals(uuid)) {
        log.info("Removing " + uuid + " for the devices pool");
        reals.remove(d);

      }
    }
    log.warning("device " + uuid + " has been unplugged, but was never there ?");
  }
}
