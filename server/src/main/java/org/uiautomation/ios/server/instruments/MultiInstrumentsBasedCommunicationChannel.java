/*
 * Copyright 2012 ios-driver committers.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */

package org.uiautomation.ios.server.instruments;

import org.libimobiledevice.binding.raw.IMobileDeviceFactory;
import org.libimobiledevice.binding.raw.IOSDevice;
import org.libimobiledevice.binding.raw.instruments.Instruments;
import org.libimobiledevice.binding.raw.instruments.ScriptMessageHandler;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.server.RealDevice;
import org.uiautomation.ios.server.command.UIAScriptRequest;
import org.uiautomation.ios.server.command.UIAScriptResponse;
import org.uiautomation.ios.utils.ScriptHelper;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultiInstrumentsBasedCommunicationChannel
    implements CommunicationChannel, ScriptMessageHandler, Logger, InstrumentManager {

  private final Lock lock = new ReentrantLock();
  private volatile boolean ready = false;
  private final Condition condition = lock.newCondition();

  private final BlockingQueue<UIAScriptResponse> responseQueue =
      new ArrayBlockingQueue<UIAScriptResponse>(1);
  private final RealDevice device;
  private final Instruments instruments;
  // TODO freynaud remove that from memory.It's too big.
  private final String script;
  private final String bundleId = "com.yourcompany.UICatalog";
  private final String sessionId;

  public MultiInstrumentsBasedCommunicationChannel(RealDevice device, int port, String aut,
                                                   String sessionId)
      throws IOException {
    this.device = device;
    this.sessionId = sessionId;
    script = new ScriptHelper()
        .generateScriptContent(port, aut, sessionId, CommunicationMode.MULTI);
    final IOSDevice iphone = IMobileDeviceFactory.INSTANCE.get(((RealDevice) device).getUuid());
    iphone.connect();

    instruments = new Instruments(iphone, this);
  }


  // TODO freynaud abstract.
  @Override
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

  @Override
  public void registerUIAScript() {
    try {
      lock.lock();
      ready = true;
      condition.signal();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void sendNextCommand(UIAScriptRequest r) {
    try {
      String
          templ =
          "UIATarget.localTarget().frontMostApp().setPreferencesValueForKey( '%s', 'cmd');";

      String escaped = r.getScript().replace("'","\"");
      String script = String.format(templ, escaped);
      instruments.executeScriptNonManaged(script);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public UIAScriptRequest getNextCommand() throws InterruptedException {
    throw new RuntimeException("NI");
  }

  @Override
  public void setNextResponse(UIAScriptResponse r) {
    responseQueue.add(r);
  }

  @Override
  public UIAScriptResponse waitForResponse() throws InterruptedException {
    UIAScriptResponse
        res =
        responseQueue.poll(IOSCapabilities.COMMAND_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    return res;
  }

  @Override
  public void handle(String message) {
    System.out.println("MESSAGE : " + message);
    if (message.startsWith("IOS_DRIVER_RESPONSE:")) {
      String raw = message.replace("IOS_DRIVER_RESPONSE:", "");
      UIAScriptResponse response = new UIAScriptResponse(raw);
      if (response.isFirstResponse()) {
        registerUIAScript();
      }
      setNextResponse(response);
    } else {
      log(message);
    }
  }

  @Override
  public void log(String message) {
    System.out.println(message);
  }

  @Override
  public void start() {
    instruments.startApp(bundleId);
    System.out.println("started app");
    instruments.executeScriptNonManaged(script);
    System.out.println("started script ");
    try {
      waitForUIScriptToBeStarted();
      System.out.println("script said hello");
    } catch (InterruptedException e) {
      throw new WebDriverException("Error starting script " + e.getMessage(), e);
    }
  }

  @Override
  public void stop() {
    instruments.stopApp();
  }
}
