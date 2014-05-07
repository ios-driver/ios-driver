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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import org.uiautomation.ios.utils.ClassicCommands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * Class IOSServerConfiguration Configures with the given configurable arguments: -port <port> #
 * Start server in port with value <port>. Default port = 5555 -host <host_name> # Start server with
 * the provided <host_name>. Default host = 'localhost'
 * 
 * String[] getSupportedApps(): Loads the supported apps from inside the APPS_FILE. Loading failure:
 * Generates and Throws a CustomMessage.class 'error' message Loading Success: Generates and Throws
 * a CustomMessage.class 'notice' message
 */
public class IOSServerConfiguration {


  @Parameter(description = "enable beta feature.Might be unstable.", names = "-beta")
  private boolean beta = false;

  @Parameter(description = "port the server will listen on.", names = "-port")
  private int port = 5555;

  @Parameter(
      description = "if specified, will send a registration request to the given url. Example : http://localhost:4444/grid/register",
      names = "-hub")
  private String registrationURL = null;

  @Parameter(
      description = "if specified, will specify the remote proxy to use on the grid. Example : org.uiautomation.ios.grid.IOSRemoteProxy",
      names = "-proxy")
  private String proxy = null;

  @Parameter(
      description = "host of the node.Needs to be specified, as guessing can be wrong complex ntw configs",
      names = "-host")
  private String serverHost;

  @Parameter(description = "location of the application under test. Absolute path or URL expected.",
             names = {"-app", "-aut"}, required = false)
  private List<String> supportedApps = new ArrayList<String>();

  @Parameter(
      description =
          "location of a folder to monitor where applications will be stored. Absolute path expected. "
          +
          "Any apps in the folder on launch will be automatically added to the desired capabilities. "
          +
          "Real device archived apps will also be backed up to this location, by default it will use the running folder /applications",
      names = {"-folder"}, required = false)
  private String appFolderToMonitor = null;

  @Parameter(
      description = "maximum session duration in seconds. Session will be forcefully terminated if it takes longer.",
      names = "-sessionTimeout")
  private int sessionTimeoutSeconds = 30 * 60; // 30 minutes

  @Parameter(
      description = "location of the TrustStore.sqlite3 to use for the simulator.",
      names = "-trustStore")
  private String trustStore = null;

  @Parameter(description = "supported real device uuid to whitelist", names = "-uuid")
  private List<String> uuidWhitelist = new ArrayList<String>();

  public String getRegistrationURL() {
    return registrationURL;
  }

  public void addSupportedApp(String appAbsolutPath) {
    supportedApps.add(appAbsolutPath);
  }

  public List<String> getSupportedApps() {
    return supportedApps;
  }

  public String serverHost() {
    return serverHost;
  }

  /**
   * Returns a IOSServerConfiguration instance of the server configuration, from the given args
   * parameters.<br>
   *
   * @return A configuration instance of the server.
   */
  public static IOSServerConfiguration create(String[] args) {
    IOSServerConfiguration res = new IOSServerConfiguration();
    new JCommander(res).parse(args);
    return res;
  }

  public void setPort(int port) {
    this.port = port;
  }


  public void setHost(String host) {
    this.serverHost = host;
  }

  public int getPort() {
    return this.port;
  }

  public String getHost() {
    return this.serverHost;
  }

  public boolean isBeta() {
    return beta;
  }

  public void setBeta(boolean beta) {
    this.beta = beta;
  }

  public boolean hasSimulators() {
    if (!"Mac OS X".equals(System.getProperty("os.name"))) {
      return false;
    }
    if (ClassicCommands.getInstalledSDKs().size() == 0) {
      return false;
    }
    return true;
  }


  public String getAppFolderToMonitor() {
    if (appFolderToMonitor == null) {
      File folder = new File("applications");
      appFolderToMonitor = folder.getAbsolutePath();
    }
    return appFolderToMonitor;
  }

  public int getSessionTimeoutMillis() {
    return sessionTimeoutSeconds * 1000;
  }

  public String getProxy() {
    return proxy;
  }

  public void setProxy(String proxy) {
    this.proxy = proxy;
  }

  public String getTrustStore() {
    return trustStore;
  }

  public Set<String> getUuidWhitelist() {
    return Collections.unmodifiableSet(new HashSet<>(uuidWhitelist));
  }
}
