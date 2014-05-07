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
package org.uiautomation.ios.server.simulator;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.server.HostInfo;
import org.uiautomation.ios.server.application.APPIOSApplication;
import org.uiautomation.ios.server.instruments.IOSDeviceManager;
import org.uiautomation.ios.server.services.Instruments;
import org.uiautomation.ios.utils.ClassicCommands;
import org.uiautomation.ios.utils.IOSVersion;
import org.uiautomation.ios.utils.SimulatorSettings;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

// java version ( simplified )
// of http://code.google.com/p/ios-sim-locale/source/browse/trunk/ios-sim-locale.m

/**
 * setting the plist file to the correct local. Tested on mac 10.7. May work on other version. The
 * assumption made is that the plist file read by the ios simulator is of binary1 format. See the
 * mac command line plutils for the formats.
 */
public class IOSSimulatorManager implements IOSDeviceManager {

  public static final String SIMULATOR_PROCESS_NAME = "iPhone Simulator";
  private static final Logger log = Logger.getLogger(IOSSimulatorManager.class.getName());
  private final List<String> sdks;
  private final String bundleId;
  private final File xcodeInstall;
  private final String desiredSDKVersion;
  private final SimulatorSettings simulatorSettings;
  private final File developer;

  /**
   * manages a single instance of the instruments process. Only 1 process can run at a given time.
   */
  public IOSSimulatorManager(IOSCapabilities capabilities,HostInfo info) {
    this.sdks = ClassicCommands.getInstalledSDKs();
    this.desiredSDKVersion = validateSDK(capabilities.getSDKVersion());

    xcodeInstall = ClassicCommands.getXCodeInstall();
    boolean is64bit = DeviceVariation.is64bit(capabilities.getDeviceVariation());
    simulatorSettings = new SimulatorSettings(info,desiredSDKVersion, is64bit);
    bundleId = capabilities.getBundleId();
    this.developer =
        new File(xcodeInstall, "/Contents/Developer/Platforms/iPhoneSimulator.platform/Developer");
  }

  private String validateSDK(String sdk) {
    if (!sdks.contains(sdk)) {
      throw new WebDriverException("desired sdk " + sdk + " isn't installed. Installed :" + sdks);
    }
    return sdk;
  }

  private boolean isSimulatorRunning() {
    return ClassicCommands.isRunning(SIMULATOR_PROCESS_NAME);
  }

  /**
   * stopping the simulator at the end of the test.
   */
  @Override
  public void cleanupDevice() {
    ClassicCommands.killall(SIMULATOR_PROCESS_NAME);
  }

  @Override
  public void setKeyboardOptions() {
    simulatorSettings.setKeyboardOptions();
  }

  @Override
  public void setMobileSafariOptions() {
    simulatorSettings.setMobileSafariOptions();
  }

  @Override
  public void setLocationPreference(boolean authorized) {
    simulatorSettings.setLocationPreference(authorized, bundleId);
  }

  @Override
  public void install(APPIOSApplication aut) {
    //no-op instruments installs automatically for simulator.
  }

  @Override
  public void setL10N(String locale, String language) {
    simulatorSettings.setL10N(locale, language);
  }

  @Override
  public void setVariation(DeviceType device, DeviceVariation variation) {
    simulatorSettings.setVariation(device, variation, desiredSDKVersion);
  }
  
  @Override
  public String getDeviceSpecification(DeviceType device, DeviceVariation variation) {
    return simulatorSettings.getDeviceSpecification(device, variation, desiredSDKVersion);
  }

  @Override
  public void resetContentAndSettings() {
    simulatorSettings.resetContentAndSettings();

  }

  @Override
  public void setSimulatorScale(String scale) {
    simulatorSettings.setSimulatorScale(scale);
  }

  @Override
  public void installTrustStore(String trustStore) {
    simulatorSettings.installTrustStore(trustStore);
  }
}
