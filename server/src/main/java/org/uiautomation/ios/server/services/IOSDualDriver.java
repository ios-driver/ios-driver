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

import org.openqa.selenium.*;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.server.InstrumentsBackedNativeIOSDriver;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.utils.IOSVersion;
import org.uiautomation.ios.wkrdp.RemoteIOSWebDriver;
import org.uiautomation.ios.wkrdp.internal.AlertDetector;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class IOSDualDriver {

  private static final Logger log = Logger.getLogger(IOSDualDriver.class.getName());
  private final ServerSideSession session;
  private final Timer stopSessionTimer = new Timer(true);
  private InstrumentsBackedNativeIOSDriver nativeDriver;
  private RemoteIOSWebDriver webDriver;
  private WorkingMode mode = WorkingMode.Native;

  public IOSDualDriver(ServerSideSession session) {
    this.session = session;
  }

  public void stop() {
    nativeDriver.stop();

    if (webDriver != null) {
      webDriver.stop();
      webDriver = null;
    }
    stopSessionTimer.cancel();

  }

  private void forceStop() {
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

  public void start() {
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

    URL url = null;
    try {
      url =
          new URL("http://localhost:" + session.getIOSServerManager().getHostInfo().getPort()
                  + "/wd/hub");
    } catch (Exception e) {
      e.printStackTrace();
    }
    nativeDriver = new InstrumentsBackedNativeIOSDriver(url, session);
    nativeDriver.start();

    if (session.getApplication().isSafari()) {
      try {
        // TODO: check if running 7.0?
        // click on "Apple" button to get the simulator out of initial state where webview is not updated
        WebElement appleButton = nativeDriver.findElement(By.xpath("//UIAWindow/UIAScrollView/UIAButton"));
        if (appleButton.getAttribute("name") == null)
          appleButton.click();
      } catch (WebDriverException ignore) {
          // button is not there
      }
      setMode(WorkingMode.Web);
      getRemoteWebDriver().get("about:blank");
    }
  }

  public InstrumentsBackedNativeIOSDriver getNativeDriver() {
    return nativeDriver;
  }

  public synchronized RemoteIOSWebDriver getRemoteWebDriver() {
    if (webDriver == null) {
      String version = session.getCapabilities().getSDKVersion();
      if (new IOSVersion(version).isGreaterOrEqualTo("6.0")) {
        webDriver = new RemoteIOSWebDriver(session, new AlertDetector(nativeDriver));
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
    webDriver = new RemoteIOSWebDriver(session, new AlertDetector(nativeDriver));
    webDriver.switchTo(String.valueOf(currentPageID));
  }


}
