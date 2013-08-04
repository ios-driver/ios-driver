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
package org.uiautomation.ios.server.instruments.communication.curl;

import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.server.command.UIAScriptRequest;
import org.uiautomation.ios.server.command.UIAScriptResponse;
import org.uiautomation.ios.server.instruments.communication.BaseCommunicationChannel;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class CURLBasedCommunicationChannel extends BaseCommunicationChannel {

  private final BlockingQueue<UIAScriptRequest> requestQueue =
      new ArrayBlockingQueue<UIAScriptRequest>(1);

  public CURLBasedCommunicationChannel(String sessionId) {
    super(sessionId);
  }


  public UIAScriptResponse executeCommand(UIAScriptRequest request) {
    handleLastCommand(request);
    requestQueue.add(request);
    return waitForResponse();
  }

  public void addResponse(UIAScriptResponse r) {
    setNextResponse(r);
  }


  public UIAScriptRequest getNextCommand() throws InterruptedException {
    UIAScriptRequest res = requestQueue.take();
    return res;
  }


}
