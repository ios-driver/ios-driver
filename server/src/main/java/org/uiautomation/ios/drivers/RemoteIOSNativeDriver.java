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

package org.uiautomation.ios.drivers;

import org.openqa.selenium.remote.SessionId;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.instruments.ApplicationCrashedOnStartException;
import org.uiautomation.ios.instruments.commandExecutor.UIAutomationCommandExecutor;
import org.uiautomation.ios.instruments.Instruments;
import org.uiautomation.ios.instruments.TakeScreenshotService;
import org.uiautomation.ios.instruments.InstrumentsFailedToStartException;

import java.net.URL;
import java.util.logging.Logger;

public class RemoteIOSNativeDriver extends ServerSideNativeDriver {

  private final Instruments instruments;
  private static final Logger log = Logger.getLogger(RemoteIOSNativeDriver.class.getName());

  public RemoteIOSNativeDriver(URL url, ServerSideSession session,Instruments impl) {
    super(url, new SessionId(session.getSessionId()));
    this.instruments = impl;
  }

  public void start(long timeOut) throws InstrumentsFailedToStartException, ApplicationCrashedOnStartException {
    try {
      instruments.start(timeOut);
    } catch (InstrumentsFailedToStartException|ApplicationCrashedOnStartException e) {
      stop();
      throw e;
    }
  }

  public void stop() {
    instruments.stop();
  }

  public UIAutomationCommandExecutor communication() {
    return instruments.getChannel();
  }

  public TakeScreenshotService getScreenshotService() {
    return instruments.getScreenshotService();
  }

  public Instruments getInstruments() {
    return instruments;
  }
}
