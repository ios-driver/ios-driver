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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.openqa.selenium.*;
import org.uiautomation.ios.UIAModels.UIAAlert;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAAlert;
import org.uiautomation.ios.wkrdp.ResponseFinder;


public class AlertDetector implements ResponseFinder {

  private static final Logger log = Logger.getLogger(AlertDetector.class.getName());
  private volatile WebDriverException ex;
  private volatile boolean finished = false;
  private volatile boolean stopRequested = false;
  private final long timeBeforeLookingForAlert = 750;
  private final RemoteIOSDriver driver;
  private volatile RemoteUIAAlert alert;

  public AlertDetector(RemoteIOSDriver driver) {
    this.driver = driver;
  }

  private void reset() {
    finished = false;
    stopRequested = false;
    ex = null;
    alert = null;
  }

  @Override
  public synchronized void startSearch(int id) throws InterruptedException {
    reset();

    try {
      Thread.sleep(timeBeforeLookingForAlert);
    } catch (InterruptedException ignore) {
      setFinished(true);
      return;
    }

    try {
      while (!stopRequested) {
        try {
          log.fine("starting to look for an alert.");
          //driver.switchTo().window(WorkingMode.Native.toString());
          alert = driver.findElement(new TypeCriteria(UIAAlert.class));
          //driver.switchTo().window(WorkingMode.Web.toString());
          String alertDetails = alert.logElementTree(null, false).toString(2);
          log.fine("found an alert." + alertDetails);
          ex = new UnhandledAlertException("alert present", alertDetails);
          break;
        } catch (NoSuchElementException ex) {
          log.fine("there was no alert.");
        } catch (NoAlertPresentException ex) {
          log.fine("there was no alert.");
        }
      }
    } catch (Exception e) {
    } finally {
      setFinished(true);
    }
  }

  private synchronized void setFinished(boolean value) {
    this.finished = value;
  }

  private synchronized boolean getFinished() {
    return this.finished;
  }

  @Override
  public void interruptSearch() {
    long start = System.currentTimeMillis();
    stopRequested = true;
    while (!getFinished()) {
      try {
        Thread.sleep(20);
      } catch (InterruptedException e) {
        // ignore.
      }

    }
    if (log.isLoggable(Level.FINE))
      log.fine("interrupted done " + (System.currentTimeMillis() - start) + "ms");
  }

  @Override
  public JSONObject getResponse() {
    if (!finished) {
      throw new RuntimeException("Bug");
    }
    if (ex != null) {
      throw ex;
    }
    return null;
  }
}


