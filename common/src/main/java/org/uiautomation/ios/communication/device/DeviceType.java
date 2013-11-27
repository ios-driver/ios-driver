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
      throw new WebDriverException("not a matching DeviceType string: " + o);
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