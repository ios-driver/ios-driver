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

package org.uiautomation.ios;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.application.APPIOSApplication;

import java.util.ArrayList;
import java.util.List;

public abstract class Device {

  private volatile boolean busy = false;

  public abstract IOSCapabilities getCapability();

  public abstract boolean canRun(APPIOSApplication app);

  public static boolean canRun(IOSCapabilities desiredCapabilities,
                               IOSCapabilities deviceCapability) {

    // if simulator, check that the requested SDK is installed.
    if (desiredCapabilities.isSimulator() && deviceCapability.isSimulator()) {

      String requestSDK = desiredCapabilities.getSDKVersion();
      if (requestSDK != null) {
        List<String> supported = toList(deviceCapability.getCapability(IOSCapabilities.UI_SDK_VERSION_ALT));
        if (supported == null || !supported.contains(requestSDK)) {
          return false;
        }
      }
    }

    if (desiredCapabilities.getCapability(IOSCapabilities.SIMULATOR) != null &&
        desiredCapabilities.isSimulator() != deviceCapability.isSimulator()) {
      return false;
    }

    if (deviceCapability.isSimulator() == false) {
      // check SDK
      if (desiredCapabilities.getSDKVersion() != null) {
        if (!deviceCapability.getSDKVersion().equals(desiredCapabilities.getSDKVersion())) {
          return false;
        }
      }
      // TODO freynaud check device family.
    }

    if (desiredCapabilities.getDeviceUUID() != null &&
        !desiredCapabilities.getDeviceUUID().equals(deviceCapability.getDeviceUUID())) {
      return false;
    }

    return true;
  }

  public synchronized Device reserve() {
    if (busy) {
      return null;
    } else {
      busy = true;
      return this;
    }
  }

  public synchronized void release() {
    busy = false;
  }

  public final boolean isBusy() {
    return busy;
  }

  /**
   * Helper method to convert a various type object to {@link List}
   *
   * @param object - object to convert to {@link List}
   * @return - {@link List} or <code>null</code> on error
   */
  @SuppressWarnings("unchecked")
  private static List<String> toList(Object object) {
    List<String> list = new ArrayList<String>();

    if (object == null || object == JSONObject.NULL) {
      return null;
    }

    if (object instanceof List) {
      return ((List<String>) object);
    } else if (object instanceof JSONArray) {
      for (int i = 0; i < ((JSONArray) object).length(); i++) {
        try {
          list.add(String.valueOf((((JSONArray) object).get(i))));
        } catch (JSONException ex) {
          //issue converting the list. return null
          return null;
        }
      }
    } else {
      list.add(object.toString());
    }

    return list;
  }

}
