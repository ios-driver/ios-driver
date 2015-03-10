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
package org.uiautomation.ios.setup;

import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.HostInfo;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.application.IOSRunningApplication;
import org.uiautomation.ios.utils.ClassicCommands;
import org.uiautomation.ios.utils.IOSVersion;
import org.uiautomation.ios.utils.SimulatorSettings;

import java.io.File;
import java.io.NotActiveException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

// java version ( simplified )
// of http://code.google.com/p/ios-sim-locale/source/browse/trunk/ios-sim-locale.m

/**
 * setting the plist file to the correct local. Tested on mac 10.7. May work on other version. The
 * assumption made is that the plist file read by the ios simulator is of binary1 format. See the
 * mac command line plutils for the formats.
 */
public class IOSSimulatorManager implements IOSDeviceManager {

  private static final Logger log = Logger.getLogger(IOSSimulatorManager.class.getName());

  public static final String SIMULATOR_PROCESS_NAME = "iPhone Simulator";
  private final List<String> sdks;
  private final String bundleId;
  private final File xcodeInstall;
  private final String desiredSDKVersion;
  protected final SimulatorSettings simulatorSettings;
  private final IOSCapabilities caps;
  protected final ServerSideSession session;
  private final HostInfo info;

  /**
   * manages a single instance of the instruments process. Only 1 process can run at a given time.
   */
  public IOSSimulatorManager(ServerSideSession session) throws NotActiveException {
    this.session = session;
    this.info = session.getIOSServerManager().getHostInfo();
    this.sdks = ClassicCommands.getInstalledSDKs();
    this.caps = session.getCapabilities();
    this.desiredSDKVersion = validateSDK(caps.getSDKVersion());

    xcodeInstall = ClassicCommands.getXCodeInstall();
    boolean is64bit = DeviceVariation.is64bit(caps.getDeviceVariation());
    DeviceType deviceType = caps.getDevice();
    DeviceVariation variation = caps.getDeviceVariation();
    simulatorSettings = new SimulatorSettings(info, desiredSDKVersion, is64bit, deviceType, variation);
    bundleId = caps.getBundleId();
  }


  @Override
  public void setup() {
    DeviceType deviceType = caps.getDevice();
    IOSRunningApplication application = session.getApplication();
    String locale = caps.getLocale();
    String language = caps.getLanguage();
    IOSVersion instrumentsVersion = new IOSVersion(info.getInstrumentsVersion().getVersion());
    boolean instrumentsIs50OrHigher = instrumentsVersion.isGreaterOrEqualTo("5.0");

    boolean putDefaultFirst = instrumentsIs50OrHigher;

    simulatorSettings.setSimulatorScale(caps.getSimulatorScale());

    if (!instrumentsVersion.isGreaterOrEqualTo("6.0")) {
      application.setDefaultDevice(deviceType, putDefaultFirst);
    }

    if (!caps.getReuseContentAndSettings()) {
      simulatorSettings.resetContentAndSettings();
    }
    for (Map.Entry<String, byte[]> entry : caps.getBootstrapFiles().entrySet()) {
      simulatorSettings.writeContentFile(entry.getKey(), entry.getValue());
    }
    simulatorSettings.setL10N(locale, language);
    simulatorSettings.setKeyboardOptions();
    simulatorSettings.setLocationPreference(/*whitelist*/true, bundleId);
    
    // Setting the Accessibility Preferences
    simulatorSettings.setAccessibilityOptions();
  }

  @Override
  public void teardown() {
    String simulatorName = info.getInstrumentsVersion().getMajor() < 6 ? SIMULATOR_PROCESS_NAME : "iOS Simulator";
    ClassicCommands.killall(simulatorName);
  }


  private String validateSDK(String sdk) {
    if (!sdks.contains(sdk)) {
      throw new WebDriverException("desired sdk " + sdk + " isn't installed. Installed :" + sdks);
    }
    return sdk;
  }




}
