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

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.remote.SessionId;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.client.uiamodels.impl.ServerSideNativeDriver;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.instruments.communication.CommunicationChannel;
import org.uiautomation.ios.server.simulator.InstrumentsFailedToStartException;
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
  private RemoteIOSDriver nativeDriver;
  private RemoteIOSWebDriver webDriver;
  private WorkingMode mode = WorkingMode.Native;
  private Timer stopSessionTimer = new Timer(true);
  private final Instruments instruments;

  private Thread shutdownHook = new Thread() {
    @Override
    public void run() {
      instruments.stop();
    }
  };
  public IOSDualDriver(ServerSideSession session) {
    this.session = session;
    instruments = InstrumentsFactory.getInstruments(session);

    Runtime.getRuntime().addShutdownHook(shutdownHook);
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

  private void forceStop() {
    try {
      instruments.stop();
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

  public void start() {
    try {
      instruments.start();
    } catch (InstrumentsFailedToStartException e) {
      forceStop();
    }

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
    nativeDriver = new ServerSideNativeDriver(url, new SessionId(session.getSessionId()));

    if ("Safari".equals(session.getCapabilities().getBundleName())) {
      setMode(WorkingMode.Web);
      getRemoteWebDriver().get("about:blank");
    }
  }

  public RemoteIOSDriver getNativeDriver() {
    return nativeDriver;
  }

  public Instruments getInstruments(){
    return instruments;
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

  public CommunicationChannel communication(){
    return instruments.getChannel();
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

  public TakeScreenshotService getScreenshotService() {
    return instruments.getScreenshotService();
  }
}
