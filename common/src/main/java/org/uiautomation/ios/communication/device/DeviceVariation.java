package org.uiautomation.ios.communication.device;

import org.openqa.selenium.WebDriverException;

public enum DeviceVariation {
  Regular, // iphone , ipad 
  Retina35,//iphone
  Retina4, // iphone
  Retina;  // ipad
  
  public static DeviceVariation getDefault() {
    return Regular;
  }

  public static DeviceVariation valueOf(Object o) {
    if (o instanceof DeviceVariation) {
      return (DeviceVariation) o;
    } else if (o instanceof String) {
      for (DeviceVariation variation : DeviceVariation.values()) {
        if (variation.toString().equals(o)) {
          return variation;
        }
      }
    }
    throw new WebDriverException("Cannot cast " + (o == null ? "null" : o.getClass()) + " to IOSDevice");
  }
}


