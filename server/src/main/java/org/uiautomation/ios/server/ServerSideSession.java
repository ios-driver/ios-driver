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
package org.uiautomation.ios.server;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.SessionId;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.UIAModels.configuration.CommandConfiguration;
import org.uiautomation.ios.UIAModels.configuration.DriverConfiguration;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.client.uiamodels.impl.ServerSideNativeDriver;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.server.application.APPIOSApplication;
import org.uiautomation.ios.server.application.IOSRunningApplication;
import org.uiautomation.ios.server.configuration.Configuration;
import org.uiautomation.ios.server.configuration.DriverConfigurationStore;
import org.uiautomation.ios.server.instruments.CommunicationChannel;
import org.uiautomation.ios.server.instruments.InstrumentsManager;
import org.uiautomation.ios.server.utils.IOSVersion;
import org.uiautomation.ios.server.utils.ZipUtils;
import org.uiautomation.ios.utils.ClassicCommands;
import org.uiautomation.ios.wkrdp.RemoteIOSWebDriver;
import org.uiautomation.ios.wkrdp.internal.AlertDetector;

import java.io.File;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Logger;

public class ServerSideSession extends Session {

  private static final Logger log = Logger.getLogger(ServerSideSession.class.getName());
  private IOSRunningApplication application;
  private Device device;
  private final IOSCapabilities capabilities;
  private final InstrumentsManager instruments;
  private final IOSServerConfiguration options;
  public final IOSServerManager driver;

  //private WebInspector inspector;

  private RemoteIOSDriver nativeDriver;
  private RemoteIOSWebDriver webDriver;

  private WorkingMode mode = WorkingMode.Native;

  private final DriverConfiguration configuration;

  private Timer stopSessionTimer = new Timer(true);

  private Thread shutdownHook = new Thread() {
    @Override
    public void run() {
      forceStop();
    }
  };


  public IOSCapabilities getCapabilities() {
    return capabilities;
  }

  ServerSideSession(IOSServerManager driver, IOSCapabilities desiredCapabilities,
                    IOSServerConfiguration options) {
    super(UUID.randomUUID().toString());

    this.driver = driver;
    this.capabilities = desiredCapabilities;
    this.options = options;

    String appCapability = (String) desiredCapabilities.getCapability("app");
    if (appCapability != null) {
      if (!Configuration.BETA_FEATURE)
        Configuration.off();
      try {
        File appFile = ZipUtils.extractAppFromURL(appCapability);
        if (appFile.getName().endsWith(".app"))
          desiredCapabilities.setCapability(IOSCapabilities.SIMULATOR, true);
        APPIOSApplication app = APPIOSApplication.createFrom(appFile);
        application = app.createInstance(desiredCapabilities.getLanguage());
      } catch (Exception ex) {
        throw new SessionNotCreatedException("cannot create app from " + appCapability + ": " + ex);
      }
    } else {
      application = driver.findAndCreateInstanceMatchingApplication(desiredCapabilities);
    }

    try {
      device = driver.findAndReserveMatchingDevice(desiredCapabilities);

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
          throw new SessionNotCreatedException(version + " is too old. Only support SDK 5.0 and above.");
        }
        if (!driver.getHostInfo().getInstalledSDKs().contains(version)) {
          throw new SessionNotCreatedException(
              "SDK " + version + " not installed on this machine.");
        }

        if (!driver.getHostInfo().getInstalledSDKs().contains(version)) {
          throw new SessionNotCreatedException(
              "Cannot start on version " + version + ".Installed : "
              + driver.getHostInfo().getInstalledSDKs());
        }
      }

      instruments = new InstrumentsManager(driver.getPort());
      configuration = new DriverConfigurationStore();

