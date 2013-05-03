package org.uiautomation.ios.server;

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.server.application.APPIOSApplication;
import org.uiautomation.ios.server.application.IPAApplication;
import org.uiautomation.iosdriver.ApplicationInfo;
import org.uiautomation.iosdriver.DeviceInfo;
import org.uiautomation.iosdriver.services.DeviceInstallerService;

import java.util.List;

public class RealDevice extends Device {

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
    return installer.getUserApplications();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    RealDevice that = (RealDevice) o;

    if (!uuid.equals(that.uuid)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }

  @Override
  public IOSCapabilities getCapability() {
    IOSCapabilities res = new IOSCapabilities();
    res.setCapability(IOSCapabilities.SIMULATOR, false);
    res.setCapability(IOSCapabilities.UI_SDK_VERSION, iosVersion);
    res.setCapability(IOSCapabilities.DEVICE, type);
    //res.setCapability(IOSCapabilities.VARIATION, DeviceVariation.valueOf(this.productType.replace(",", "")));
    res.setCapability(IOSCapabilities.UUID, uuid);
    return res;
  }

  @Override
  public boolean canRun(APPIOSApplication app) {
    if (!(app instanceof IPAApplication)) {
      return false;
    }
    return true;
  }
}

