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
import org.libimobiledevice.ios.driver.binding.exceptions.LibImobileException;
import org.libimobiledevice.ios.driver.binding.exceptions.SDKException;
import org.libimobiledevice.ios.driver.binding.services.DeviceService;
import org.libimobiledevice.ios.driver.binding.services.IOSDevice;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.application.APPIOSApplication;
import org.uiautomation.ios.server.application.IOSRunningApplication;
import org.uiautomation.ios.server.application.IPAApplication;
import org.uiautomation.ios.server.command.UIAScriptRequest;
import org.uiautomation.ios.server.command.UIAScriptResponse;
import org.uiautomation.ios.server.instruments.IOSDeviceManager;
import org.uiautomation.ios.server.instruments.InstrumentsVersion;
import org.uiautomation.ios.server.instruments.communication.CommunicationChannel;
import org.uiautomation.ios.server.instruments.communication.curl.CURLBasedCommunicationChannel;
import org.uiautomation.ios.server.services.Instruments;
import org.uiautomation.ios.server.services.InstrumentsAppleScreenshotService;
import org.uiautomation.ios.server.services.TakeScreenshotService;
import org.uiautomation.ios.utils.ApplicationCrashListener;
import org.uiautomation.ios.utils.ClassicCommands;
import org.uiautomation.ios.utils.Command;
import org.uiautomation.ios.utils.IOSVersion;
import org.uiautomation.ios.utils.ScriptHelper;

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
  private long instrumentsPid = -1;

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

    File scriptPath = new ScriptHelper().getScript(port, appPath, sessionId, CURL,caps.isAcceptAllCerts());
    output = createTmpOutputFolder();

    if (uuid == null) {
      deviceManager = new IOSSimulatorManager(caps);
    } else {
      IPAApplication ipa = (IPAApplication) application.getUnderlyingApplication();
      IOSDevice d = null;
      try {
        d = DeviceService.get(uuid);
      } catch (SDKException e) {
        e.printStackTrace();
      } catch (LibImobileException e) {
        e.printStackTrace();
      }
      deviceManager = new IOSRealDeviceManager(caps, d, ipa);
    }

    instruments = createInstrumentCommand(scriptPath);
    instruments.registerListener(new ApplicationCrashListener(session));
    instruments.setWorkingDirectory(output);

    channel = new CURLBasedCommunicationChannel(sessionId);

    screenshotService = new InstrumentsAppleScreenshotService(this, sessionId);

    safariFolder =
        APPIOSApplication.findSafariLocation(ClassicCommands.getXCodeInstall(), desiredSDKVersion);
  }

  @Override
  public void start(long timeout) throws InstrumentsFailedToStartException {
    DeviceType deviceType = caps.getDevice();
    DeviceVariation variation = caps.getDeviceVariation();
    String locale = caps.getLocale();
    String language = caps.getLanguage();
    String instrumentsVersion = version.getVersion();
    boolean instrumentsIs50OrHigher = new IOSVersion(instrumentsVersion).isGreaterOrEqualTo("5.0");
    boolean isSDK70OrHigher = new IOSVersion(desiredSDKVersion).isGreaterOrEqualTo("7.0");
    boolean putDefaultFirst = instrumentsIs50OrHigher;

    // the 5.0 instruments can't start MobileSafari
    // workaround is to remove the MobileSafari app from the install directory and put
    // it back after instruments starts it
    boolean
        tempRemoveMobileSafari =
        instrumentsIs50OrHigher && application.isSafari() && application.isSimulator();

    if (tempRemoveMobileSafari) {
      moveMobileSafariAppOutOfInstallDir();
    }

    deviceManager.setVariation(deviceType, variation);
    deviceManager.setSimulatorScale(caps.getSimulatorScale());
    application.setDefaultDevice(deviceType, putDefaultFirst);


    if (application.isSafari() && isSDK70OrHigher && application.isSimulator()) {
      application.setSafariBuiltinFavorites();
    }
    deviceManager.resetContentAndSettings();
    deviceManager.setL10N(locale, language);
    deviceManager.setKeyboardOptions();
    deviceManager.setLocationPreference(true);
    deviceManager.setMobileSafariOptions();

    deviceManager.installTrustStore(session.getOptions().getTrustStore());

    boolean success = false;
    try {
      instruments.start();
      // for the no delay instruments, the command launches a script that in turn launches instruments.
      // need to keep the pid of intruments itself to be able to kill it.

      // let the process spawn
      Thread.sleep(2000);
      instrumentsPid = ClassicCommands.getHighestPidForName("instruments");

      log.fine("waiting for registration request");
      success = channel.waitForUIScriptToBeStarted(timeout);
      log.fine("registration request received" + session.getCachedCapabilityResponse());
      if (!success) {
        log.warning("instruments crashed (" + timeout + " sec)".toUpperCase());
        throw new InstrumentsFailedToStartException(
            "Didn't get the capability back.Most likely, instruments crashed at startup.");
      }
    } catch (InterruptedException e) {
      throw new InstrumentsFailedToStartException("instruments was interrupted while starting.");
    } finally {
      // appears only in ios6. : Automation Instrument ran into an exception
      // while trying to run the script. UIAScriptAgentSignaledException
      if (!success) {
        instruments.forceStop();
        deviceManager.cleanupDevice();
      }
      putMobileSafariAppBackInInstallDir();
    }
  }

  @Override
  public void stop() {
    deviceManager.cleanupDevice();
    instruments.forceStop();
    ClassicCommands.kill(instrumentsPid);
    channel.stop();
    putMobileSafariAppBackInInstallDir();
  }

  private Command createInstrumentCommand(File script) {
    List<String> args = new ArrayList<>();

    args.add(getInstrumentsClient());
    args.add("-v");
    if (uuid != null) {
      args.add("-w");
      args.add(uuid);
    } else if (application.isSimulator() && Integer.parseInt(version.getBuild()) >= 55044) {
      // newer instruments require to specify the simulator SDK and device type
      args.add("-w");
      args.add(deviceManager.getDeviceSpecification(caps.getDevice(), caps.getDeviceVariation()));
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
    return new Command(args, false);
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

  public File getOutput() {
    return output;
  }

  /**
   * @return true if MobileSafari was moved out, false otherwise
   */
  private boolean moveMobileSafariAppOutOfInstallDir() {
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
        return false; // don't remove existing copy in this case
      }
    }

    // delete MobileSafari in install dir
    try {
      FileUtils.deleteDirectory(safariFolder);
      if (!howToMoveSafariBackMessageGiven) {
        howToMoveSafariBackMessageGiven = true;
        String to = safariFolder.getAbsolutePath();
        log.info(
            "temporarily moving MobileSafari out of the install directory, if you need to restore it yourself use:\n$ cp -rf "
            + copy + ' ' + to);
      }
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

    return true;
  }

  private boolean howToMoveSafariBackMessageGiven;

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
