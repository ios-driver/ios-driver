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

public enum DeviceVariation {
  Regular, // iphone , ipad 
  Retina35,//iphone
  Retina4, // iphone
  Retina,  // ipad
  iPad25; // ipad mini
  
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


