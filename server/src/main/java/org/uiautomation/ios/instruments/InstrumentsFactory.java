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

package org.uiautomation.ios.instruments;

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.Device;
import org.uiautomation.ios.RealDevice;
import org.uiautomation.ios.ServerSideSession;

import java.util.List;

public class InstrumentsFactory {


  public static Instruments getInstruments(ServerSideSession session) {
    IOSCapabilities caps = session.getCapabilities();
    Device device = session.getDevice();

    List<String> envtParams = caps.getExtraSwitches();

    if (device instanceof RealDevice) {
      RealDevice d = (RealDevice) device;
      if ("com.apple.mobilesafari".equals(session.getCapabilities().getBundleId())) {
        return new NoInstrumentsImplementationAvailable(session);
      } else {
        return new InstrumentsCommandLine(d.getUuid(),
                                    session.getIOSServerManager().getHostInfo()
                                        .getInstrumentsVersion(),
                                    session.getOptions().getPort(),
                                    session.getSessionId(),
                                    session.getApplication(),
                                    envtParams,
                                    caps,
                                    session
        );
      }
//      return null;
//          new InstrumentsLibImobile(d.getUuid(),
//                                    session.getOptions().getPort(),
//                                    session.getApplication().getDotAppAbsolutePath(),
//                                    session.getSessionId(),
//                                    session.getApplication().getBundleId());

    } else {

      return new InstrumentsCommandLine(null,
                                  session.getIOSServerManager().getHostInfo()
                                      .getInstrumentsVersion(),
                                  session.getOptions().getPort(),
                                  session.getSessionId(),
                                  session.getApplication(),
                                  envtParams,
                                  caps,
                                  session
      );


    }
  }


}
