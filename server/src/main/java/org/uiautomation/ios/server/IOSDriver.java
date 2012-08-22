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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    cache.cacheResource(application.getApplicationPath().getAbsolutePath());
  }

  public ResourceCache getCache() {
    return cache;
  }

  public ServerSideSession startSession(IOSCapabilities capabilities) {
    IOSApplication app = findMatchingApplication(capabilities);
    app.setLanguage(capabilities.getLanguage());
    ServerSideSession session = new ServerSideSession(app, hostInfo.getPort());
    sessions.add(session);
    session.start(capabilities);
    return session;
  }

  public void stop(String opaqueKey) {
    ServerSideSession session = getSession(opaqueKey);
    session.stop();
    sessions.remove(session);
  }

  private IOSApplication findMatchingApplication(IOSCapabilities desiredCapabilities) {
    for (IOSApplication app : supportedApplications) {
      if (app.matches(desiredCapabilities)) {
        return app;
      }
    }
    throw new IOSAutomationException(desiredCapabilities + "not found on server.");
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

      // TODO find a way to get the default SDK the simulator launches when invoked via instruments
      List<String> sdks = ClassicCommands.getInstalledSDKs();
      simulatorVersion = sdks.get(sdks.size() - 1);

      this.port = port;
    }

    public int getPort() {
      return port;
    }
  }

  public Set<IOSApplication> getSupportedApplications() {
    return supportedApplications;
  }
}
