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

package org.uiautomation.ios.server.simulator;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.application.APPIOSApplication;
import org.uiautomation.ios.server.application.IOSRunningApplication;
import org.uiautomation.ios.server.command.UIAScriptRequest;
import org.uiautomation.ios.server.command.UIAScriptResponse;
import org.uiautomation.ios.server.instruments.IOSDeviceManager;
import org.uiautomation.ios.server.instruments.InstrumentsVersion;
import org.uiautomation.ios.server.instruments.communication.CommunicationChannel;
import org.uiautomation.ios.server.instruments.communication.curl.CURLBasedCommunicationChannel;
import org.uiautomation.ios.server.services.Instruments;
import org.uiautomation.ios.server.services.InstrumentsAppleScreenshotService;
import org.uiautomation.ios.server.services.TakeScreenshotService;
import org.uiautomation.ios.utils.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.uiautomation.ios.server.instruments.communication.CommunicationMode.CURL;

public class InstrumentsApple implements Instruments {

  private static final Logger log = Logger.getLogger(InstrumentsApple.class.getName());
  private final String uuid;
  private final File template;
  private final IOSRunningApplication application;
  private final File output;
  private final String sessionId;
  private final List<String> envtParams;
  private final Command instruments;
  private final CURLBasedCommunicationChannel channel;
  private final InstrumentsVersion version;
  private final TakeScreenshotService screenshotService;
  private final IOSDeviceManager deviceManager;
  private final IOSCapabilities caps;
  private final String desiredSDKVersion;
  private final File safariFolder;
  private final ServerSideSession session;

  public InstrumentsApple(String uuid, InstrumentsVersion version, int port, String sessionId,
                          IOSRunningApplication application,
                          List<String> envtParams, IOSCapabilities caps,
                          ServerSideSession session) {
    this.uuid = uuid;
    this.caps = caps;
    this.version = version;
    this.sessionId = sessionId;
    this.application = application;
    this.envtParams = envtParams;
    this.session = session;
    this.desiredSDKVersion = caps.getSDKVersion();
    template = ClassicCommands.getAutomationTemplate();

    String appPath = application.getDotAppAbsolutePath();
    File scriptPath = new ScriptHelper().getScript(port, appPath, sessionId, CURL);
    output = createTmpOutputFolder();

    instruments = createInstrumentCommand(scriptPath);
    instruments.registerListener(new ApplicationCrashListener(session));
    instruments.setWorkingDirectory(output);

    channel = new CURLBasedCommunicationChannel(sessionId);

    screenshotService = new InstrumentsAppleScreenshotService(this, sessionId);
    deviceManager = new IOSSimulatorManager(caps, this);
    
    safariFolder = APPIOSApplication.findSafariLocation(ClassicCommands.getXCodeInstall(), desiredSDKVersion);
  }

  @Override
  public void start() throws InstrumentsFailedToStartException {
    DeviceType deviceType = caps.getDevice();
    DeviceVariation variation = caps.getDeviceVariation();
    String locale = caps.getLocale();
    String language = caps.getLanguage();
    String instrumentsVersion = version.getVersion();
    boolean instrumentsIs50OrHigher = new IOSVersion(instrumentsVersion).isGreaterOrEqualTo("5.0");
    boolean putDefaultFirst = instrumentsIs50OrHigher;
    
    // the 5.0 instruments can't start MobileSafari
    // workaround is to remove the MobileSafari app from the install directory and put
    // it back after instruments starts it
    boolean tempRemoveMobileSafari = instrumentsIs50OrHigher && application.isSafari() && application.isSimulator();
    
    if (tempRemoveMobileSafari)
      moveMobileSafariAppOutOfInstallDir();

    deviceManager.setVariation(deviceType, variation);
    this.application.setDefaultDevice(deviceType, putDefaultFirst);
    deviceManager.setSDKVersion();
    deviceManager.resetContentAndSettings();
    deviceManager.setL10N(locale, language);
    deviceManager.setKeyboardOptions();
    deviceManager.setLocationPreference(true);
    deviceManager.setMobileSafariOptions();
    
    if (application.isSimulator())
      installTrustStore();

    boolean success = false;
    try {
      instruments.start();
      
      log.fine("waiting for registration request");
      success = channel.waitForUIScriptToBeStarted();
    } catch (InterruptedException e) {
      throw new InstrumentsFailedToStartException("instruments was interrupted while starting.");
    } finally {
      // appears only in ios6. : Automation Instrument ran into an exception
      // while trying to run the script. UIAScriptAgentSignaledException
      if (!success) {
        instruments.forceStop();
        deviceManager.cleanupDevice();
        throw new InstrumentsFailedToStartException("Instruments crashed.");
      }
      putMobileSafariAppBackInInstallDir();
    }
  }

  @Override
  public void stop() {
    deviceManager.cleanupDevice();
    instruments.forceStop();
    channel.stop();
    putMobileSafariAppBackInInstallDir();
  }

