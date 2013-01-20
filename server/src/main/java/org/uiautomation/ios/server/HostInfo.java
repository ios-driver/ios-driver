/*
 * Copyright 2012 ios-driver committers.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the Licence at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */

package org.uiautomation.ios.server;

import org.uiautomation.ios.server.utils.BuildInfo;
import org.uiautomation.ios.server.utils.ClassicCommands;

import java.io.File;
import java.util.List;

public class HostInfo {

  private final String osName;
  private final String osArch;
  private final String osVersion;

  private final String javaVersion;

  private final String simulatorVersion;
  private final List<String> installedSimulators;
  private final File xCodeInstall;

  private final BuildInfo info = new BuildInfo();
  private final int port;

  public HostInfo(int port) {
    osName = System.getProperty("os.name");
    osArch = System.getProperty("os.arch");
    osVersion = System.getProperty("os.version");

    javaVersion = System.getProperty("java.version");

    String sdk = ClassicCommands.getDefaultSDK();
    simulatorVersion = sdk;
    installedSimulators = ClassicCommands.getInstalledSDKs();
    this.port = port;
    xCodeInstall = ClassicCommands.getXCodeInstall();
  }

  public String getSDK() {
    return simulatorVersion;
  }

  public File getXCodeInstall() {
    return xCodeInstall;
  }

  public List<String> getInstalledSDKs() {
    return installedSimulators;
  }

  public int getPort() {
    return port;
  }
}
