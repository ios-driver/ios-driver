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

package org.uiautomation.ios.drivers;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.instruments.*;
import org.uiautomation.ios.setup.IOSDeviceManager;
import org.uiautomation.ios.setup.IOSDeviceManagerFactory;
import org.uiautomation.ios.setup.IOSSafariSimulatorManager;
import org.uiautomation.ios.utils.IOSVersion;
import org.uiautomation.ios.wkrdp.WebKitRemoteDebugProtocolFactory;
import org.uiautomation.ios.wkrdp.internal.WebKitRemoteDebugProtocol;
import org.uiautomation.ios.wkrdp.message.WebkitApplication;

import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IOSDualDriver {

  private static final Logger log = Logger.getLogger(IOSDualDriver.class.getName());
  private final ServerSideSession session;
  private final Timer stopSessionTimer = new Timer(true);

  // Setup TearDown
  private IOSDeviceManager deviceManager;
  // Instruments
  private RemoteIOSNativeDriver nativeDriver;
  // WKRDP
  private RemoteIOSWebDriver webDriver;


  private WorkingMode mode = WorkingMode.Native;

  public IOSDualDriver(ServerSideSession session) throws Exception {
    this.session = session;

    // Create the device manager that does the setup and teardown
    deviceManager = IOSDeviceManagerFactory.create(session);

    // Create the instruments based comm

    Instruments instruments = InstrumentsFactory.getInstruments(session);
    nativeDriver = new RemoteIOSNativeDriver(getURL(), session, instruments);

    // the WKRDP comm is created lazily.
    WebKitRemoteDebugProtocol p = WebKitRemoteDebugProtocolFactory.create(session, nativeDriver);
    webDriver = new RemoteIOSWebDriver(session, p);

  }

  private URL getURL() {
    URL url;
    try {
      url =
          new URL("http://localhost:" + session.getIOSServerManager().getHostInfo().getPort()
                  + "/wd/hub");
    } catch (Exception e) {
      throw new WebDriverException(e);
    }
    return url;
  }


  public void stop() {
    nativeDriver.stop();
    deviceManager.teardown();

    if (webDriver != null) {
      webDriver.stop();
      webDriver = null;
    }

    stopSessionTimer.cancel();
  }

  private void forceStop() {
    try {
      deviceManager.teardown();
    } catch (Exception e) {
      log.log(Level.WARNING,"device manager didn't stop properly",e);
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

  public void start(long timeOut) throws InstrumentsFailedToStartException, ApplicationCrashedOnStartException {
    deviceManager.setup();

    nativeDriver.start(timeOut);
    if (deviceManager instanceof IOSSafariSimulatorManager) {
      ((IOSSafariSimulatorManager) deviceManager).tmpFix();
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
      log.warning("Couldn't find button after "+retry+"retries.");
      throw new RecoverableCrashException(
          "coudln't find the about:blank button after " + retry + " retries.");
    }
    setMode(WorkingMode.Web);
  }

  private void sleep(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      //Thread.currentThread().interrupt();
    }
  }

  public RemoteIOSNativeDriver getNativeDriver() {
    return nativeDriver;
  }

  public synchronized RemoteIOSWebDriver getRemoteWebDriver() {
    if (!webDriver.isStarted()) {
      String version = session.getCapabilities().getSDKVersion();
      if (new IOSVersion(version).isGreaterOrEqualTo("6.0")) {
        webDriver.start();
      } else {
        log.warning("Cannot create a driver. Version too old " + version);
      }
    }
    return webDriver;
  }

  public void setMode(WorkingMode mode) throws NoSuchWindowException {
    if (mode == this.mode){
        return;
    }

    if (mode == WorkingMode.Web) {
      checkWebModeIsAvailable();
    }
    this.mode = mode;
  }

  private void checkWebModeIsAvailable() {
    if (session.getApplication().isSafari()) {
      return;
    }

    try {
        getNativeDriver().findElement(By.className("UIAWebView"));
    } catch (NoSuchElementException e) {
        throw new NoSuchWindowException("The app currently doesn't have a webview displayed.");
    }
  }

  public WorkingMode getWorkingMode() {
    return mode;
  }

  public void restartWebkit() {
    int currentPageID = webDriver.getCurrentPageID();
    webDriver.stop();
    WebKitRemoteDebugProtocol p = WebKitRemoteDebugProtocolFactory.create(session, nativeDriver);
    
    List<WebkitApplication> applications = webDriver.getApplications();
    webDriver = new RemoteIOSWebDriver(session, p);
    webDriver.setApplications(applications);
    webDriver.start();
    webDriver.switchTo(String.valueOf(currentPageID));
  }
}
