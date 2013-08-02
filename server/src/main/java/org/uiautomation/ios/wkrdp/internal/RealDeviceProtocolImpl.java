/*
 * Copyright 2012 ios-driver committers.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the Licence at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */

package org.uiautomation.ios.wkrdp.internal;

import org.libimobiledevice.binding.raw.IMobileDeviceFactory;
import org.libimobiledevice.binding.raw.IOSDevice;
import org.libimobiledevice.binding.raw.WebInspectorService;
import org.uiautomation.ios.wkrdp.MessageListener;
import org.uiautomation.ios.wkrdp.ResponseFinder;

/**
 * WKRDP implementation for real device using a USB connection.
 */
public class RealDeviceProtocolImpl extends WebKitRemoteDebugProtocol {

  private WebInspectorService inspector;

  public RealDeviceProtocolImpl(MessageListener listener,
                                ResponseFinder... finders) {
    super(listener, finders);
    String uuid = "d1ce6333af579e27d166349dc8a1989503ba5b4f";
    IOSDevice device = IMobileDeviceFactory.INSTANCE.get(uuid);
    inspector = new WebInspectorService(device);
    start();
  }

  @Override
  public void start() {
    inspector.startWebInspector();
    startListenerThread();
  }

  @Override
  public void stop() {
    stopListenerThread();
    inspector.stopWebInspector();
  }

  @Override
  protected void read() throws Exception {
    String msg = inspector.receiveMessage();
    if (msg != null) {
      handler.handle(msg);
    }
  }

  @Override
  protected void sendMessage(String message) {
    inspector.sendMessage(message);
  }
}
