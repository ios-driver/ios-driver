package org.uiautomation.ios.server;

import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.iosdriver.ApplicationInfo;
import org.uiautomation.iosdriver.DeviceInfo;
import org.uiautomation.iosdriver.services.DeviceInstallerException;
import org.uiautomation.iosdriver.services.DeviceInstallerService;

import java.util.List;

public class RealDevice implements Device {

  private final String uuid;
  private final DeviceType type;
  private final String name;
  private final String buildVersion;
  private final String productType;
  private final String iosVersion;
  private final DeviceInstallerService installer;

  public RealDevice(DeviceInfo info) {
    this.uuid = info.getUniqueDeviceID();
    this.name = info.getDeviceName();
    this.type = DeviceType.valueOf(info.getDeviceClass().toLowerCase());
    this.buildVersion = info.getBuildVersion();
    this.productType = info.getProductType();
    this.iosVersion = info.getProductVersion();
    installer = new DeviceInstallerService(uuid);
  }


  public String getUuid() {
    return uuid;
  }

  public DeviceType getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getIosVersion() {
    return iosVersion;
  }

  public String getProductType() {
    return productType;
  }

  public String getBuildVersion() {
    return buildVersion;
  }

  public List<ApplicationInfo> getApplications() {
    return installer.listUserApps();
  }


}

