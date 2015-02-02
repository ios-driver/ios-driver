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
package org.uiautomation.ios;

import org.libimobiledevice.ios.driver.binding.exceptions.SDKException;
import org.libimobiledevice.ios.driver.binding.services.DeviceService;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.application.APPIOSApplication;
import org.uiautomation.ios.application.AppleLanguage;
import org.uiautomation.ios.application.IOSRunningApplication;
import org.uiautomation.ios.application.ResourceCache;
import org.uiautomation.ios.command.configuration.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class IOSServerManager {

  private static final Logger log = Logger.getLogger(IOSServerManager.class.getName());
  public final ApplicationStore apps;
  private final List<ServerSideSession> sessions = new CopyOnWriteArrayList<>();
  private final HostInfo hostInfo;
  private final ResourceCache cache = new ResourceCache();
  private final IOSServerConfiguration options;
  private final int TIME_OUT_FOR_SESSION;
  private DeviceStore devices;
  private final Object lock = new Object();
  private State state = State.stopped;
  private Map<String, ServerSideSession.StopCause> reasonByOpaqueKey = new ConcurrentHashMap<>();

  private static final String SAFARI_BUNDLE_NAME = "Safari";

  public void waitForState(State expected) {
    while (getState() != expected) {
      try {
        Thread.sleep(150);
      } catch (InterruptedException ignore) {
      }
    }
  }

  public IOSServerConfiguration getConfiguration() {
    return options;
  }

  public static enum State {
    starting, running, stopping, stopped;
  }

  // TODO freynaud cleanup
  public IOSServerManager(IOSServerConfiguration options) {
    // force stop session if running for too long

    setState(State.starting);
    this.options = options;
    TIME_OUT_FOR_SESSION = options.getSessionTimeoutMillis();

    //check if skipLoggingConfiguration is set to true
    boolean skipLogging = options.getSkipLoggerConfiguration();

    // setup logging
    String loggingConfigFile = System.getProperty("java.util.logging.config.file");
    if (loggingConfigFile != null) {
      if (!new File(loggingConfigFile).exists()) {
        System.err
            .println("logging file not found: " + new File(loggingConfigFile).getAbsolutePath());
        loggingConfigFile = null; // to revert to builtin one
      }
    }
    if (loggingConfigFile == null && !skipLogging) {
      // do not use builtin ios-logging.properties if -Djava.util.logging.config.file set
      // or if skipLoggingConfiguration is set to true
      try {
        LogManager.getLogManager().readConfiguration(
            IOSServerManager.class.getResourceAsStream("/ios-logging.properties"));
      } catch (Exception e) {
        System.err.println("Cannot configure logger.");
      }
    }

    this.hostInfo = new HostInfo(options);

    apps = new ApplicationStore(options.getAppFolderToMonitor());
    devices = new DeviceStore(apps, options.getUuidWhitelist());

    if (Configuration.BETA_FEATURE) {
      try {

        DeviceService.INSTANCE.startDetection(devices);
      } catch (SDKException e) {
        log.log(Level.SEVERE, "LIMD_SDK", e);
      }
    }

    if (Configuration.SIMULATORS_ENABLED) {
      devices.add(new SimulatorDevice(getHostInfo()));
    }
    setState(State.running);
  }


  public static boolean matches(Map<String, Object> appCapabilities,
                                Map<String, Object> desiredCapabilities) {
    IOSCapabilities a = new IOSCapabilities(appCapabilities);
    IOSCapabilities d = new IOSCapabilities(desiredCapabilities);
    return matches(a, d);
  }

  private static boolean matches(IOSCapabilities applicationCapabilities,
                                 IOSCapabilities desiredCapabilities) {
    return APPIOSApplication.canRun(desiredCapabilities, applicationCapabilities) &&
           Device.canRun(desiredCapabilities, applicationCapabilities);
  }

  public void stop() {
    for (java.util.logging.Handler h : log.getHandlers()) {
      if (h instanceof FileHandler) {
        ((FileHandler) h).close();
      }
    }
    for (ServerSideSession session : sessions) {
      session.stop();
    }
    sessions.clear();

    if (Configuration.BETA_FEATURE) {
      try {
        DeviceService.INSTANCE.stopDetection();
      } catch (SDKException e) {
        log.log(Level.SEVERE, "LIMD_SDK", e);
      }
    }
  }

  public DeviceStore getDeviceStore() {
    return devices;
  }

  public void addSupportedApplication(APPIOSApplication application) {
    apps.add(application);
    cache.cacheResource(application);
  }

  public HostInfo getHostInfo() {
    return hostInfo;
  }

  public ResourceCache getCache() {
    return cache;
  }

  public int getPort() {
    return hostInfo.getPort();
  }

  public ServerSideSession createSession(IOSCapabilities cap)
      throws SessionNotInitializedException {

    if (getState() != State.running) {
      return null;
    }
    ServerSideSession session = new ServerSideSession(this, cap, options);
    sessions.add(session);
    return session;
  }

  public void registerSessionHasStop(Session session) {
    registerSessionHasStop(session, ServerSideSession.StopCause.normal);
  }


  public void registerSessionHasStop(Session session, ServerSideSession.StopCause cause) {
    reasonByOpaqueKey.put(session.getSessionId(), cause);
    sessions.remove(session);
  }

  public List<IOSCapabilities> getAllCapabilities() {
    List<IOSCapabilities> res = new ArrayList<>();
    for (Device d : getDeviceStore().getDevices()) {
      res.addAll(getApplicationStore().getCapabilities(d));
    }
    return res;
  }

  public IOSRunningApplication findAndCreateInstanceMatchingApplication(
      IOSCapabilities desiredCapabilities) {
    List<APPIOSApplication> matchingApps = findAllMatchingApplications(desiredCapabilities);
    if (matchingApps.size() == 0) {
      throw new SessionNotCreatedException("desired app not found on server: "
                                           + desiredCapabilities.getRawCapabilities()
                                           + ".\n    Available apps: "
                                           + getSupportedApplications());
    }

    // If more than one matches it returns the last in the list (highest version for MobileSafari
    // if not requested with a particular version of SDK)
    APPIOSApplication app = matchingApps.get(matchingApps.size() - 1);
    AppleLanguage lang = AppleLanguage.create(desiredCapabilities.getLanguage());
    return app.createInstance(lang);
  }

  public List<APPIOSApplication> findAllMatchingApplications(IOSCapabilities desiredCapabilities) {
    List<APPIOSApplication> matchingApps = new ArrayList<>();
    if (isSafariRequestedWithSDKVersion(desiredCapabilities)) {
      if (log.isLoggable(Level.INFO)) {
        log.log(Level.INFO, "Safari application requested for SDK version: " + desiredCapabilities.getSDKVersion());
      }
      matchingApps.add(getSafariForSdkVersion(desiredCapabilities.getSDKVersion()));
      return matchingApps;
    }
    for (APPIOSApplication app : getApplicationStore().getApplications()) {
      IOSCapabilities appCapabilities = app.getCapabilities();
      if (APPIOSApplication.canRun(desiredCapabilities, appCapabilities)) {
        matchingApps.add(app);
      }
    }
    return matchingApps;
  }

  private boolean isSafariRequestedWithSDKVersion(IOSCapabilities desiredCapabilities) {
    return desiredCapabilities.getSDKVersion() != null && !desiredCapabilities.getSDKVersion().isEmpty()
        && SAFARI_BUNDLE_NAME.equalsIgnoreCase(desiredCapabilities.getBundleName());
  }

  private APPIOSApplication getSafariForSdkVersion(String sdkVersion) {
    for (APPIOSApplication application : getApplicationStore().getApplications()) {
      if (application.isSafari() && sdkVersion.equalsIgnoreCase(application.getSafariSDKVersion())) {
        return application;
      }
    }
    throw new WebDriverException("Cannot find Safari for SDK version: " + sdkVersion);
  }
  
  public Device findAndReserveMatchingDevice(IOSCapabilities desiredCapabilities) {
    List<Device> devices = getDeviceStore().getDevices();
    for (Device device : devices) {
      IOSCapabilities deviceCapabilities = device.getCapability();
      if (Device.canRun(desiredCapabilities, deviceCapabilities)) {
        Device d = device.reserve();
        if (d != null) {
          return d;
        }
      }
    }
    throw new SessionNotCreatedException(
        desiredCapabilities.getRawCapabilities() + " no devices available.\n    Known devices: "
        + devices);
  }

  public ApplicationStore getApplicationStore() {
    return apps;
  }

  public List<ServerSideSession> getSessions() {
    return sessions;
  }

  public ServerSideSession getSession(String opaqueKey) throws SessionTimedOutException {
    // first, check if the session stopped already

    // check if the session is in the process of stopping
    for (ServerSideSession session : sessions) {
      if (session.getSessionId().equals(opaqueKey)) {
        if (session.getSessionState() == ServerSideSession.SessionState.stopped) {
          throw new WebDriverException(session.getStopCause().name());
        } else {
          return session;
        }
      }
    }

    // if the session isn't there anymore, try to give a helpful message on why it stopped
    ServerSideSession.StopCause cause = reasonByOpaqueKey.get(opaqueKey);
    if (cause != null) {
      throw new WebDriverException(cause.name());
    }
    throw new WebDriverException("Cannot find session " + opaqueKey + " on the server.");

  }

  public static class SessionTimedOutException extends WebDriverException {

    public SessionTimedOutException(String message) {
      super(message);
    }
  }

  public List<APPIOSApplication> getSupportedApplications() {
    return apps.getApplications();
  }

  public IOSServerConfiguration getIOSServerConfiguration() {
    return options;
  }

  public void stopGracefully() throws InterruptedException {
    // refuse further requests
    setState(State.stopping);
    // wait for requests to be processed
    while (getSessions().size() != 0) {
      Thread.sleep(250);
    }
    // stops
    stop();
    setState(State.stopped);
  }

  public boolean isRunning() {
    return getState() == State.running;
  }

  private void setState(State state) {
    synchronized (lock) {
      this.state = state;
    }
  }

  private State getState() {
    synchronized (lock) {
      return this.state;
    }
  }
}
