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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.Device;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.server.application.AppleLocale;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.simulator.IOSSimulatorManager;
import org.uiautomation.ios.server.utils.ClassicCommands;
import org.uiautomation.ios.server.utils.Command;
import org.uiautomation.ios.server.utils.ScriptHelper;
import org.uiautomation.ios.server.utils.hack.TimeSpeeder;

public class InstrumentsManager {

  private static final Logger log = Logger.getLogger(InstrumentsManager.class.getName());

  private File output;
  private final File template;
  private IOSApplication application;
  private IOSDeviceManager simulator;
  private String sessionId;
  private final int port;

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

  public void startSession(Device device, DeviceVariation variation, String sdkVersion,
                           String locale, String language,
                           IOSApplication application, String sessionId, boolean timeHack,
                           List<String> envtParams)
      throws WebDriverException {
    log.fine("starting session");
    IOSSimulatorManager sim = null;
    try {
      this.sessionId = sessionId;
      this.extraEnvtParams = envtParams;

      output = createTmpOutputFolder();

      this.application = application;
      this.application.setDefaultDevice(device);

      if (isWarmupRequired(sdkVersion)) {
        warmup();
      }
      log.fine("prepare simulator");
      simulator =
          prepareSimulator(sdkVersion, device, variation, locale, language,
                           application.getMetadata(IOSCapabilities.BUNDLE_ID));
      sim = (IOSSimulatorManager) simulator;
      log.fine("forcing SDK");
      sim.forceDefaultSDK(sdkVersion);
      log.fine("creating script");
      File
          uiscript =
          new ScriptHelper()
              .getScript(port, application.getApplicationPath().getAbsolutePath(), sessionId);
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
      if (sim != null) {
        sim.restoreExiledSDKs();
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

  private IOSDeviceManager prepareSimulator(String sdkVersion, Device device,
                                            DeviceVariation variation, String locale,
                                            String language, String bundleId) {
    // TODO freynaud handle real device ?
    IOSDeviceManager simulator = new IOSSimulatorManager(sdkVersion, device);
    simulator.resetContentAndSettings();
    simulator.setL10N(locale, language);
    simulator.setKeyboardOptions();
    simulator.setVariation(device, variation);
    simulator.setLocationPreference(true, bundleId);
    return simulator;
  }

  public void stop() {
    TimeSpeeder.getInstance().stop();
    simulatorProcess.waitFor();
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
    command.add("instruments");
    command.add("-t");
    command.add(template.getAbsolutePath());
    command.add(application.getApplicationPath().getAbsolutePath());
    command.add("-e");
    command.add("UIASCRIPT");
    command.add(script);
    command.add("-e");
    command.add("UIARESULTSPATH");
    command.add(output.getAbsolutePath());
    command.addAll(extraEnvtParams);

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
    if (simulator != null) {
      simulator.cleanupDevice();
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
