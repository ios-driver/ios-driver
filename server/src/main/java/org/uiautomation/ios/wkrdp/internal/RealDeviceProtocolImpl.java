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

package org.uiautomation.ios.wkrdp.internal;

import org.libimobiledevice.ios.driver.binding.exceptions.SDKException;
import org.libimobiledevice.ios.driver.binding.services.DeviceService;
import org.libimobiledevice.ios.driver.binding.services.IOSDevice;
import org.libimobiledevice.ios.driver.binding.services.WebInspectorService;
import org.uiautomation.ios.wkrdp.ResponseFinder;

import java.util.List;

/**
 * WKRDP implementation for real device using a USB connection.
 */
public class RealDeviceProtocolImpl extends WebKitRemoteDebugProtocol {

  private final WebInspectorService inspector;

  public RealDeviceProtocolImpl(String uuid, List<ResponseFinder> finders) {
    super(finders);
    IOSDevice device = null;
    try {
      device = DeviceService.get(uuid);
    } catch (SDKException e) {
      e.printStackTrace();
    }
    inspector = new WebInspectorService(device);
  }

  @Override
  public void start() {
    try {
      inspector.startWebInspector();
    } catch (SDKException e) {
      e.printStackTrace();
    }
    startListenerThread();
  }

  @Override
  public void stop() {
    stopListenerThread();
    try {
      inspector.stopWebInspector();
    } catch (SDKException e) {
      e.printStackTrace();
    }
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
    try {
      inspector.sendMessage(message);
    } catch (SDKException e) {
      e.printStackTrace();
    }
  }
}
