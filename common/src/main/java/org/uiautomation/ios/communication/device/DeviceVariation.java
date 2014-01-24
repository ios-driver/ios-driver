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

package org.uiautomation.ios.communication.device;

import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.utils.IOSVersion;

public enum DeviceVariation {
  // Legacy names, preserved because they are client-visible as capability names.
  Regular,  // iPhone or iPad.
  Retina35, // iPhone retina 3.5".
  Retina4,  // iPhone retina 4".
  Retina,   // iPad retina.
  iPad25,   // iPad Mini.

  // Normalized names. The naming scheme is accretive; as new devices are introduced, the corresponding names encode
  // relevant distinguishing characteristics relative to the existing set of devices at the time of release.
  iPhone,
  iPhoneRetina,
  iPhoneRetina_4inch,
  iPhoneRetina_4inch_64bit,

  iPad,
  iPadRetina,
  iPadRetina_64bit;

  public static DeviceVariation normalize(DeviceType deviceType, DeviceVariation deviceVariation) {
    switch (deviceVariation) {
      case Regular:
        switch (deviceType) {
          case iphone:
            return iPhone;
          case ipad:
            return iPad;
        }
      case Retina35:
        return iPhoneRetina;
      case Retina4:
        return iPhoneRetina_4inch;
      case Retina:
        return iPadRetina;
      case iPad25:
        return iPad;
      default:
        return deviceVariation;
    }
  }

  public static boolean compatibleWithSDKVersion(DeviceType device, DeviceVariation deviceVariation,
                                                 String sdkVersion) {
    deviceVariation = DeviceVariation.normalize(device, deviceVariation);
    boolean isSdk7OrNewer = (new IOSVersion(sdkVersion)).isGreaterOrEqualTo("7.0");
    switch (device) {
      case iphone:
        switch (deviceVariation) {
          case iPhone:
            return !isSdk7OrNewer;
          case iPhoneRetina_4inch_64bit:
            return isSdk7OrNewer;
        }
        break;
      case ipad:
        switch (deviceVariation) {
          case iPadRetina_64bit:
            return isSdk7OrNewer;
        }
        break;
    }
    return true;
  }
  
  public static DeviceVariation getCompatibleVersion(DeviceType device, String sdkVersion) {
      boolean isSdk7OrNewer = (new IOSVersion(sdkVersion)).isGreaterOrEqualTo("7.0");
      switch (device) {
        case iphone:
          return isSdk7OrNewer? Retina4 : Regular;
        case ipad:
          return isSdk7OrNewer? Retina : Regular;
      }
      return Regular;
  }

  public static String deviceString(DeviceType deviceType, DeviceVariation deviceVariation) {
    String result;
    boolean mismatch;

    switch (normalize(deviceType, deviceVariation)) {
      case iPhone:
        result = "iPhone";
        mismatch = (deviceType != DeviceType.iphone);
        break;
      case iPhoneRetina:
        result = "iPhone Retina (3.5-inch)";
        mismatch = (deviceType != DeviceType.iphone);
        break;
      case iPhoneRetina_4inch:
        result = "iPhone Retina (4-inch)";
        mismatch = (deviceType != DeviceType.iphone);
        break;
      case iPhoneRetina_4inch_64bit:
        result = "iPhone Retina (4-inch 64-bit)";
        mismatch = (deviceType != DeviceType.iphone);
        break;
      case iPad:
        result = "iPad";
        mismatch = (deviceType != DeviceType.ipad);
        break;
      case iPadRetina:
        result = "iPad Retina";
        mismatch = (deviceType != DeviceType.ipad);
        break;
      case iPadRetina_64bit:
        result = "iPad Retina (64-bit)";
        mismatch = (deviceType != DeviceType.ipad);
        break;
      default:
        result = "";
        mismatch = true;
    }

    if (mismatch) {
      throw new WebDriverException(String.format("Unsupported device type/variation %s/%s",
          deviceType.name(), deviceVariation.name()));
    }
    return result;
  }
  
  public static boolean is64bit(DeviceVariation variation) {
    if (variation == null)
      return false;
    return variation.toString().endsWith("_64bit");
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
      throw new WebDriverException("not a valid DeviceVariation string: " + o);
    }
    throw new WebDriverException("Cannot cast " + (o == null ? "null" : o.getClass()) + " to IOSDevice");
  }
}


