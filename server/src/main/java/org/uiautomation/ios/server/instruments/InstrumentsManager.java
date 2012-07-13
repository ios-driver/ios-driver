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
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.command.impl.session.hack.TimeSpeeder;
import org.uiautomation.ios.server.simulator.IOSSimulatorManager;

public class InstrumentsManager {

  public static final String INSTRUMENTS_PROCESS_NAME = "/Developer/usr/bin/instruments";
  private File output;
  private final File template;
  private File application;
  private IOSDeviceManager simulator;
  private String sessionId;
  private List<String> extraEnvtParams;
  private CommunicationChannel communicationChannel;
  private Command simulatorProcess;


  public InstrumentsManager() throws IOSAutomationSetupException {
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
      simulatorProcess.start();

      communicationChannel.waitForUIScriptToBeStarted();

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
    //command.add("-w");
    //command.add("d1ce6333af579e27d166349dc8a1989503ba5b4f");
    command.add("-t");
    command.add(template.getAbsolutePath());
    command.add(application.getAbsolutePath());
    command.add("-e");
    command.add("UIASCRIPT");
    File uiscript = new ScriptHelper().getScript(IOSServer.port,application.getAbsolutePath());

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
