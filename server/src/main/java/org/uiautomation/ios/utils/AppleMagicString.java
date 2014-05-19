/*
 * Copyright 2012-2014 eBay Software Foundation and ios-driver committers
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

package org.uiautomation.ios.utils;


import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;

public class AppleMagicString {

  public static String getDeviceSpecification(DeviceType device, DeviceVariation variation,
                                       String desiredSDKVersion) {
    // i.e. 'iPad Retina (64-bit) - Simulator - iOS 7.1'
    IOSVersion iosVersion = new IOSVersion(desiredSDKVersion);
    String version = iosVersion.getMajor() + '.' + iosVersion.getMinor();
    return getSimulateDeviceValue(device, variation, desiredSDKVersion) + " - Simulator - iOS "
           + version;
  }

  public static  String getSimulateDeviceValue(DeviceType device, DeviceVariation variation,
                                        String desiredSDKVersion)
      throws WebDriverException {
    if (!DeviceVariation.compatibleWithSDKVersion(device, variation, desiredSDKVersion)) {
      DeviceVariation
          compatibleVariation =
          DeviceVariation.getCompatibleVersion(device, desiredSDKVersion);
      throw new WebDriverException(
          String.format("%s variation incompatible with SDK %s, a compatible variation is %s",
                        DeviceVariation.deviceString(device, variation),
                        desiredSDKVersion, compatibleVariation));
    }
    return DeviceVariation.deviceString(device, variation);
  }
}
