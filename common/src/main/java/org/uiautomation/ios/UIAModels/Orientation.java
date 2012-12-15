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
package org.uiautomation.ios.UIAModels;


public enum Orientation {
  PORTRAIT("UIA_DEVICE_ORIENTATION_PORTRAIT", 1, 0),
  UIA_DEVICE_ORIENTATION_PORTRAIT_UPSIDEDOWN("UIA_DEVICE_ORIENTATION_PORTRAIT_UPSIDEDOWN", 2, 180),
  LANDSCAPE("UIA_DEVICE_ORIENTATION_LANDSCAPELEFT", 3, -90),
  UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT("UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT", 4, 90),
  UIA_DEVICE_ORIENTATION_FACEUP("UIA_DEVICE_ORIENTATION_FACEUP", -1, -1),
  UIA_DEVICE_ORIENTATION_FACEDOWN("UIA_DEVICE_ORIENTATION_FACEDOWN", -1, -1);


  private final String value;
  private final int interfaceOrientation;
  private final int rotationInDegree;

  private Orientation(String value, int interfaceOrientation, int rotationInDegree) {
    this.interfaceOrientation = interfaceOrientation;
    this.rotationInDegree = rotationInDegree;
    this.value = value;
  }

  public int getRotationInDegree() {
    return rotationInDegree;
  }

  public static Orientation fromInterfaceOrientation(int interfaceOrientation) {
    for (Orientation o : Orientation.values()) {
      if (o.interfaceOrientation == interfaceOrientation) {
        return o;
      }
    }
    throw new IllegalArgumentException("interfaceOrientation not recognized.");
  }


  public String instrumentsValue() {
    return value;
  }
}
