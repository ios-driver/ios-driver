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
import org.uiautomation.ios.server.application.IPAApplication;
import org.uiautomation.ios.server.instruments.communication.CommunicationChannel;
import org.uiautomation.ios.server.instruments.communication.multi.MultiInstrumentsBasedCommunicationChannel;
import org.uiautomation.ios.server.simulator.IOSRealDeviceManager;
import org.uiautomation.ios.server.simulator.IOSSimulatorManager;
import org.uiautomation.ios.server.simulator.Instruments;
import org.uiautomation.ios.server.simulator.InstrumentsApple;
import org.uiautomation.ios.server.simulator.InstrumentsFailedToStartException;
import org.uiautomation.ios.server.simulator.InstrumentsLibImobile;
import org.uiautomation.ios.utils.ClassicCommands;
import org.uiautomation.ios.utils.Command;
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
  private Command simulatorProcess;


  private Instruments instruments;

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
    language = caps.getLanguage();
    //language = application.getAppleLocaleFromLanguageCode(caps.getLanguage())
    //    .getAppleLanguagesForPreferencePlist();
    boolean timeHack = caps.isTimeHack();
    List<String> envtParams = caps.getExtraSwitches();

    log.fine("starting session");

    try {
      this.sessionId = sessionId;
      this.extraEnvtParams = envtParams;



      this.application = application;
      this.application.setDefaultDevice(deviceType);

      if (device instanceof RealDevice) {
        RealDevice d = (RealDevice) device;
        instruments =
            new InstrumentsLibImobile(d.getUuid(), port, application.getDotAppAbsolutePath(),
                                      sessionId, application.getBundleId());
        deviceManager = new IOSRealDeviceManager(capabilities,
                                                 (RealDevice) device,
                                                 (IPAApplication) application
                                                     .getUnderlyingApplication());

      } else {
        instruments =
            new InstrumentsApple(null, port, sessionId,
                                 new File(application.getDotAppAbsolutePath()), envtParams,
                                 sdkVersion);

        deviceManager = new IOSSimulatorManager(capabilities, instruments);

      }
      deviceManager.setSDKVersion();
      deviceManager.install(application.getUnderlyingApplication());
      deviceManager.resetContentAndSettings();
      deviceManager.setL10N(locale, language);
      deviceManager.setKeyboardOptions();
      deviceManager.setVariation(deviceType, variation);
      deviceManager.setLocationPreference(true);
      deviceManager.setMobileSafariOptions();

      try {
        instruments.start();
      } catch (InstrumentsFailedToStartException e) {
        instruments.stop();
      }
    } catch (Exception e) {
      throw new WebDriverException("error", e);
    }
  }




  public void stop() {
    if (device != null) {
      device.release();
    }
    TimeSpeeder.getInstance().stop();
    if (simulatorProcess != null) {
      simulatorProcess.waitFor(60 * 1000);
    }
    killSimulator();

  }

  public void forceStop() {
    if (device != null) {
      device.release();
    }
    TimeSpeeder.getInstance().stop();
    if (simulatorProcess != null) {
      simulatorProcess.forceStop();
    }
    killSimulator();
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
    return instruments.getChannel();
  }
}
