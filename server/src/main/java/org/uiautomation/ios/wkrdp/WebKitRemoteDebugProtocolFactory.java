/*
 * Copyright 2012-2014 eBay Software Foundation and ios-driver committers
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

package org.uiautomation.ios.wkrdp;

import org.uiautomation.ios.RealDevice;
import org.uiautomation.ios.drivers.RemoteIOSNativeDriver;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.command.configuration.Configuration;
import org.uiautomation.ios.drivers.RemoteIOSNativeDriver;
import org.uiautomation.ios.wkrdp.internal.RealDeviceProtocolImpl;
import org.uiautomation.ios.wkrdp.internal.SimulatorProtocolImpl;
import org.uiautomation.ios.wkrdp.internal.WebKitRemoteDebugProtocol;

public class WebKitRemoteDebugProtocolFactory {

  public static WebKitRemoteDebugProtocol create(ServerSideSession session,
                                                 RemoteIOSNativeDriver driver) {

    if (session != null && session.getDevice() instanceof RealDevice) {
      if (!Configuration.BETA_FEATURE) {
        Configuration.off();
      }
      String uuid = ((RealDevice) session.getDevice()).getUuid();
      return new RealDeviceProtocolImpl(uuid, session);
    } else {
      return new SimulatorProtocolImpl(session);
    }
  }
}