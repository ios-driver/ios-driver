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

import static org.uiautomation.ios.IOSCapabilities.MAGIC_PREFIX;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.LogManager;

import org.json.JSONException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.application.AppleLocale;
import org.uiautomation.ios.server.application.ResourceCache;
import org.uiautomation.ios.server.utils.BuildInfo;
import org.uiautomation.ios.server.utils.ClassicCommands;

public class IOSDriver {

  private final List<ServerSideSession> sessions = new ArrayList<ServerSideSession>();
  private final Set<IOSApplication> supportedApplications = new HashSet<IOSApplication>();
  private final HostInfo hostInfo;
  private final ResourceCache cache = new ResourceCache();

  public IOSDriver(int port) {
    try {
      LogManager.getLogManager()
          .readConfiguration(IOSDriver.class.getResourceAsStream("/ios-logging.properties"));
    } catch (Exception e) {
      System.err.println("Cannot configure logger.");
    }
    this.hostInfo = new HostInfo(port);
  }

  public void addSupportedApplication(IOSApplication application) {
    boolean added = supportedApplications.add(application);
    if (!added) {
      throw new WebDriverException("app already present :" + application.getApplicationPath());
    }
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
    ServerSideSession session = new ServerSideSession(this, cap);
    sessions.add(session);
    return session;
  }

  public void stop(String opaqueKey) {
    ServerSideSession session = getSession(opaqueKey);
    session.stop();
    sessions.remove(session);
  }

  public IOSCapabilities getCapabilities(IOSApplication application) {
    // some cap are from the host. SDK version is not defined by the app itself.
    IOSCapabilities cap = new IOSCapabilities();
    cap.setSDKVersion(hostInfo.getSDK());
    cap.setSupportedLanguages(application.getSupportedLanguagesCodes());
    cap.setCapability("applicationPath", application.getApplicationPath().getAbsoluteFile());

    for (Iterator iterator = application.getMetadata().keys(); iterator.hasNext(); ) {
      String key = (String) iterator.next();

      try {
        Object value = application.getMetadata().get(key);
        cap.setCapability(key, value);
      } catch (JSONException e) {
        throw new WebDriverException("cannot get metadata", e);
      }
    }

    cap.setSupportedDevices(cap.getSupportedDevicesFromDeviceFamily());
    return cap;
  }

  public IOSApplication findMatchingApplication(IOSCapabilities desiredCapabilities) {
    for (IOSApplication app : supportedApplications) {
      IOSCapabilities appCapabilities = getCapabilities(app);
      if (IOSDriver.matches(appCapabilities, desiredCapabilities)) {
        return app;
      }
    }
    throw new SessionNotCreatedException(
        desiredCapabilities.getRawCapabilities() + "not found on server.");
  }

  public static boolean matches(Map<String, Object> appCapabilities,
                                Map<String, Object> desiredCapabilities) {
    IOSCapabilities a = new IOSCapabilities(appCapabilities);
    IOSCapabilities d = new IOSCapabilities(desiredCapabilities);
    return matches(a, d);

  }

  private static boolean matches(IOSCapabilities applicationCapabilities,
                                 IOSCapabilities desiredCapabilities) {

    if (desiredCapabilities.getBundleName() == null) {
      throw new WebDriverException("you need to specify the bundle to test.");
    }
    String desired = desiredCapabilities.getBundleName();
    String
        appName =
        (String) (applicationCapabilities.getBundleName() != null ? applicationCapabilities
            .getBundleName() : applicationCapabilities.getCapability("CFBundleDisplayName"));

    if (!desired.equals(appName)) {
      return false;
    }
    if (desiredCapabilities.getBundleVersion() != null
        && !desiredCapabilities.getBundleVersion()
        .equals(applicationCapabilities.getBundleVersion())) {
      return false;
    }
    if (desiredCapabilities.getDevice() == null) {
      throw new WebDriverException("you need to specify the device.");
    }
    if (!(applicationCapabilities.getSupportedDevices()
              .contains(desiredCapabilities.getDevice()))) {
      return false;
    }
    // check any extra capability starting with plist_
    for (String key : desiredCapabilities.getRawCapabilities().keySet()) {
      if (key.startsWith(IOSCapabilities.MAGIC_PREFIX)) {
        String realKey = key.replace(MAGIC_PREFIX, "");
        if (!desiredCapabilities.getRawCapabilities().get(key)
            .equals(applicationCapabilities.getRawCapabilities().get(realKey))) {
          return false;
        }
      }
    }
    String l = desiredCapabilities.getLanguage();
    if (l != null && !applicationCapabilities.getSupportedLanguages().contains(l)) {
      throw new SessionNotCreatedException(
          "Language requested, " + l + " ,isn't supported.Supported are : "
          + applicationCapabilities.getSupportedLanguages());
    }

    String sdk = desiredCapabilities.getSDKVersion();
    // TODO freynaud validate for multi SDK
    /*
     * if (sdk != null && !sdk.equals(applicationCapabilities.getSDKVersion()))
     * { throw new IOSAutomationException("Cannot start sdk " + sdk +
     * ". Run on " + applicationCapabilities.getSDKVersion()); }
     */

    return true;
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
    throw new WebDriverException("Cannot find session " + opaqueKey + " on the sesver.");
  }


  public Set<IOSApplication> getSupportedApplications() {
    return supportedApplications;
  }

}
