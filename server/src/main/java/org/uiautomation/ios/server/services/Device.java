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

package org.uiautomation.ios.server.services;

import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;

public class Device {

  private final DeviceType type;
  private final DeviceVariation variation;
  private final SDKVersion sdkVersion;

  public Device(DeviceType type, DeviceVariation variation, SDKVersion version) {
    this.type = type;
    this.variation = variation;
    this.sdkVersion = version;
    validate();
  }

  public static Device iphone4S(SDKVersion version) {
    return new Device(DeviceType.iphone, DeviceVariation.iPhoneRetina, version);
  }

  private void validate() {

  }

}
