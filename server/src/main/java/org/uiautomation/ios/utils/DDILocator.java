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


import org.libimobiledevice.ios.driver.binding.services.IOSDevice;
import org.openqa.selenium.WebDriverException;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DDILocator {

  private static File xcode = ClassicCommands.getXCodeInstall();
  private static String ddiPath = "/Contents/Developer/Platforms/iPhoneOS.platform/DeviceSupport";
  private static String ddiName = "DeveloperDiskImage.dmg";
  private static String ddiSign = "DeveloperDiskImage.dmg.signature";

  public static File locateDDI(IOSDevice device) {
    File ddiFolder = new File(xcode, ddiPath);
    List<File> alls = Arrays.asList(ddiFolder.listFiles());

    Collections.sort(alls);
    File f = alls.get((alls.size() - 1));
    File ddi = new File(f, ddiName);
    File ddiS = new File(f, ddiSign);
    if (ddi.exists() && ddiS.exists()) {
      return ddi;
    } else {
      throw new WebDriverException("cannot find " + ddi + " and " + ddiS);
    }

  }


  public static void main(String[] args) {
    System.err.println(DDILocator.locateDDI(null));
  }


}
