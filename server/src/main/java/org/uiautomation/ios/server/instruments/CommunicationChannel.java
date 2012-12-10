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

import org.uiautomation.ios.server.command.UIAScriptRequest;
import org.uiautomation.ios.server.command.UIAScriptResponse;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CommunicationChannel {

  private final BlockingQueue<UIAScriptRequest> requestQueue =
      new ArrayBlockingQueue<UIAScriptRequest>(1);
  private final BlockingQueue<UIAScriptResponse> responseQueue =
      new ArrayBlockingQueue<UIAScriptResponse>(1);

  private final Lock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();
  private volatile boolean ready = false;

  public boolean waitForUIScriptToBeStarted() throws InterruptedException {
    try {
      lock.lock();
      if (ready) {
        return true;
      }
      return condition.await(60, TimeUnit.SECONDS);
    } finally {
      lock.unlock();
    }
  }

  public void registerUIAScript() {
    try {
      lock.lock();
      ready = true;
      condition.signal();
    } finally {
      lock.unlock();
    }
  }

  public void sendNextCommand(UIAScriptRequest r) {
    if (responseQueue.size() != 0) {
      System.err.println("adding command but reponse not read.");
    }
    requestQueue.add(r);
  }

  public UIAScriptRequest getNextCommand() throws InterruptedException {
    UIAScriptRequest res = requestQueue.take();
    return res;
  }

  public void setNextResponse(UIAScriptResponse r) {
    responseQueue.add(r);
  }

  public UIAScriptResponse waitForResponse() throws InterruptedException {
    UIAScriptResponse res = responseQueue.take();
    return res;
  }
}
