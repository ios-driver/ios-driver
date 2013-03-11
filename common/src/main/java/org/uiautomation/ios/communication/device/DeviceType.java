package org.uiautomation.ios.communication.device;

import org.openqa.selenium.WebDriverException;

public enum DeviceType {
  iphone(1), ipad(2), ipod(1);

  public int getDeviceFamily() {
    return deviceFamily;
  }

  private final int deviceFamily;

  private DeviceType(int deviceFamily) {
    this.deviceFamily = deviceFamily;
  }

  public static DeviceType valueOf(Object o) {
    if (o instanceof DeviceType) {
      return (DeviceType) o;
    } else if (o instanceof String) {
      for (DeviceType device : DeviceType.values()) {
        if (device.toString().equals(o)) {
          return device;
        }
      }
    }
    throw new WebDriverException(
        "Cannot cast " + (o == null ? "null" : o.getClass() + "=" + o) + " to DeviceType");
  }

  public static DeviceType getFromFamilyCode(int deviceFamily) {
    for (DeviceType d : DeviceType.values()) {
      if (d.deviceFamily == deviceFamily) {
        return d;
      }
    }
    throw new WebDriverException("device family not recognized : " + deviceFamily);
  }

}
