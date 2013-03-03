/*
 * Copyright 2013 ios-driver committers.
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

import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.wkrdp.RemoteIOSWebDriver;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WebKitSyncronizer {

  private Lock simLock = new ReentrantLock();
  private Condition simRegistered = simLock.newCondition();
  private Condition simSentApps = simLock.newCondition();
  private Condition simSentPages = simLock.newCondition();
  private final RemoteIOSWebDriver driver;

  public WebKitSyncronizer(RemoteIOSWebDriver driver) {
    this.driver = driver;
  }

  public void waitForSimToSendPages() {
    try {
      simLock.lock();
      if (driver.getPages() != null && driver.getPages().size() > 0) {
        return;
      }
      simSentPages.await(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new WebDriverException("InterruptedException before getting the pages.");
    } finally {
      simLock.unlock();
    }
  }

  public void waitForSimToRegister() {
    try {
      simLock.lock();
      if (driver.getDevice() != null) {
        return;
      }
      simRegistered.await(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new WebDriverException(
          "InterruptedException while waiting for the simulator to respond.");
    } finally {
      simLock.unlock();
    }
  }

  public void signalSimSentPages() {
    try {
      simLock.lock();
      simSentPages.signal();
    } finally {
      simLock.unlock();
    }
  }

  public void signalSimRegistered() {
    try {
      simLock.lock();
      simRegistered.signal();
    } finally {
      simLock.unlock();
    }
  }

  public void waitForSimToSendApps() {

    try {
      simLock.lock();
      if (driver.getApplications() != null && !driver.getApplications().isEmpty()) {
        return;
      }
      simSentApps.await(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new WebDriverException(
          "InterruptedException while waiting for the simulator to send its apps.");
    } finally {
      simLock.unlock();
    }
  }

  public void signalSimSentApps() {
    try {
      simLock.lock();
      simSentApps.signal();
    } finally {
      simLock.unlock();
    }
  }
}
