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

package org.uiautomation.ios.server.services;

import org.libimobiledevice.ios.driver.binding.services.DeviceService;
import org.libimobiledevice.ios.driver.binding.services.IOSDevice;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.server.HostInfo;
import org.uiautomation.ios.server.InstrumentsBackedNativeIOSDriver;
import org.uiautomation.ios.server.RealDevice;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.application.IOSRunningApplication;
import org.uiautomation.ios.server.application.IPAApplication;
import org.uiautomation.ios.server.instruments.IOSDeviceManager;
import org.uiautomation.ios.server.instruments.NoInstrumentsImplementationAvailable;
import org.uiautomation.ios.server.simulator.IOSRealDeviceManager;
import org.uiautomation.ios.server.simulator.IOSSafariSimulatorManager;
import org.uiautomation.ios.server.simulator.IOSSimulatorManager;
import org.uiautomation.ios.server.simulator.InstrumentsFailedToStartException;
import org.uiautomation.ios.utils.IOSVersion;
import org.uiautomation.ios.wkrdp.RemoteIOSWebDriver;
import org.uiautomation.ios.wkrdp.ResponseFinder;
import org.uiautomation.ios.wkrdp.internal.AlertDetector;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class IOSDualDriver {

  private static final Logger log = Logger.getLogger(IOSDualDriver.class.getName());
  private final ServerSideSession session;
  private final Timer stopSessionTimer = new Timer(true);
  private final IOSCapabilities caps;
  private final HostInfo info;

  // Setup TearDown
  private IOSDeviceManager deviceManager;

  // Instruments
  private InstrumentsBackedNativeIOSDriver nativeDriver;

  // WKRDP
  private RemoteIOSWebDriver webDriver;


  private List<ResponseFinder> finders = new ArrayList<>();

  private WorkingMode mode = WorkingMode.Native;

  public IOSDualDriver(ServerSideSession session) {
    this.session = session;
    this.caps = session.getCapabilities();
    info = session.getIOSServerManager().getHostInfo();
    init();
  }

  private boolean isSimulator() {
    return session.getCapabilities().isSimulator();
  }

  private void init() {
    IOSCapabilities cap = session.getCapabilities();
    HostInfo info = session.getIOSServerManager().getHostInfo();

    // Create the device manager that does the setup and teardown
    if (isSimulator()) {
      if (session.getApplication().isSafari()) {
        deviceManager = new IOSSafariSimulatorManager(cap, info);
      } else {
        deviceManager = new IOSSimulatorManager(cap, info);
      }
    } else {
      IPAApplication ipa = (IPAApplication) session.getApplication().getUnderlyingApplication();
      RealDevice d = (RealDevice) session.getDevice();
      try {
        IOSDevice device = DeviceService.get(d.getUuid());
        deviceManager = new IOSRealDeviceManager(cap, device, ipa);
      } catch (Exception e) {
        throw new WebDriverException("Error getting device : " + e.getMessage());
      }
    }

    // Create the instruments based comm
    URL url = null;
    try {
      url =
          new URL("http://localhost:" + session.getIOSServerManager().getHostInfo().getPort()
                  + "/wd/hub");
    } catch (Exception e) {
      e.printStackTrace();
    }
    nativeDriver = new InstrumentsBackedNativeIOSDriver(url, session);

    if (!(getNativeDriver().getInstruments() instanceof NoInstrumentsImplementationAvailable)) {
      finders.add(new AlertDetector(getNativeDriver()));
    }

    // the WKRDP comm is created lazily.
  }

  public void stop() {
    deviceManager.cleanupDevice();
    nativeDriver.stop();

    if (webDriver != null) {
      webDriver.stop();
      webDriver = null;
    }

    stopSessionTimer.cancel();

  }

  private void forceStop() {
    try {
      deviceManager.cleanupDevice();
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      session.getIOSServerManager().stop(session.getSessionId());
    } catch (Exception e) {
      e.printStackTrace();
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
        nativeDriver.stop();
      } catch (Exception ignore) {
      }
    }
  }

  public void start(long timeOut) throws InstrumentsFailedToStartException {
    // force stop session if running for too long
    final int sessionTimeoutMillis = session.getOptions().getSessionTimeoutMillis();

    stopSessionTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        log.warning("forcing stop session that has been running for " + sessionTimeoutMillis / 1000
                    + " seconds");
        forceStop();
      }
    }, sessionTimeoutMillis);

    DeviceType deviceType = caps.getDevice();
    IOSRunningApplication application = session.getApplication();
    DeviceVariation variation = caps.getDeviceVariation();
    String locale = caps.getLocale();
    String language = caps.getLanguage();
    String instrumentsVersion = info.getInstrumentsVersion().getVersion();
    boolean instrumentsIs50OrHigher = new IOSVersion(instrumentsVersion).isGreaterOrEqualTo("5.0");
    boolean isSDK70OrHigher = new IOSVersion(caps.getSDKVersion()).isGreaterOrEqualTo("7.0");
    boolean putDefaultFirst = instrumentsIs50OrHigher;

    // the 5.0 instruments can't start MobileSafari
    // workaround is to remove the MobileSafari app from the install directory and put
    // it back after instruments starts it
    boolean
        tempRemoveMobileSafari =
        instrumentsIs50OrHigher && application.isSafari() && application.isSimulator();

    deviceManager.prepare();

    deviceManager.setVariation(deviceType, variation);
    deviceManager.setSimulatorScale(caps.getSimulatorScale());
    if (application.isSimulator()) {
      application.setDefaultDevice(deviceType, putDefaultFirst);
    }

    if (application.isSafari() && isSDK70OrHigher && application.isSimulator()) {
      application.setSafariBuiltinFavorites();
    }
    deviceManager.resetContentAndSettings();
    deviceManager.setL10N(locale, language);
    deviceManager.setKeyboardOptions();
    deviceManager.setLocationPreference(true);
    deviceManager.setMobileSafariOptions();

    deviceManager.installTrustStore(session.getOptions().getTrustStore());

    try {
      nativeDriver.start(timeOut);
    } catch (InstrumentsFailedToStartException e) {
      deviceManager.cleanupDevice();
    }

    if (session.getApplication().isSafari()) {
      setMode(WorkingMode.Web);
      getRemoteWebDriver().get("about:blank");

      String sdkVersion = session.getCapabilities().getSDKVersion();
      IOSVersion version = new IOSVersion(sdkVersion);
      if (sdkVersion != null && version.isGreaterOrEqualTo("7.0")) {
        if (!(getNativeDriver().getInstruments() instanceof NoInstrumentsImplementationAvailable)) {
          forceWebViewToReloadManually(3);
        }
      }
    }
  }


  /**
   * the webview doesn't refresh correctly if it hasn't been loaded at lease once.
   */
  private void forceWebViewToReloadManually(int retry) {

    boolean ok = false;
    setMode(WorkingMode.Native);
    for (int i = 0; i < retry; i++) {

      try {
        // to get Safari out of his home page and become responsive we need to click
        // on one of the home icons, click on the "about:blank" we added in prefs
        WebElement
            b =
            getNativeDriver().findElement(By.xpath("//UIAWindow/UIAScrollView/UIAButton"));
        b.click();

//        // click on the Go! button on the keyboard
//        log.fine("clicking on about:blank button: " + i);
//        Criteria c = new AndCriteria(new NameCriteria("Go"), new TypeCriteria(UIAButton.class));
//        UIAButton go = getNativeDriver().findElement(c);
//        go.click();
        ok = true;
      } catch (WebDriverException e) {
        sleep(2000); // allow some time to take effect
        // else keep trying as sometimes the click doesn't take effect on slow machines
        log.fine("about:blank button gone, proceeding");
        break;
      }
    }
    if (!ok) {
      throw new SessionNotCreatedException(
          "coudln't find the about:blank button after " + retry + " retries.");
    }
    setMode(WorkingMode.Web);
  }

  private void sleep(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public InstrumentsBackedNativeIOSDriver getNativeDriver() {
    return nativeDriver;
  }

  public synchronized RemoteIOSWebDriver getRemoteWebDriver() {
    if (webDriver == null) {
      String version = session.getCapabilities().getSDKVersion();
      if (new IOSVersion(version).isGreaterOrEqualTo("6.0")) {
        webDriver = new RemoteIOSWebDriver(session, finders);
        webDriver.start();

      } else {
        log.warning("Cannot create a driver. Version too old " + version);
      }
    }
    return webDriver;
  }

  public void setMode(WorkingMode mode) throws NoSuchWindowException {
    if (mode == WorkingMode.Web) {
      checkWebModeIsAvailable();
    }
    this.mode = mode;
  }

  private void checkWebModeIsAvailable() {
    if (session.getApplication().isSafari()) {
      return;
    }
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

  public WorkingMode getWorkingMode() {
    return mode;
  }

  public void restartWebkit() {
    int currentPageID = webDriver.getCurrentPageID();
    webDriver.stop();
    webDriver = new RemoteIOSWebDriver(session, finders);
//    webDriver.start();
    webDriver.switchTo(String.valueOf(currentPageID));
  }


}
