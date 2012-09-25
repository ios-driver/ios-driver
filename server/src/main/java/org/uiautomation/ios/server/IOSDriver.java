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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.application.Localizable;
import org.uiautomation.ios.server.application.ResourceCache;
import org.uiautomation.ios.server.utils.BuildInfo;
import org.uiautomation.ios.server.utils.ClassicCommands;

public class IOSDriver {


  private final List<ServerSideSession> sessions = new ArrayList<ServerSideSession>();
  private final Set<IOSApplication> supportedApplications = new HashSet<IOSApplication>();
  private final HostInfo hostInfo;
  private final ResourceCache cache = new ResourceCache();

  public IOSDriver(int port) {
    this.hostInfo = new HostInfo(port);
  }

  public void addSupportedApplication(IOSApplication application) {
    boolean added = supportedApplications.add(application);
    if (!added) {
      throw new IOSAutomationException("app already present :" + application.getApplicationPath());
    }
    cache.cacheResource(application);
  }

  public ResourceCache getCache() {
    return cache;
  }
  
  public int getPort(){
    return hostInfo.getPort();
  }

  public ServerSideSession startSession(IOSCapabilities capabilities) {
    IOSApplication app = findMatchingApplication(capabilities);
    app.setLanguage(capabilities.getLanguage());
    if (capabilities.getSDKVersion() == null) {
      capabilities.setSDKVersion(ClassicCommands.getDefaultSDK());
    }
    ServerSideSession session = new ServerSideSession(app, hostInfo.getPort());
    sessions.add(session);
    try {
      session.start(capabilities);
    } catch (IOSAutomationException e) {
      sessions.remove(session);
      throw e;
    }

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
    List<String> languageCodes = new ArrayList<String>();
    for (Localizable l : application.getSupportedLanguages()) {
      languageCodes.add(l.getName());
    }
    cap.setSupportedLanguages(languageCodes);
    cap.setCapability("applicationPath", application.getApplicationPath().getAbsoluteFile());

    for (Iterator iterator = application.getMetadata().keys(); iterator.hasNext();) {
      String key = (String) iterator.next();

      try {
        Object value = application.getMetadata().get(key);
        cap.getRawCapabilities().put(key, value);
      } catch (JSONException e) {
        throw new IOSAutomationException("cannot get metadata", e);
      }
    }

    cap.setDevice(cap.getDeviceFromDeviceFamily());
    return cap;
  }

  private IOSApplication findMatchingApplication(IOSCapabilities desiredCapabilities) {
    for (IOSApplication app : supportedApplications) {
      IOSCapabilities appCapabilities = getCapabilities(app);
      if (IOSDriver.matches(appCapabilities, desiredCapabilities)) {
        return app;
      }
    }
    throw new IOSAutomationException(desiredCapabilities.getRawCapabilities()
        + "not found on server.");
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
      throw new IOSAutomationException("you need to specify the bundle to test.");
    }
    if (!desiredCapabilities.getBundleName().equals(applicationCapabilities.getBundleName())) {
      return false;
    }
    if (desiredCapabilities.getBundleVersion() != null
        && !desiredCapabilities.getBundleVersion().equals(
            applicationCapabilities.getBundleVersion())) {
      return false;
    }
    if (desiredCapabilities.getDevice() == null) {
      throw new IOSAutomationException("you need to specify the device.");
    }
    if (!(applicationCapabilities.getDeviceFromDeviceFamily() == desiredCapabilities.getDevice())) {
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
      throw new IOSAutomationException("Language requested, " + l
          + " ,isn't supported.Supported are : " + applicationCapabilities.getSupportedLanguages());
    }

    String sdk = desiredCapabilities.getSDKVersion();
    if (sdk != null && !sdk.equals(applicationCapabilities.getSDKVersion())) {
      throw new IOSAutomationException("Cannot start sdk " + sdk + ". Run on "
          + applicationCapabilities.getSDKVersion());
    }

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
    throw new IOSAutomationException("Cannot find session " + opaqueKey + " on the sesver.");
  }

  class HostInfo {
    private final String osName;
    private final String osArch;
    private final String osVersion;

    private final String javaVersion;

    private final String simulatorVersion;

    private final BuildInfo info = new BuildInfo();
    private final int port;

    public HostInfo(int port) {
      osName = System.getProperty("os.name");
      osArch = System.getProperty("os.arch");
      osVersion = System.getProperty("os.version");

      javaVersion = System.getProperty("java.version");

      String sdk = ClassicCommands.getDefaultSDK();
      simulatorVersion = sdk;

      this.port = port;
    }

    public String getSDK() {
      return simulatorVersion;
    }

    public int getPort() {
      return port;
    }
  }

  public Set<IOSApplication> getSupportedApplications() {
    return supportedApplications;
  }
}
