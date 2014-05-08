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

import org.libimobiledevice.ios.driver.binding.exceptions.LibImobileException;
import org.libimobiledevice.ios.driver.binding.services.DeviceService;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.server.application.APPIOSApplication;
import org.uiautomation.ios.server.application.AppleLanguage;
import org.uiautomation.ios.server.application.IOSRunningApplication;
import org.uiautomation.ios.server.application.ResourceCache;
import org.uiautomation.ios.server.configuration.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.LogManager;


public class IOSServerManager {

  public final ApplicationStore apps;
  private final List<ServerSideSession> sessions = new CopyOnWriteArrayList<>();
  private final HostInfo hostInfo;
  private final ResourceCache cache = new ResourceCache();
  private final IOSServerConfiguration options;
  private DeviceStore devices;

  // TODO freynaud cleanup
  public IOSServerManager(IOSServerConfiguration options) {
    this.options = options;

    // setup logging
    String loggingConfigFile = System.getProperty("java.util.logging.config.file");
    if (loggingConfigFile != null) {
      if (!new File(loggingConfigFile).exists()) {
        System.err
            .println("logging file not found: " + new File(loggingConfigFile).getAbsolutePath());
        loggingConfigFile = null; // to revert to builtin one
      }
    }
    if (loggingConfigFile == null) {
      // do not use builtin ios-logging.properties if -Djava.util.logging.config.file set
      try {
        LogManager.getLogManager()
            .readConfiguration(
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
      } catch (LibImobileException e) {
        e.printStackTrace();
      }
    }

    if (Configuration.SIMULATORS_ENABLED) {
      devices.add(new SimulatorDevice());
    }

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
    for (ServerSideSession session : sessions) {
      session.stop();
    }
    sessions.clear();

    if (Configuration.BETA_FEATURE) {
      try {
        DeviceService.INSTANCE.stopDetection();
      } catch (LibImobileException e) {
        e.printStackTrace();
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

  public ServerSideSession createSession(IOSCapabilities cap) {
    ServerSideSession session = new ServerSideSession(this, cap, options);
    sessions.add(session);
    return session;
  }

  public void stop(String opaqueKey) {
    ServerSideSession session = getSession(opaqueKey);

    session.stop();
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
            + desiredCapabilities.getRawCapabilities() + ".\n    Available apps: "
            + getSupportedApplications());
    }
    // if more than one matches it returns the last in the list (highest version for MobileSafari)
    APPIOSApplication app = matchingApps.get(matchingApps.size() - 1);
    AppleLanguage lang = AppleLanguage.create(desiredCapabilities.getLanguage());
    return app.createInstance(lang);
  }
  
  public List<APPIOSApplication> findAllMatchingApplications(
        IOSCapabilities desiredCapabilities) {
    List<APPIOSApplication> matchingApps = new ArrayList<>();
    for (APPIOSApplication app : getApplicationStore().getApplications()) {
      IOSCapabilities appCapabilities = app.getCapabilities();
      if (APPIOSApplication.canRun(desiredCapabilities, appCapabilities)) {
        matchingApps.add(app);
      }
    }
    return matchingApps;
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
        desiredCapabilities.getRawCapabilities() + " no devices available.\n    Known devices: " + devices);
  }

  public ApplicationStore getApplicationStore() {
    return apps;
  }

  public List<ServerSideSession> getSessions() {
    return sessions;
  }

  public ServerSideSession getSession(String opaqueKey) {
    for (ServerSideSession session : sessions) {
      if (session.getSessionId().equals(opaqueKey)) {
        return session;
      }
    }
    throw new WebDriverException("Cannot find session " + opaqueKey + " on the server.");
  }

  public List<APPIOSApplication> getSupportedApplications() {
    return apps.getApplications();
  }
}