      Runtime.getRuntime().addShutdownHook(shutdownHook);
    } catch (WebDriverException e) {
      if (device != null) {
        device.release();
      }
      throw e;
    }
  }

  public Device getDevice() {
    return device;
  }

  public CommandConfiguration configure(WebDriverLikeCommand command) {
    return configuration.configure(command);
  }

  public RemoteIOSDriver getNativeDriver() {
    return nativeDriver;
  }

  public IOSRunningApplication getApplication() {
    return application;
  }

  public CommunicationChannel communication() {
    return instruments.communicate();
  }

  public void stop() {
    // nativeDriver should have instruments within it.
    instruments.stop();
    if (webDriver != null) {
      webDriver.stop();
      webDriver = null;
    }

    stopSessionTimer.cancel();
    stopSessionTimer = null;
    Runtime.getRuntime().removeShutdownHook(shutdownHook);
    shutdownHook = null;
  }

  public void forceStop() {
    instruments.forceStop();
  }

  private void hardForceStop() {
    try {
      instruments.forceStop();
    } catch (Exception ignore) {
    }
    if (webDriver != null) {
      try {
        webDriver.stop();
      } catch (Exception ignore) {
      }
      webDriver = null;
    }
    if (nativeDriver != null) {
      try {
        nativeDriver.quit();
      } catch (Exception ignore) {
      }
      nativeDriver = null;
    }
  }

  public File getOutputFolder() {
    return instruments.getOutput();
  }

  public InstrumentsManager getInstruments() {
    return instruments;

  }

  public void start() {
    instruments.startSession(getSessionId(), application, device, capabilities);

    // force stop session if running for too long
    final int sessionTimeoutMillis = options.getSessionTimeoutMillis();
    stopSessionTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        log.warning("forcing stop session that has been running for " + sessionTimeoutMillis / 1000
                    + " seconds");
        hardForceStop();
      }
    }, sessionTimeoutMillis);

    URL url = null;
    try {
      url = new URL("http://localhost:" + driver.getHostInfo().getPort() + "/wd/hub");
    } catch (Exception e) {
      e.printStackTrace();
    }
    nativeDriver = new ServerSideNativeDriver(url, new SessionId(instruments.getSessionId()));

    if ("Safari".equals(capabilities.getBundleName())) {
      setMode(WorkingMode.Web);
      getRemoteWebDriver().get("about:blank");
    }

  }


  public synchronized RemoteIOSWebDriver getRemoteWebDriver() {
    if (webDriver == null) {
      String version = capabilities.getSDKVersion();
      if (new IOSVersion(version).isGreaterOrEqualTo("6.0")) {
        webDriver = new RemoteIOSWebDriver(this, new AlertDetector(nativeDriver));
      } else {
        log.warning("Cannot create a driver. Version too old " + version);
      }
    }
    return webDriver;
  }

  public void setMode(WorkingMode mode) throws NoSuchWindowException {
    if (mode == WorkingMode.Web) {
      checkWebModeIsAvailable();
      /*try {
        if (!getRemoteWebDriver().isConnected()) {
          //String bundleId = application.getMetadata("CFBundleIdentifier");
          //webDriver.connect(bundleId);
          getRemoteWebDriver().switchTo(webDriver.getPages().get(0));
        }
      } catch (Exception e) {
        e.printStackTrace();
        throw new NoSuchWindowException("Cannot switch to window " + mode, e);
      }   */
    }
    this.mode = mode;
  }

  public WorkingMode getWorkingMode() {
    return this.mode;
  }

  private void checkWebModeIsAvailable() {
    if (webDriver != null) {
      return;
    } else {
      try {
        getNativeDriver().findElement(By.className("UIAWebView"));
      } catch (NoSuchElementException e) {
        throw new NoSuchWindowException("The app currently doesn't have a webview displayed.");
      }
    }

  }


  public void restartWebkit() {
    int currentPageID = webDriver.getCurrentPageID();
    webDriver.stop();
    webDriver = new RemoteIOSWebDriver(this, new AlertDetector(nativeDriver));
    webDriver.switchTo(String.valueOf(currentPageID));
  }
}
