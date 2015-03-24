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

package org.uiautomation.ios.setup;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.application.APPIOSApplication;
import org.uiautomation.ios.application.IOSRunningApplication;
import org.uiautomation.ios.utils.ClassicCommands;
import org.uiautomation.ios.utils.IOSVersion;

import java.io.File;
import java.io.IOException;
import java.io.NotActiveException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class IOSSafariSimulatorManager extends IOSSimulatorManager {

  private static final Logger log = Logger.getLogger(IOSSafariSimulatorManager.class.getName());
  /**
   * manages a single instance of the instruments process. Only 1 process can run at a given time.
   */
  private final String desiredSDKVersion;
  private final File safariFolder;
  private boolean howToMoveSafariBackMessageGiven;

  public IOSSafariSimulatorManager(ServerSideSession session) throws NotActiveException {

    super(session);
    this.desiredSDKVersion = session.getCapabilities().getSDKVersion();
    safariFolder =
        APPIOSApplication.findSafariLocation(ClassicCommands.getXCodeInstall(), desiredSDKVersion);
  }


  @Override
  public void setup(){
    if (!new IOSVersion(session.getCapabilities().getSDKVersion()).isGreaterOrEqualTo("8.0")) {
      copySafariToAllowInstallByInstruments();
      setFavorite();
    }

    super.setup();
    simulatorSettings.setMobileSafariOptions();
    simulatorSettings.installTrustStore(session.getOptions().getTrustStore());
  }


  public void tmpFix(){
    putMobileSafariAppBackInInstallDir();
  }

  @Override
  public void teardown(){
    super.teardown();
    putMobileSafariAppBackInInstallDir();
  }



  private void setFavorite(){
    boolean isSDK70OrHigher = new IOSVersion(session.getCapabilities().getSDKVersion()).isGreaterOrEqualTo(
        "7.0");
    IOSRunningApplication application = session.getApplication();
    if (application.isSafari() && isSDK70OrHigher && application.isSimulator()) {
      application.setSafariBuiltinFavorites();
    }
  }

  private void copySafariToAllowInstallByInstruments() {
    // make backup copy before deleting
    File
        copy =
        new File(System.getProperty("user.home") + "/.ios-driver/safariCopies",
                 "MobileSafari-" + desiredSDKVersion + ".app");
    if (!copy.exists()) {
      copy.mkdirs();
      try {
        FileUtils.copyDirectory(safariFolder, copy);
        log.fine("copied " + safariFolder.getAbsolutePath() + " to " + copy.getAbsolutePath());
      } catch (IOException e) {
        log.log(Level.SEVERE, "Cannot create backup copy of safari : " + e.getMessage(), e);
        throw new WebDriverException("Cannot create backup copy of safari : " + e.getMessage());
      }
    }

    // delete MobileSafari in install dir
    try {
      FileUtils.deleteDirectory(safariFolder);
      if (!howToMoveSafariBackMessageGiven) {
        howToMoveSafariBackMessageGiven = true;
        String to = safariFolder.getAbsolutePath();
        log.info("temporarily moving MobileSafari out of the install directory, if you need to restore it yourself use: $ cp -rf " + copy + ' ' + to);}
    } catch (IOException e) {
      log.log(Level.SEVERE,
              "----------------------------------------------------------------------------");
      StringBuilder sb = new StringBuilder();
      sb.append(
          "\n---------> R E A D   T H I S:\ncouldn't delete MobileSafari app install dir: " + e
              .getMessage());
      sb.append(
          "\nmake sure ios-driver has read/write permissions to that folder by executing those 2 commands:");
      sb.append("\n\t$ sudo chmod a+rw " + safariFolder.getParentFile().getAbsolutePath());
      sb.append("\n\t$ sudo chmod -R a+rw " + safariFolder.getAbsolutePath());
      String em = sb.toString();
      log.log(Level.SEVERE, em, e);
      log.log(Level.SEVERE,
              "----------------------------------------------------------------------------");
      throw new WebDriverException(em);
    }
  }


  private void putMobileSafariAppBackInInstallDir() {
    if (safariFolder.exists()) {
      log.fine("not restoring MobileSafari.app, folder already exists: " + safariFolder
          .getAbsolutePath());
      return;
    }

    File
        copy =
        new File(System.getProperty("user.home") + "/.ios-driver/safariCopies",
                 "MobileSafari-" + desiredSDKVersion + ".app");
    safariFolder.mkdir();
    try {
      FileUtils.copyDirectory(copy, safariFolder);
    } catch (IOException e) {
      log.warning(
          "cannot copy MobileSafari app back to: " + safariFolder.getAbsolutePath() + ": " + e);
    }
  }
}
