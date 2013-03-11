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

package org.uiautomation.ios.server.instruments;

import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.server.Device;
import org.uiautomation.ios.server.RealDevice;
import org.uiautomation.ios.server.application.IOSRunningApplication;
import org.uiautomation.ios.server.simulator.IOSRealDeviceManager;
import org.uiautomation.ios.server.simulator.IOSSimulatorManager;
import org.uiautomation.ios.utils.ClassicCommands;
import org.uiautomation.ios.utils.Command;
import org.uiautomation.ios.utils.ScriptHelper;
import org.uiautomation.ios.utils.hack.TimeSpeeder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class InstrumentsManager {

  //public static boolean realDevice = false;

  private static final Logger log = Logger.getLogger(InstrumentsManager.class.getName());

  private File output;
  private final File template;
  private IOSRunningApplication application;
  private Device device;
  private IOSDeviceManager deviceManager;
  private String sessionId;
  private final int port;
  private String sdkVersion;
  private String locale;
  private String language;
  private DeviceType deviceType;
  private DeviceVariation variation;
  private IOSCapabilities caps;
  private List<String> extraEnvtParams;
  private CommunicationChannel communicationChannel;
  private Command simulatorProcess;

  /**
   * constructor that will create an instrument process linked to the server.
   *
   * @param serverPort the port the server lives on
   */
  public InstrumentsManager(int serverPort) {
    template = ClassicCommands.getAutomationTemplate();
    this.port = serverPort;
  }

  public void startSession(String sessionId,
                           IOSRunningApplication application,
                           Device device,
                           IOSCapabilities capabilities) throws WebDriverException {

    this.device = device;
    caps = capabilities;

    deviceType = caps.getDevice();
    variation = caps.getDeviceVariation();
    sdkVersion = caps.getSDKVersion();
    locale = caps.getLocale();
    language = application.getAppleLocaleFromLanguageCode(caps.getLanguage())
        .getAppleLanguagesForPreferencePlist();
    boolean timeHack = caps.isTimeHack();
    List<String> envtParams = caps.getExtraSwitches();

    log.fine("starting session");

    try {
      this.sessionId = sessionId;
      this.extraEnvtParams = envtParams;

      output = createTmpOutputFolder();

      this.application = application;
      this.application.setDefaultDevice(deviceType);

      if (isWarmupRequired(sdkVersion)) {
        warmup();
      }
      log.fine("prepare simulator");
      deviceManager = prepareSimulator(capabilities);

      if (deviceManager instanceof IOSSimulatorManager) {
        log.fine("forcing SDK");
        ((IOSSimulatorManager) deviceManager).forceDefaultSDK();
        log.fine("creating script");
      }
      File
          uiscript =
          new ScriptHelper()
              .getScript(port, application.getDotAppAbsolutePath(), sessionId);
      log.fine("starting instruments");
      List<String> instruments = createInstrumentCommand(uiscript.getAbsolutePath());
      communicationChannel = new CommunicationChannel();

      simulatorProcess = new Command(instruments, true);
      simulatorProcess.setWorkingDirectory(output);
      simulatorProcess.start();

      log.fine("waiting for registration request");
      boolean success = communicationChannel.waitForUIScriptToBeStarted();
      // appears only in ios6. : Automation Instrument ran into an exception
      // while trying to run the
      // script. UIAScriptAgentSignaledException
      if (!success) {
        simulatorProcess.forceStop();
        killSimulator();
        throw new WebDriverException("Instruments crashed.");
      }

      if (timeHack) {
        TimeSpeeder.getInstance().activate();
        TimeSpeeder.getInstance().start();
      } else {
        TimeSpeeder.getInstance().desactivate();
      }

    } catch (Exception e) {
      if (simulatorProcess != null) {
        simulatorProcess.forceStop();
      }
      killSimulator();
      throw new WebDriverException(
          "error starting instrument for session " + sessionId + ", " + e.getMessage(), e);
    } finally {
      log.fine("start session done");

      if (deviceManager instanceof IOSSimulatorManager) {
        log.fine("forcing SDK");
        ((IOSSimulatorManager) deviceManager).restoreExiledSDKs();
        log.fine("creating script");
      }
    }

  }

  private void warmup() {
    File script = new ScriptHelper().createTmpScript("UIALogger.logMessage('warming up');");
    List<String> cmd = createInstrumentCommand(script.getAbsolutePath());
    Command c = new Command(cmd, true);
    c.executeAndWait();
  }

  private boolean isWarmupRequired(String sdkVersion) {
    List<String> sdks = ClassicCommands.getInstalledSDKs();
    // TODO freynaud not rely on order.
    if (sdks.get(sdks.size() - 1).equals(sdkVersion)) {
      return false;
    }
    return true;

    /*
     * if (sdkVersion.equals("5.0") || sdkVersion.equals("5.1")||
     * sdkVersion.equals("6.0")) { if (sdks.contains("6.1")) { return true; } }
     * return false;
     */
  }

  private IOSDeviceManager prepareSimulator(IOSCapabilities capabilities) {
    IOSDeviceManager deviceManager;

    if (device instanceof RealDevice) {
      deviceManager = new IOSRealDeviceManager(capabilities);
    } else {
      deviceManager = new IOSSimulatorManager(capabilities);
    }

    deviceManager.resetContentAndSettings();
    deviceManager.setL10N(locale, language);
    deviceManager.setKeyboardOptions();
    deviceManager.setVariation(deviceType, variation);
    deviceManager.setLocationPreference(true);
    return deviceManager;
  }

  public void stop() {
    if (device != null) {
      device.release();
    }
    TimeSpeeder.getInstance().stop();
    if (simulatorProcess != null) {
      simulatorProcess.waitFor();
    }
    killSimulator();
  }

  public void forceStop() {
    TimeSpeeder.getInstance().stop();
    if (simulatorProcess != null) {
      simulatorProcess.forceStop();
    }

  }

  private List<String> createInstrumentCommand(String script) {
    List<String> command = new ArrayList<String>();

    command.add(deviceManager.getInstrumentsClient());
    if (device instanceof RealDevice) {
      command.add("-w");
      command.add(((RealDevice) device).getUuid());
    }
    command.add("-t");
    command.add(template.getAbsolutePath());
    command.add(application.getDotAppAbsolutePath());
    command.add("-e");
    command.add("UIASCRIPT");
    command.add(script);
    command.add("-e");
    command.add("UIARESULTSPATH");
    command.add(output.getAbsolutePath());
    command.addAll(extraEnvtParams);
    StringBuilder b = new StringBuilder();
    for (String s : command) {
      b.append(s);
      b.append(" ");
    }
    log.warning("Starting instruments:\n" + b.toString());
    return command;

  }

  private File createTmpOutputFolder() throws IOException {
    output = File.createTempFile(sessionId, null);
    output.delete();
    output.mkdir();
    output.deleteOnExit();
    return output;
  }

  private void killSimulator() {
    if (deviceManager != null) {
      deviceManager.cleanupDevice();
    }
  }

  public String getSessionId() {
    return sessionId;
  }

  public File getOutput() {
    return output;
  }

  public CommunicationChannel communicate() {
    return communicationChannel;
  }
}
