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
import java.util.List;
import java.util.UUID;

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.communication.IOSDevice;
import org.uiautomation.ios.exceptions.IOSAutomationSetupException;
import org.uiautomation.ios.server.IOSServerConfiguration;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.application.LanguageDictionary;
import org.uiautomation.ios.server.application.Localizable;
import org.uiautomation.ios.server.application.ResourceCache;


public class SessionsManager {

  private Session currentSession;
  private IOSApplication currentApplication;
  private IOSServerConfiguration config;
  private ResourceCache cache;



  private InstrumentsManager instrumentsManager;

  public SessionsManager(IOSServerConfiguration config) throws IOSAutomationSetupException {
    instrumentsManager = new InstrumentsManager();
    this.config = config;
    this.cache = new ResourceCache();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        forceStop();
      }
    });
  }

  
  public ResourceCache getResourceCache(){
    return cache;
  }

  public void createSession(IOSCapabilities cap) throws IOSAutomationSetupException {
    IOSCapabilities v = completeWithDefaults(cap);

    Localizable language = new LanguageDictionary(v.getLanguage()).getLanguage();
    String aut = v.getApplication();
    currentApplication = new IOSApplication(language, aut);
    currentApplication.loadAllContent();

    createSession(v.getDevice(), v.getSDKVersion(), v.getLocale(), v.getLanguage(),
        new File(v.getApplication()), v.isTimeHack(), v.getExtraSwitches());
  }


  public void createSession(IOSDevice device, String sdkVersion, String locale, String language,
      File application, boolean timeHack, List<String> envtParams)
      throws IOSAutomationSetupException {
    if (currentSession != null) {
      throw new IOSAutomationSetupException("session already assigned");
    }
    String sessionId = UUID.randomUUID().toString();
    instrumentsManager.startSession(device, sdkVersion, locale, language, application, sessionId,
        timeHack, envtParams);
    currentSession = new Session(sessionId);
  }

  private IOSCapabilities completeWithDefaults(IOSCapabilities cap)
      throws IOSAutomationSetupException {
    if (cap.getDevice() == null) {
      cap.setDevice(IOSDevice.getDefault());
    }
    if (cap.getSDKVersion() == null) {
      List<String> sdks = ClassicCommands.getInstalledSDKs();
      String sdk = sdks.get(sdks.size() - 1);
      cap.setSDKVersion(sdk);
    }

    if (cap.getLocale() == null) {
      cap.setLocale("en_GB");
    }
    if (cap.getLanguage() == null) {
      cap.setLanguage("en");
    }
    return cap;

  }

  public InstrumentsManager getInstrumentManager() {
    return instrumentsManager;
  }

  public File getCurrentSessionOutputFolder() {
    return instrumentsManager.getOutput();
  }

  public String getCurrentSessionId() {
    if (currentSession == null) {
      return null;
    } else {
      return currentSession.getSessionId();
    }
  }

  public void deleteSession() throws IOSAutomationSetupException {
    instrumentsManager.stop();
    currentSession = null;
  }

  public IOSApplication getCurrentApplication() {
    return currentApplication;
  }

  public void forceStop() {
    instrumentsManager.forceStop();
    currentSession = null;
  }


  public IOSServerConfiguration getServerConfig() {
    return config;
  }
}
