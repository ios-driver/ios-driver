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

package org.uiautomation.ios.server.instruments;

import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.server.command.UIAScriptRequest;
import org.uiautomation.ios.server.instruments.communication.CommunicationChannel;
import org.uiautomation.ios.server.services.Instruments;
import org.uiautomation.ios.server.services.TakeScreenshotService;
import org.uiautomation.ios.server.simulator.InstrumentsFailedToStartException;
import org.uiautomation.ios.utils.Command;

import java.util.List;

public class NoInstrumentsImplementationAvailable implements Instruments {

  private final IOSCapabilities caps;

  public NoInstrumentsImplementationAvailable(IOSCapabilities caps) {
    this.caps = caps;
  }

  @Override
  public void start(long timeOut) throws InstrumentsFailedToStartException {
      List<String> safariStartingScript = caps.getStartScript();
      if (safariStartingScript!=null){
        Command c = new Command(safariStartingScript,true);
        c.executeAndWait();
      }
  }

  @Override
  public void stop() {

  }

  @Override
  public Response executeCommand(UIAScriptRequest request) {
    return null;
  }

  @Override
  public CommunicationChannel getChannel() {
    return null;
  }

  @Override
  public TakeScreenshotService getScreenshotService() {
    return null;
  }
}
