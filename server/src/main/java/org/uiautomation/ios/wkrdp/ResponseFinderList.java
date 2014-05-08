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

package org.uiautomation.ios.wkrdp;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * orchestrate all the ResponseFinders. Run all of them in parallel, return the first response found
 * after gracefully waiting for all of them to finish.
 */
// TODO freynaud fix that class.
public class ResponseFinderList {

  private static final Logger log = Logger.getLogger(ResponseFinderList.class.getName());
  private final List<ResponseFinder> finders;
  private final Lock lock = new ReentrantLock();
  private final Condition foundIt = lock.newCondition();
  private final List<Thread> threads = new ArrayList<Thread>();
  private final long timeoutInMs;

  public ResponseFinderList(List<ResponseFinder> finders, long timeoutInMs) {
    this.finders = finders;
    this.timeoutInMs = timeoutInMs;
  }

  public JSONObject findResponse(final int id) {
    final long start = System.currentTimeMillis();
    // start all the finders.
    for (final ResponseFinder finder : finders) {
      Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            finder.startSearch(id);
            if (log.isLoggable(Level.FINE))
              log.fine(
                "finder " + finder.getClass() + " found something - " + (System.currentTimeMillis()                                                                        - start) + "ms");
            try {
              lock.lock();
              foundIt.signal();
            } finally {
              lock.unlock();
            }
          } catch (InterruptedException e) {
            log.fine("search was interrupted.");
          } catch (Exception e){
            log.warning(e.getMessage());
          }

        }
      });
      threads.add(t);
      t.start();
    }

    // when for one to finish
    try {
      try {
        lock.lock();
        foundIt.await(timeoutInMs, TimeUnit.MILLISECONDS);
        if (log.isLoggable(Level.FINE))
          log.fine("await returns " + (System.currentTimeMillis() - start) + "ms");
      } finally {
        lock.unlock();
      }

    } catch (InterruptedException e) {
    }

    for (Thread t : threads) {
      t.interrupt();
    }
    // stop the others
    for (ResponseFinder finder : finders) {
      finder.interruptSearch();
    }
    if (log.isLoggable(Level.FINE))
      log.fine("all finders interrupted " + (System.currentTimeMillis() - start) + "ms");
    for (ResponseFinder finder : finders) {
      JSONObject response = finder.getResponse();
      if (response != null) {
        if (log.isLoggable(Level.FINE))
          log.fine("returns response  " + (System.currentTimeMillis() - start) + "ms");
        return response;
      }
    }

    throw new RuntimeException("bug.One of the finder should have got something.");
  }


}
