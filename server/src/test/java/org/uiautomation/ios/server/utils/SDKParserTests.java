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

package org.uiautomation.ios.server.utils;


import org.testng.annotations.Test;
import org.uiautomation.ios.utils.ShowSDKsParser;

public class SDKParserTests {

  private String noWarning = "OS X SDKs:\n"
                             + "\tOS X 10.8                     \t-sdk macosx10.8\n"
                             + "\tOS X 10.9                     \t-sdk macosx10.9\n"
                             + "\n"
                             + "iOS SDKs:\n"
                             + "\tiOS 7.1                       \t-sdk iphoneos7.1\n"
                             + "\n"
                             + "iOS Simulator SDKs:\n"
                             + "\tSimulator - iOS 6.0           \t-sdk iphonesimulator6.0\n"
                             + "\tSimulator - iOS 6.1           \t-sdk iphonesimulator6.1\n"
                             + "\tSimulator - iOS 7.0           \t-sdk iphonesimulator7.0\n"
                             + "\tSimulator - iOS 7.1           \t-sdk iphonesimulator7.1\n";
  private
  String
      warning =
      "2014-05-13 14:56:34.997 xcodebuild[3246:1207] [MT] PluginLoading: Required plug-in compatibility UUID 37B30044-3B14-46BA-ABAA-F01000C27B63 for plug-in at path '~/Library/Application Support/Developer/Shared/Xcode/Plug-ins/Unity4XC.xcplugin' not present in DVTPlugInCompatibilityUUIDs\n"
      + "OS X SDKs:\n"
      + "OS X 10.8 -sdk macosx10.8\n"
      + "\n"
      + "iOS SDKs:\n"
      + "iOS 7.0 -sdk iphoneos7.0\n"
      + "\n"
      + "iOS Simulator SDKs:\n"
      + "Simulator - iOS 5.0 -sdk iphonesimulator5.0\n"
      + "Simulator - iOS 5.1 -sdk iphonesimulator5.1\n"
      + "Simulator - iOS 6.0 -sdk iphonesimulator6.0\n"
      + "Simulator - iOS 7.0 -sdk iphonesimulator7.0";

  @Test
  public void canParseNormalOutput() {
    ShowSDKsParser parser = new ShowSDKsParser();
    String[] lines = noWarning.split("\n");
    for (String line : lines) {
      parser.stdout(line);
    }
    System.out.println(parser.getSDKs());
  }


  @Test
  public void canParseWarningOutput() {
    ShowSDKsParser parser = new ShowSDKsParser();
    String[] lines = warning.split("\n");
    for (String line : lines) {
      parser.stdout(line);
    }
    System.out.println(parser.getSDKs());
  }
}
