/*
 * Copyright 2012-2014 eBay Software Foundation and ios-driver committers
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

package org.uiautomation.ios.utils;


import org.openqa.selenium.WebDriverException;

public class SimulatorProductVersionParser implements CommandOutputListener {
  // gets ProductVersion by parsing:
  // iPhoneSimulator7.0.sdk - Simulator - iOS 7.0 (iphonesimulator7.0)
  // SDKVersion: 7.0
  // Path: /Applications/Xcode.app/Contents/Developer/Platforms/iPhoneSimulator.platform/Developer/SDKs/iPhoneSimulator7.0.sdk
  // PlatformPath: /Applications/Xcode.app/Contents/Developer/Platforms/iPhoneSimulator.platform
  // ProductBuildVersion: 11B508
  // ProductCopyright: 1983-2013 Apple Inc.
  // ProductName: iPhone OS
  // ProductVersion: 7.0.3

  private final String sdkVersion;
  private String productVersion;
  private boolean inSdkVersion;

  SimulatorProductVersionParser(String sdkVersion) {
    this.sdkVersion = sdkVersion;
  }

  String getProductVersion() {
    if (productVersion == null) {
      throw new WebDriverException("couldn't find simulator product version for " + sdkVersion);
    }
    return productVersion;
  }

  @Override
  public void stdout(String line) {
    ClassicCommands.log.fine(line);
    // SDKVersion: 7.0
    if (line.startsWith("SDKVersion:")) {
      String version = line.substring(12).trim();
      inSdkVersion = sdkVersion.equals(version);
    }
    if (inSdkVersion) {
      // ProductVersion: 7.0.3
      if (line.startsWith("ProductVersion:")) {
        productVersion = line.substring(16).trim();
      }
    }
  }

  @Override
  public void stderr(String line) {
    ClassicCommands.log.warning(line);
  }
}