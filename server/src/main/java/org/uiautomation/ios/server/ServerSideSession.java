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
package org.uiautomation.ios.server;

import org.openqa.selenium.SessionNotCreatedException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.UIAModels.configuration.CommandConfiguration;
import org.uiautomation.ios.UIAModels.configuration.DriverConfiguration;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.server.application.APPIOSApplication;
import org.uiautomation.ios.server.application.AppleLanguage;
import org.uiautomation.ios.server.application.IOSRunningApplication;
import org.uiautomation.ios.server.configuration.Configuration;
import org.uiautomation.ios.server.configuration.DriverConfigurationStore;
import org.uiautomation.ios.server.refactor.IOSDualDriver;
import org.uiautomation.ios.server.utils.IOSVersion;
import org.uiautomation.ios.server.utils.ZipUtils;
import org.uiautomation.ios.utils.ApplicationCrashDetails;
import org.uiautomation.ios.utils.ClassicCommands;

import java.io.File;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Logger;

public class ServerSideSession extends Session {

  private static final Logger log = Logger.getLogger(ServerSideSession.class.getName());
  public final IOSServerManager server;
  private final IOSCapabilities capabilities;
  private final IOSDualDriver driver;
  private final IOSServerConfiguration options;
  private final DriverConfiguration configuration;
  private IOSRunningApplication application;
  private Device device;
  private boolean sessionCrashed;
  private ApplicationCrashDetails applicationCrashDetails;
  private String outputFolder;

  ServerSideSession(IOSServerManager server, IOSCapabilities desiredCapabilities,
                    IOSServerConfiguration options) {
    super(UUID.randomUUID().toString());
    this.server = server;
    this.capabilities = desiredCapabilities;
    this.options = options;

    this.sessionCrashed = false;
    this.applicationCrashDetails = null;

    // extract application from capabilities if necessary
    URL url = desiredCapabilities.getAppURL();
    if (url != null) {
      application = extractFromCapabilities();
    } else {
      application = server.findAndCreateInstanceMatchingApplication(desiredCapabilities);
    }

    try {
      device = server.findAndReserveMatchingDevice(desiredCapabilities);

      updateCapabilitiesWithSensibleDefaults();

      driver = new IOSDualDriver(this);
      configuration = new DriverConfigurationStore();


    } catch (Exception e) {
      if (device != null) {
        device.release();
      }
      throw e;
    }
  }

  private void updateCapabilitiesWithSensibleDefaults() {
    // update capabilities and put default values in the missing fields.
    if (capabilities.getDeviceVariation() == null) {
      capabilities.setDeviceVariation(DeviceVariation.Regular);
    }
    capabilities.setBundleId(application.getBundleId());
    // TODO device.getSDK()
    if (capabilities.getSDKVersion() == null) {
      capabilities.setSDKVersion(ClassicCommands.getDefaultSDK());
    } else {
      String version = capabilities.getSDKVersion();

      if (!new IOSVersion(version).isGreaterOrEqualTo("5.0")) {
        throw new SessionNotCreatedException(
            version + " is too old. Only support SDK 5.0 and above.");
      }
      if (!server.getHostInfo().getInstalledSDKs().contains(version)) {
        throw new SessionNotCreatedException(
            "SDK " + version + " not installed on this machine.");
      }

      if (!server.getHostInfo().getInstalledSDKs().contains(version)) {
        throw new SessionNotCreatedException(
            "Cannot start on version " + version + ".Installed : "
            + server.getHostInfo().getInstalledSDKs());
      }
    }
  }

  private IOSRunningApplication extractFromCapabilities() {
    URL appURL = capabilities.getAppURL();
    if (!Configuration.BETA_FEATURE) {
      Configuration.off();
    }

    try {
      File appFile = ZipUtils.extractAppFromURL(appURL);
      if (appFile.getName().endsWith(".app")) {
        capabilities.setCapability(IOSCapabilities.SIMULATOR, true);
      }
      APPIOSApplication app = APPIOSApplication.createFrom(appFile);
      AppleLanguage lang = AppleLanguage.valueOf(capabilities.getLanguage());
      return app.createInstance(lang);
    } catch (Exception ex) {
      throw new SessionNotCreatedException("cannot create app from " + appURL + ": " + ex);
    }

  }

  public IOSCapabilities getCapabilities() {
    return capabilities;
  }

  public Device getDevice() {
    return device;
  }

  public CommandConfiguration configure(WebDriverLikeCommand command) {
    return configuration.configure(command);
  }

  public IOSRunningApplication getApplication() {
    return application;
  }

  public boolean hasCrashed() {
    return sessionCrashed;
  }

  public void sessionHasCrashed(String log) {
    sessionCrashed = true;
    applicationCrashDetails = new ApplicationCrashDetails(log);
  }

  public ApplicationCrashDetails getCrashDetails() {
    return applicationCrashDetails;
  }

  public IOSServerManager getIOSServerManager() {
    return server;
  }

  public IOSServerConfiguration getOptions() {
    return options;
  }

  public IOSDualDriver getDualDriver() {
    return driver;
  }

  public void start() {
    driver.start();
  }

  public void stop() {
    if (driver != null) {
      driver.stop();
    }
  }
}
