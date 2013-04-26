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

package org.uiautomation.ios.server.instruments;

import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;

import java.io.File;

public interface IOSDeviceManager {

  public void setL10N(String locale, String language);

  public void resetContentAndSettings();

  public void cleanupDevice();

  public void setKeyboardOptions();

  void setLocationPreference(boolean authorized);

  void setVariation(DeviceType device, DeviceVariation variation);

  String getInstrumentsClient();

  void setMobileSafariOptions();
}
