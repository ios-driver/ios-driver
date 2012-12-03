package org.uiautomation.ios.communication.device;

import org.openqa.selenium.WebDriverException;

public enum Device {
  iphone(1), ipad(2);

  public int getDeviceFamily() {
    return deviceFamily;
  }

  private final int deviceFamily;

  private Device(int deviceFamily) {
    this.deviceFamily = deviceFamily;
  }

  public static Device valueOf(Object o) {
    if (o instanceof Device) {
      return (Device) o;
    } else if (o instanceof String) {
      for (Device device : Device.values()) {
        if (device.toString().equals(o)) {
          return device;
        }
      }
    }
    throw new WebDriverException("Cannot cast " + (o == null ? "null" : o.getClass()+"="+o) + " to Device");
  }

  public static Device getFromFamilyCode(int deviceFamily) {
    for (Device d : Device.values()) {
      if (d.deviceFamily == deviceFamily) {
        return d;
      }
    }
    throw new WebDriverException("device family not recognized : " + deviceFamily);
  }

}
