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

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.server.command.UIAScriptRequest;
import org.uiautomation.ios.server.command.UIAScriptResponse;
import org.uiautomation.ios.server.instruments.communication.CommunicationChannel;
import org.uiautomation.ios.server.instruments.communication.curl.CURLBasedCommunicationChannel;
import org.uiautomation.ios.utils.ApplicationCrashListener;
import org.uiautomation.ios.utils.ClassicCommands;
import org.uiautomation.ios.utils.Command;
import org.uiautomation.ios.utils.ScriptHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.uiautomation.ios.server.instruments.communication.CommunicationMode.CURL;

public class InstrumentsApple implements Instruments {

  private static final Logger log = Logger.getLogger(InstrumentsApple.class.getName());

  private final String uuid;
  private final File template;
  private final File application;
  private final File output;
  private final String sessionId;
  private final List<String> envtParams;
  private final Command instruments;
  private final CURLBasedCommunicationChannel channel;
  private final String desiredSDKVersion;



  public InstrumentsApple(String uuid, int port, String sessionId, File application,
                          List<String> envtParams, String desiredSDKVersion,ApplicationCrashListener list) {
    this.uuid = uuid;
    this.sessionId = sessionId;
    this.desiredSDKVersion = desiredSDKVersion;
    this.application = application;
    this.envtParams = envtParams;
    template = ClassicCommands.getAutomationTemplate();

    String appPath = application.getAbsolutePath();
    File scriptPath = new ScriptHelper().getScript(port, appPath, sessionId, CURL);
    output = createTmpOutputFolder();

    instruments = new Command(createInstrumentCommand(scriptPath), true);
    instruments.registerListener(list);
    instruments.setWorkingDirectory(output);

    channel = new CURLBasedCommunicationChannel(sessionId);
  }

  public void start() throws InstrumentsFailedToStartException {

    instruments.start();

    log.fine("waiting for registration request");
    boolean success = false;
    try {
      success = channel.waitForUIScriptToBeStarted();
    } catch (InterruptedException e) {
      throw new InstrumentsFailedToStartException("instruments was interrupted while starting.");
    } finally {
      // appears only in ios6. : Automation Instrument ran into an exception
      // while trying to run the
      // script. UIAScriptAgentSignaledException
      if (!success) {
        instruments.forceStop();
        throw new InstrumentsFailedToStartException("Instruments crashed.");
      }
    }
  }

  @Override
  public void stop() {
    instruments.forceStop();
    channel.stop();
  }


  public void startWithDummyScript() {
    File script = new ScriptHelper().createTmpScript("UIALogger.logMessage('warming up');");
    List<String> cmd = createInstrumentCommand(script);
    Command c = new Command(cmd, true);
    c.executeAndWait();
  }

  private List<String> createInstrumentCommand(File script) {
    List<String> command = new ArrayList<String>();

    command.add(getInstrumentsClient());
    if (uuid != null) {
      command.add("-w");
      command.add(uuid);
    }
    command.add("-t");
    command.add(template.getAbsolutePath());
    command.add(application.getAbsolutePath());
    command.add("-e");
    command.add("UIASCRIPT");
    command.add(script.getAbsolutePath());
    command.add("-e");
    command.add("UIARESULTSPATH");
    command.add(output.getAbsolutePath());
    command.addAll(envtParams);
    StringBuilder b = new StringBuilder();
    for (String s : command) {
      b.append(s);
      b.append(" ");
    }
    log.fine("Starting instruments:\n" + b.toString());
    return command;

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
    return InstrumentsNoDelayLoader.getInstance().getInstruments().getAbsolutePath();
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

  public File getOuput() {
    return output;
  }
}
