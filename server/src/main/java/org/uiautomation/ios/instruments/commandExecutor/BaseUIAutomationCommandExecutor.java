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

package org.uiautomation.ios.instruments.commandExecutor;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.logging.LogLevelMapping;
import org.openqa.selenium.remote.BeanToJsonConverter;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.command.UIAScriptRequest;
import org.uiautomation.ios.command.UIAScriptResponse;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.uiautomation.ios.IOSCapabilities.COMMAND_TIMEOUT_MILLIS;

/**
 * base class for commandExecutor with an Instruments process. Assumes asyncronous commandExecutor.
 */
public abstract class BaseUIAutomationCommandExecutor implements UIAutomationCommandExecutor {

  private static final Logger
      log =
      Logger.getLogger(BaseUIAutomationCommandExecutor.class.getName());

  protected final BlockingQueue<UIAScriptResponse> responseQueue = new ArrayBlockingQueue<>(1);

  private final Lock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();
  private final String STOP_RESPONSE = "exit from instruments loop ok";
  private final String
      UNKNOWN_REASON =
      "The UI Automation Command executor stopped for an unknown reason";
  private volatile boolean ready = false;
  private final ServerSideSession session;
  
  // Holds the running state of BaseUIAutomationCommandExecutor
  private boolean stopped;

  public BaseUIAutomationCommandExecutor(ServerSideSession session) {
    this.session = session;
    
    // Initial state 
    this.stopped = false;
  }

  protected final String getSessionId() {
    return session.getSessionId();
  }

  @Override
  public final boolean waitForUIScriptToBeStarted(long timeOut) throws InterruptedException {
    try {
      lock.lock();
      if (ready) {
        return true;
      }
      
      // Check for stop called externally while registration is still
      // in progress
      boolean success = (condition.await(timeOut, TimeUnit.SECONDS) && !stopped);
      if (log.isLoggable(Level.FINE)) {
        log.fine("UIAutomationCommandExecutor is stopped? = " + stopped);
      }
      return success;
    } finally {
      lock.unlock();
    }
  }

  public final void stop() {
    try {
      lock.lock();
      ready = false;
      
      // Change the state to stopped and notify UIScriptRegistration
      // process if its in wait mode
      if (!stopped) {
        stopped = true;
      }
      condition.signal();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public final void registerUIAScript() {
    try {
      lock.lock();
      ready = true;
      condition.signal();
    } finally {
      lock.unlock();
    }
  }

  protected boolean isReady() {
    try {
      lock.lock();
      return ready;
    } finally {
      lock.unlock();
    }
  }


  protected final void handleLastCommand(UIAScriptRequest request) {
    // Stop is a fire and forget response. It will kill the instruments script,
    // so the script cannot
    // send a response.
    if ("stop".equals(request.getScript())) {
      Response response = new Response();
      response.setSessionId(getSessionId());
      response.setStatus(0);
      response.setValue(STOP_RESPONSE);
      BeanToJsonConverter converter = new BeanToJsonConverter();
      String json = converter.convert(response);
      UIAScriptResponse r = new UIAScriptResponse(json);
      setNextResponse(r);
    }
  }

  public final void setNextResponse(UIAScriptResponse r) {
    try {
      responseQueue.add(r);
    } catch (IllegalStateException e) {
      try {
        log.warning("ALREADY PRESENT:" + responseQueue.take().getRaw());
        log.warning("TRY TO ADD:" + r.getRaw());
      } catch (InterruptedException e1) {
        log.log(Level.SEVERE, "error", e1);
      }
      log.log(Level.SEVERE, "error", e);
    }
  }


  protected final UIAScriptResponse waitForResponse() {
    UIAScriptResponse res = null;
    long deadline = System.currentTimeMillis() + COMMAND_TIMEOUT_MILLIS;

    while (System.currentTimeMillis() < deadline) {
      try {
        res = responseQueue.poll(1000, TimeUnit.MILLISECONDS);
      } catch (InterruptedException ignore) {
      }
      // the executor is now stopped. Not need to wait any further
      if (!isReady()) {
        Response r = new Response();
        r.setStatus(13);
        r.setSessionId(getSessionId());
        if (session.getStopCause() != null) {
          r.setValue(session.getStopCause().name());
        } else {
          r.setValue(UNKNOWN_REASON);
        }
        UIAScriptResponse response = new UIAScriptResponse(new BeanToJsonConverter().convert(r));
        return response;
      }

      // we have a "valid" response
      if (res != null) {
        if (STOP_RESPONSE.equals(res.getResponse().getValue())) {
          stop();
          throw new InstrumentsHasStoppedException(session.getStopCause().name());
        }
        return res;
      }

    }
    throw new WebDriverException("timeout getting the response");
  }

  public static class InstrumentsHasStoppedException extends WebDriverException {

    public InstrumentsHasStoppedException(String message) {
      super(message);
    }
  }
}
