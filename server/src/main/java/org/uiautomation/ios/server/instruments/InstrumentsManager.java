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

import org.uiautomation.ios.communication.IOSDevice;
import org.uiautomation.ios.exceptions.IOSAutomationSetupException;
import org.uiautomation.ios.server.simulator.IOSSimulatorManager;
import org.uiautomation.ios.server.utils.Command;
import org.uiautomation.ios.server.utils.ScriptHelper;
import org.uiautomation.ios.server.utils.hack.TimeSpeeder;

public class InstrumentsManager {

  private File output;
  private final File template;
  private File application;
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
   * @throws IOSAutomationSetupException
   */
  public InstrumentsManager(int serverPort) throws IOSAutomationSetupException {
    File t =
        new File("/Applications/Xcode.app/Contents/Developer/"
            + "Platforms/iPhoneOS.platform/Developer/Library/"
            + "Instruments/PlugIns/AutomationInstrument.bundle/"
            + "Contents/Resources/Automation.tracetemplate");
    if (!t.exists()) {
      t =
          new File(
              "/Developer/Platforms/iPhoneOS.platform/Developer/Library/Instruments/PlugIns/AutomationInstrument.bundle/Contents/Resources/Automation.tracetemplate");
    }
    if (!t.exists()) {
      throw new IOSAutomationSetupException("can't find template. Try instruments -s");
    }


    template = t;
    this.port = serverPort;
  }

  public void startSession(IOSDevice device, String sdkVersion, String locale, String language,
      File application, String sessionId, boolean timeHack, List<String> envtParams)
      throws IOSAutomationSetupException {
    try {
      this.sessionId = sessionId;
      this.extraEnvtParams = envtParams;

      output = createTmpOutputFolder();
      if (!application.exists() || !application.isDirectory()) {
        throw new IOSAutomationSetupException("Invalid app :" + application);
      }
      this.application = application;

      simulator = prepareSimulator(sdkVersion, device, locale, language);

      List<String> instruments = createInstrumentCommand();
      communicationChannel = new CommunicationChannel();

      simulatorProcess = new Command(instruments, true);
      simulatorProcess.setWorkingDirectory(output);
      simulatorProcess.start();

     
      boolean success = communicationChannel.waitForUIScriptToBeStarted();
      // appears only in ios6. : Automation Instrument ran into an exception while trying to run the
      // script. UIAScriptAgentSignaledException
      if (!success) {
        simulatorProcess.forceStop();
        killSimulator();
        throw new IOSAutomationSetupException("Instruments crashed.");
      }

      if (timeHack) {
        TimeSpeeder.getInstance().activate();
        TimeSpeeder.getInstance().start();
      } else {
        TimeSpeeder.getInstance().desactivate();
      }


    } catch (Exception e) {
      throw new IOSAutomationSetupException("error starting instrument for session " + sessionId, e);
    }

  }

  private IOSDeviceManager prepareSimulator(String sdkVersion, IOSDevice device, String locale,
      String language) throws IOSAutomationSetupException {
    // TODO freynaud handle real device ?
    IOSDeviceManager simulator = new IOSSimulatorManager(sdkVersion, device);
    simulator.resetContentAndSettings();
    simulator.setL10N(locale, language);
    return simulator;
  }

  public void stop() throws IOSAutomationSetupException {
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

  private List<String> createInstrumentCommand() throws IOSAutomationSetupException {
    List<String> command = new ArrayList<String>();
    command.add("instruments");
    command.add("-t");
    command.add(template.getAbsolutePath());
    command.add(application.getAbsolutePath());
    command.add("-e");
    command.add("UIASCRIPT");
    File uiscript = new ScriptHelper().getScript(port, application.getAbsolutePath(), sessionId);

    command.add(uiscript.getAbsolutePath());
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

  private void killSimulator() throws IOSAutomationSetupException {
    simulator.cleanupDevice();
  }


  public File getOutput() {
    return output;
  }

  public CommunicationChannel communicate() {
    return communicationChannel;
  }
}
