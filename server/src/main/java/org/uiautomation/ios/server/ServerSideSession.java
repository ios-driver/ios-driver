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
import org.openqa.selenium.remote.SessionId;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.UIAModels.configuration.CommandConfiguration;
import org.uiautomation.ios.UIAModels.configuration.DriverConfiguration;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.client.uiamodels.impl.ServerSideNativeDriver;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.device.Device;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.configuration.DriverConfigurationStore;
import org.uiautomation.ios.server.instruments.CommunicationChannel;
import org.uiautomation.ios.server.instruments.InstrumentsManager;
import org.uiautomation.ios.utils.ClassicCommands;
import org.uiautomation.ios.wkrdp.RemoteIOSWebDriver;
import org.uiautomation.ios.wkrdp.internal.AlertDetector;

import java.io.File;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Logger;

public class ServerSideSession extends Session {

  private static final Logger log = Logger.getLogger(ServerSideSession.class.getName());
  private final IOSApplication application;
  private final IOSCapabilities capabilities;
  private final InstrumentsManager instruments;
  public final IOSServerManager driver;

  //private WebInspector inspector;

  private RemoteIOSDriver nativeDriver;
  private RemoteIOSWebDriver webDriver;

  private WorkingMode mode = WorkingMode.Native;

  private final DriverConfiguration configuration;


  public IOSCapabilities getCapabilities() {
    return capabilities;
  }

  ServerSideSession(IOSServerManager driver, IOSCapabilities capabilities) {
    super(UUID.randomUUID().toString());

    this.driver = driver;
    this.capabilities = capabilities;

    application = driver.findMatchingApplication(capabilities);
    application.setLanguage(capabilities.getLanguage());
    if (capabilities.getSDKVersion() == null) {
      capabilities.setSDKVersion(ClassicCommands.getDefaultSDK());
    } else {
      String version = capabilities.getSDKVersion();
      Float v = Float.parseFloat(version);
      if (v < 5) {
        throw new SessionNotCreatedException(v + " is too old. Only support SDK 5.0 and above.");
      }

      if (!driver.getHostInfo().getInstalledSDKs().contains(version)) {
        throw new SessionNotCreatedException("Cannot start on version " + version + ".Installed : "
                                             + driver.getHostInfo().getInstalledSDKs());
      }
    }
    instruments = new InstrumentsManager(driver.getPort());

    configuration = new DriverConfigurationStore();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        forceStop();
      }
    });
  }

  public Device getDevice() {
    return capabilities.getDevice();
  }

  public CommandConfiguration configure(WebDriverLikeCommand command) {
    return configuration.configure(command);
  }

  public RemoteIOSDriver getNativeDriver() {
    return nativeDriver;
  }

  public IOSApplication getApplication() {
    return application;
  }

  public CommunicationChannel communication() {
    return instruments.communicate();
  }

  public void stop() {
    // nativDriver should have instruments within it.
    instruments.stop();
    if (webDriver != null) {
      webDriver.stop();
    }

  }

  public void forceStop() {
    instruments.forceStop();
  }

  public File getOutputFolder() {
    return instruments.getOutput();
  }

  public InstrumentsManager getInstruments() {
    return instruments;

  }

  public void start() {
    String
        appleLanguage =
        application.getAppleLocaleFromLanguageCode(capabilities.getLanguage())
            .getAppleLanguagesForPreferencePlist();
    instruments.startSession(capabilities.getDevice(), capabilities.getDeviceVariation(),
                             capabilities.getSDKVersion(), capabilities.getLocale(),
                             appleLanguage, application, getSessionId(), capabilities.isTimeHack(),
                             capabilities.getExtraSwitches());

    URL url = null;
    try {
      url = new URL("http://localhost:" + driver.getHostInfo().getPort() + "/wd/hub");
    } catch (Exception e) {
      e.printStackTrace();
    }
    nativeDriver = new ServerSideNativeDriver(url, new SessionId(instruments.getSessionId()));

    if ("Safari".equals(capabilities.getBundleName())) {
      setMode(WorkingMode.Web);
    }

  }


  public synchronized RemoteIOSWebDriver getRemoteWebDriver() {
    if (webDriver == null) {
      String version = capabilities.getSDKVersion();
      Float v = Float.parseFloat(version);
      if (v >= 6) {
        webDriver = new RemoteIOSWebDriver(this, new AlertDetector(nativeDriver));
      } else {
        log.warning("Cannot create a driver. Version too old " + v);
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
    webDriver.stop();
    webDriver = null;
    webDriver = new RemoteIOSWebDriver(this, new AlertDetector(nativeDriver));
    webDriver.switchTo(webDriver.getPages().get(0));
  }
}