  public void startWithDummyScript() {
    File script = new ScriptHelper().createTmpScript("UIALogger.logMessage('warming up');");
    Command c = createInstrumentCommand(script);
    c.executeAndWait();
  }

  private Command createInstrumentCommand(File script) {
    List<String> args = new ArrayList<>();

    args.add(getInstrumentsClient());
    if (uuid != null) {
      args.add("-w");
      args.add(uuid);
    }
    args.add("-t");
    args.add(template.getAbsolutePath());
    args.add(application.getDotAppAbsolutePath());
    args.add("-e");
    args.add("UIASCRIPT");
    args.add(script.getAbsolutePath());
    args.add("-e");
    args.add("UIARESULTSPATH");
    args.add(output.getAbsolutePath());
    args.addAll(envtParams);
    return new Command(args, true);
  }

  private File createTmpOutputFolder() {
    try {
      File output = File.createTempFile(sessionId, null);
      output.delete();
      output.mkdir();
      output.deleteOnExit();
      return output;
    } catch (IOException e) {
      throw new WebDriverException(
          "Cannot create the tmp folder where all the instruments tmp files"
          + "will be stored.", e);
    }
  }

  private String getInstrumentsClient() {
    return InstrumentsNoDelayLoader.getInstance(version).getInstruments().getAbsolutePath();
  }

  @Override
  public Response executeCommand(UIAScriptRequest request) {
    UIAScriptResponse res = channel.executeCommand(request);
    return res.getResponse();
  }

  @Override
  public CommunicationChannel getChannel() {
    return channel;
  }

  @Override
  public TakeScreenshotService getScreenshotService() {
    return screenshotService;
  }

  public File getOutput(){
    return output;
  }
  
  private void installTrustStore() {
    String trustStore = session.getOptions().getTrustStore();
    if (trustStore != null) {
      log.info("installing -trustStore: " + trustStore);
      // executes:
      // mkdir ~/"Library/Application Support/iPhone Simulator/7.0/Library/Keychains"
      // cp libs/ios/TrustStore.sqlite3 ~/"Library/Application Support/iPhone Simulator/7.0/Library/Keychains"
      File keychainsDir = new File(System.getProperty("user.home") + "/Library/Application Support/iPhone Simulator/"
        + desiredSDKVersion + "/Library/Keychains");
      File sourceFile = new File(trustStore);
      if (!sourceFile.exists()) {
          log.severe("-trustStore: source trust store file doesn't exist: " + sourceFile.getAbsolutePath());
          return;
      }
      File destFile = new File(keychainsDir, "TrustStore.sqlite3");
      try {
        if (!keychainsDir.exists()) {
            if (!keychainsDir.mkdir()) {
                log.severe("-trustStore: could not create Keychains dir: " + keychainsDir.getAbsolutePath());
                return;
            }
        }
        FileUtils.copyFile(sourceFile, destFile, false);
      } catch (Exception e) {
        log.severe("cannot install trust store file " + sourceFile.getAbsolutePath()
                + " to " + destFile.getAbsolutePath() + ": " + e);
      }
    }
  }
  
  /**
   * @return true if MobileSafari was moved out, false otherwise
   */
  private boolean moveMobileSafariAppOutOfInstallDir() {
    // make backup copy before deleting
    File copy = new File(System.getProperty("user.home")+"/.ios-driver/safariCopies", "MobileSafari-" + desiredSDKVersion + ".app");
    if (!copy.exists()) {
      copy.mkdirs();
      try {
        FileUtils.copyDirectory(safariFolder, copy);
        log.fine("copied " + safariFolder.getAbsolutePath() + " to " + copy.getAbsolutePath());
      } catch (IOException e) {
        log.log(Level.SEVERE, "Cannot create backup copy of safari : " + e.getMessage(), e);
        return false; // don't remove existing copy in this case
      }
    }
    
    // delete MobileSafari in install dir
    try {
      FileUtils.deleteDirectory(safariFolder);
    } catch (IOException e) {
      log.log(Level.WARNING, "couldn't delete MobileSafari app install dir: " + e.getMessage(), e);
      log.warning("make sure ios-driver has read/write permissions to that folder by executing those 2 commands:\n" + 
              "sudo chmod a+rw " + safariFolder.getParentFile().getAbsolutePath() + '\n' +
              "sudo chmod -R a+rw " + safariFolder.getAbsolutePath());
    }
    
    return true;
  }

  private void putMobileSafariAppBackInInstallDir() {
    if (safariFolder.exists()) {
      log.fine("not restoring MobileSafari.app, folder already exists: " + safariFolder.getAbsolutePath());
      return;
    }
    
    File copy = new File(System.getProperty("user.home")+"/.ios-driver/safariCopies", "MobileSafari-" + desiredSDKVersion + ".app");
    safariFolder.mkdir();
    try {
      FileUtils.copyDirectory(copy, safariFolder);
    } catch (IOException e) {
      log.warning("cannot copy MobileSafari app back to: " + safariFolder.getAbsolutePath() + ": " + e); 
    }
  }
}
