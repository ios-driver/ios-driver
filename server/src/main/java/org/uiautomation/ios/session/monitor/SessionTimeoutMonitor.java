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

package org.uiautomation.ios.session.monitor;


import org.uiautomation.ios.ServerSideSession;

import java.util.logging.Logger;

public class SessionTimeoutMonitor implements ServerSideSessionMonitor {

  private final ServerSideSession session;
  private boolean shouldRun = true;
  private Thread loop;
  private static final Logger log = Logger.getLogger(MaxTimeBetween2CommandsMonitor.class.getName());

  public SessionTimeoutMonitor(ServerSideSession session) {
    this.session = session;
  }

  @Override
  public void startMonitoring() {
    synchronized (session) {
      shouldRun = true;
    }

    loop = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          runLoop();
        } catch (InterruptedException ignore) {
          //ignore.printStackTrace();
        }

      }
    });
    loop.start();

  }

  private void runLoop() throws InterruptedException {
    while (shouldRun()) {
      Thread.sleep(100);
      if (session.hasTimedOut()) {
        log.warning("Session has timed out.");
        session.stop(ServerSideSession.StopCause.sessionTimeout);
      }
    }
  }

  private boolean shouldRun() {
    synchronized (session) {
      return shouldRun;
    }
  }

  @Override
  public void stopMonitoring() {
    synchronized (session) {
      shouldRun = false;
    }
    loop.interrupt();
  }
}