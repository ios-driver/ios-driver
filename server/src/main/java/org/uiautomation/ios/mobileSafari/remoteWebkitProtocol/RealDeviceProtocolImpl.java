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

package org.uiautomation.ios.mobileSafari.remoteWebkitProtocol;

import org.uiautomation.ios.mobileSafari.ResponseFinder;
import org.uiautomation.iosdriver.services.WebInspectorService;

public class RealDeviceProtocolImpl extends WebInspector2 {

  private WebInspectorService inspector;

  public RealDeviceProtocolImpl(DefaultMessageListener listener,
                                ResponseFinder... finders) {
    super(listener, finders);
    inspector = new WebInspectorService("d1ce6333af579e27d166349dc8a1989503ba5b4f");
    start();
  }

  @Override
  public void start() {
    inspector.start();
    startListenerThread();
  }

  @Override
  public void stop() {
    stopListenerThread();
    inspector.stop();
  }

  @Override
  protected void read() throws Exception {
    //System.out.println("about to receive");
    String msg = inspector.receiveMessage();
    //System.out.println("received");
    if (msg != null) {
      handler.handle(msg);
    }
  }

  @Override
  protected void sendMessage(String message) {
    inspector.sendMessage(message);
  }
}
