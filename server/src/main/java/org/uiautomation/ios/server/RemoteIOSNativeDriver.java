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

package org.uiautomation.ios.server;

import org.openqa.selenium.remote.SessionId;
import org.uiautomation.ios.server.instruments.communication.CommunicationChannel;
import org.uiautomation.ios.server.services.Instruments;
import org.uiautomation.ios.server.services.InstrumentsFactory;
import org.uiautomation.ios.server.services.TakeScreenshotService;
import org.uiautomation.ios.server.simulator.InstrumentsFailedToStartException;

import java.net.URL;

public class RemoteIOSNativeDriver extends ServerSideNativeDriver {

  private final Instruments instruments;
  private final Thread shutdownHook;

  public RemoteIOSNativeDriver(URL url, ServerSideSession session,Instruments impl) {
    super(url, new SessionId(session.getSessionId()));
    this.instruments = impl;
    shutdownHook = new Thread() {
      @Override
      public void run() {
        instruments.stop();
      }
    };
  }

  public void start(long timeOut) throws InstrumentsFailedToStartException {
    try {
      instruments.start(timeOut);
      Runtime.getRuntime().addShutdownHook(shutdownHook);
    } catch (InstrumentsFailedToStartException e) {
      stop();
      throw e;
    }
  }

  public void stop() {
    instruments.stop();
    Runtime.getRuntime().removeShutdownHook(shutdownHook);
  }

  public CommunicationChannel communication() {
    return instruments.getChannel();
  }

  public TakeScreenshotService getScreenshotService() {
    return instruments.getScreenshotService();
  }

  public Instruments getInstruments() {
    return instruments;
  }
}
