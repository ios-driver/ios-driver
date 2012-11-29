/*
 * Copyright 2012 ios-driver committers.
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
package org.uiautomation.ios.mobileSafari;

import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.communication.IOSDevice;

public class SafariHelper {

  private final IOSDevice device;
  private final UIARect rect;

  public SafariHelper(IOSDevice device) {
    this.device = device;
    rect = getOffsets(device);
  }

  // x, y , h , w
  // x = no used
  // y = height of the address bar area
  // h = height of the device
  // w = width of the device
  // so for portait, the html page size = h-y
  // for landscape size = w-y
  private UIARect getOffsets(IOSDevice device) {
    switch (device) {
    case iPhoneSimulator:
      return new UIARect(0, 80, 480, 320);
    case iPadSimulator:
      return new UIARect(0, 96, 1024, 768);
    default:
      throw new RuntimeException(device + "offset not done");
    }
  }

  public int getPageWidth(Orientation o) {
    switch (o) {
    case UIA_DEVICE_ORIENTATION_PORTRAIT:
    case UIA_DEVICE_ORIENTATION_PORTRAIT_UPSIDEDOWN:
      return rect.getWidth();
    case UIA_DEVICE_ORIENTATION_LANDSCAPELEFT:
    case UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT:
      return rect.getHeight();
    default:
      throw new RuntimeException("NI" + o);
    }
  }

  public int getAddressBarHeigth() {
    return rect.getY();
  }
}
