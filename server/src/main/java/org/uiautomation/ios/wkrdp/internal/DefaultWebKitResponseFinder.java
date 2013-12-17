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

import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.wkrdp.ResponseFinder;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Default response finder for webkit. Waits up to 5 seconds and gives up. No detection of alerts
 * for instance.
 */
public class DefaultWebKitResponseFinder implements ResponseFinder {

  private static final Logger log = Logger.getLogger(DefaultMessageHandler.class.getName());
  private final List<JSONObject> responses = new CopyOnWriteArrayList<JSONObject>();
  private long start;
  private long end;
  private final long timeout;

  private volatile boolean ok = true;
  private WebDriverException exception;
  private JSONObject response;

  public DefaultWebKitResponseFinder(long timeout) {
    this.timeout = timeout;
  }

  private void reset() {
    start = System.currentTimeMillis();
    this.end = start + timeout;
    ok = true;
    exception = null;
    response = null;
  }

  public void addResponse(JSONObject response) {
    responses.add(response);
  }

  @Override
  public void startSearch(int id) {
    reset();
    long start = System.currentTimeMillis();
    log.fine("begin search");
    while (ok) {
      synchronized (this) {
        if (System.currentTimeMillis() > end) {
          exception =
              new WebDriverException("timeout (" + timeout + "ms) waiting for a response for request id: " + id);
          return;
        }
        try {
          Thread.sleep(10);
          for (JSONObject o : responses) {
            if (o.optInt("id") == id) {
              responses.remove(o);
              response = o;
              if (log.isLoggable(Level.FINE))
                log.fine("found a response " + (System.currentTimeMillis() - start) + "ms.");
              return;
            }
          }
        } catch (InterruptedException e) {
          // ignore.
        }
      }

    }
  }

  @Override
  public synchronized void interruptSearch() {
    ok = false;
  }

  @Override
  public JSONObject getResponse() {
    if (response != null) {
      return response;
    }
    if (exception != null) {
      throw exception;
    }
    return null;
  }

}

