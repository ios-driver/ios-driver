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

package org.uiautomation.ios.application;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.IOSServer;
import org.uiautomation.ios.utils.ClassicCommands;
import org.uiautomation.ios.utils.IOSVersion;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class MobileSafariLocator {

  private static final Logger log = Logger.getLogger(MobileSafariLocator.class.getName());
  private static File xcode = ClassicCommands.getXCodeInstall();
  private static final Map<String, APPIOSApplication> safariCopies = new HashMap<>();

  public static APPIOSApplication locateSafari(String sdkVersion) {
    APPIOSApplication res = safariCopies.get(sdkVersion);
    if (res !=null){
      return res;
    }
   throw new WebDriverException("Cannot find safari for " + sdkVersion + " in the known mobile safari list.");
  }


  public static APPIOSApplication locateSafariInstall(String sdkVersion) {
    APPIOSApplication res = safariCopies.get(sdkVersion);
    if (res == null) {
      if (new IOSVersion(sdkVersion).isGreaterOrEqualTo("8.0")) {
        res = APPIOSApplication.findSafariApp(xcode, sdkVersion);
      } else {
        res = copyOfSafari(xcode, sdkVersion);
      }
      safariCopies.put(sdkVersion, res);
    }
    return res;
  }

  // TODO freynaud - if xcode change, the safari copy should be wiped out.
  private static APPIOSApplication copyOfSafari(File xcodeInstall, String sdk) {
    File
        copy =
        new File(IOSServer.getTmpIOSFolder().getAbsolutePath(),
                 "safariCopies/safari-" + sdk + ".app");
    if (!copy.exists()) {
      File safariFolder = APPIOSApplication.findSafariLocation(xcodeInstall, sdk);
      copy.mkdirs();
      try {
        FileUtils.copyDirectory(safariFolder, copy);
      } catch (IOException e) {
        log.warning("Cannot create the copy of safari : " + e.getMessage());
      }
    }
    return new APPIOSApplication(copy.getAbsolutePath());
  }
}
