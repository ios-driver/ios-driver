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

import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.server.application.APPIOSApplication;
import org.uiautomation.ios.server.instruments.IOSDeviceManager;
import org.uiautomation.ios.server.utils.IOSVersion;
import org.uiautomation.ios.utils.ClassicCommands;
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
  private final Instruments instruments;
  private final File developer;

  /**
   * manages a single instance of the instruments process. Only 1 process can run at a given time.
   */
  public IOSSimulatorManager(IOSCapabilities capabilities, Instruments instruments) {

    this.sdks = ClassicCommands.getInstalledSDKs();
    this.desiredSDKVersion = validateSDK(capabilities.getSDKVersion());

    xcodeInstall = ClassicCommands.getXCodeInstall();
    simulatorSettings = new SimulatorSettings(desiredSDKVersion);
    bundleId = capabilities.getBundleId();
    this.instruments = instruments;
    this.developer =
        new File(xcodeInstall, "/Contents/Developer/Platforms/iPhoneSimulator.platform/Developer");

  }

  @Override
  public void setSDKVersion() {
    if (!isDefaultSDK()) {
      //warmup();
      forceDefaultSDK();
    }
  }

  private boolean isDefaultSDK() {
    String defaultSDK = ClassicCommands.getDefaultSDK();
    if (new IOSVersion(defaultSDK).equals(desiredSDKVersion)) {
      return true;
    }
    return false;
  }

  private void warmup() {
    if (instruments instanceof InstrumentsApple) {
      ((InstrumentsApple) instruments).startWithDummyScript();
    } else {
      throw new RuntimeException("NI");
    }
  }

  private void forceDefaultSDK() {

    for (String version : sdks) {

      if (new IOSVersion(version).isGreaterThan(desiredSDKVersion)) {
        File f = new File(developer, "SDKs/iPhoneSimulator" + version + ".sdk");
        if (!f.exists()) {
          System.err.println("doesn't exist " + f);
        } else {
          File renamed = new File(developer, "/exiledSDKs/iPhoneSimulator" + version + ".sdk");
          boolean ok = f.renameTo(renamed);
          if (!ok) {
            throw new WebDriverException(
                "Starting the non default SDK requires some more setup.Failed to move " + f
                + " to " + renamed + getErrorMessageMoveSDK());
          }
        }
        f.mkdirs();
      }
    }
  }

  private String getErrorMessageMoveSDK() {
    File sdk = new File(developer, "SDKs");
    File exiled = new File(developer, "exiledSDKs");
    String msg = "Cannot move folders from " + sdk + " to " + exiled;
    msg += " Make sure the rights are correct (chmod -R +rw ... )";
    return msg;

  }

  public void restoreExiledSDKs() {
    File exiled = new File(developer, "exiledSDKs/");
    if (!exiled.exists()) {
      log.warning(exiled.getAbsolutePath() + " doesn't exist." + getErrorMessageMoveSDK());
      return;
    }
    for (String s : exiled.list()) {
      File sdk = new File(exiled, s);
      File original = new File(sdk.getParentFile().getParentFile() + "/SDKs/" + s);
      if (original.listFiles().length == 0) {
        original.delete();
      }
      boolean ok = sdk.renameTo(original);
      if (!ok) {
        throw new WebDriverException(
            "Error restoring the SDK to its original directory. The SDK will be missing in xcode.");
      }
    }

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
  public void cleanupDevice() {
    restoreExiledSDKs();
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
    //no-op instruments installs automatically for simualtor.
  }

  @Override
  public void setL10N(String locale, String language) {
    simulatorSettings.setL10N(locale, language);
  }

  @Override
  public void setVariation(DeviceType device, DeviceVariation variation) {
    simulatorSettings.setVariation(device, variation);
  }


  @Override
  public void resetContentAndSettings() {
    simulatorSettings.resetContentAndSettings();

  }

}
