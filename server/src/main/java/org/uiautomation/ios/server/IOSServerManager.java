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

import org.libimobiledevice.binding.raw.IMobileDeviceFactory;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.server.application.APPIOSApplication;
import org.uiautomation.ios.server.application.AppleLanguage;
import org.uiautomation.ios.server.application.IOSRunningApplication;
import org.uiautomation.ios.server.application.ResourceCache;
import org.uiautomation.ios.server.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class IOSServerManager {

  private static final Logger log = Logger.getLogger(IOSServerManager.class.getName());
  public final ApplicationStore apps;
  private final List<ServerSideSession> sessions = new ArrayList<ServerSideSession>();
  private final HostInfo hostInfo;
  private final ResourceCache cache = new ResourceCache();
  private final IOSServerConfiguration options;
  private DeviceStore devices;

  // TODO freynaud cleanup
  public IOSServerManager(IOSServerConfiguration options) {
    this.options = options;
    try {
      LogManager.getLogManager()
          .readConfiguration(IOSServerManager.class.getResourceAsStream("/ios-logging.properties"));
    } catch (Exception e) {
      System.err.println("Cannot configure logger.");
    }
    this.hostInfo = new HostInfo(options);

    devices = new DeviceStore();

    if (Configuration.BETA_FEATURE) {
      IMobileDeviceFactory.INSTANCE.setDeviceDetectionCallback(devices);
      IMobileDeviceFactory.INSTANCE.startDetection();
    }

    if (Configuration.SIMULATORS_ENABLED) {
      devices.add(new SimulatorDevice());
    }

    apps = new ApplicationStore(options.getAppFolderToMonitor());

  }

  public static boolean matches(Map<String, Object> appCapabilities,
                                Map<String, Object> desiredCapabilities) {
    IOSCapabilities a = new IOSCapabilities(appCapabilities);
    IOSCapabilities d = new IOSCapabilities(desiredCapabilities);
    return matches(a, d);

  }

  private static boolean matches(IOSCapabilities applicationCapabilities,
                                 IOSCapabilities desiredCapabilities) {

    if (!APPIOSApplication.canRun(desiredCapabilities, applicationCapabilities)) {
      return false;
    }
    if (!Device.canRun(desiredCapabilities, applicationCapabilities)) {
      return false;
    }
    return true;
  }

  public void stop() {
    if (Configuration.BETA_FEATURE) {
      IMobileDeviceFactory.INSTANCE.stopDetection();
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
    List<IOSCapabilities> res = new ArrayList<IOSCapabilities>();
    for (Device d : getDeviceStore().getDevices()) {
      res.addAll(getApplicationStore().getCapabilities(d));
    }
    return res;
  }

  public IOSRunningApplication findAndCreateInstanceMatchingApplication(
      IOSCapabilities desiredCapabilities) {
    for (APPIOSApplication app : getApplicationStore().getApplications()) {
      IOSCapabilities appCapabilities = app.getCapabilities();
      if (APPIOSApplication.canRun(desiredCapabilities, appCapabilities)) {
        AppleLanguage lang = AppleLanguage.create(desiredCapabilities.getLanguage());
        return app.createInstance(lang);
      }
    }
    throw new SessionNotCreatedException(
        desiredCapabilities.getRawCapabilities() + " not found on server.");
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
        desiredCapabilities.getRawCapabilities() + "not available. Available are " + devices);
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
        if (session.hasCrashed()) {
          throw new WebDriverException(session.getCrashDetails().toString());
        }
        return session;
      }
    }
    throw new WebDriverException("Cannot find session " + opaqueKey + " on the server.");
  }

  public List<APPIOSApplication> getSupportedApplications() {
    return apps.getApplications();
  }
}
