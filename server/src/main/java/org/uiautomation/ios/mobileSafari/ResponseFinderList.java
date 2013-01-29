package org.uiautomation.ios.mobileSafari;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;


// TODO freynaud fix that class.
public class ResponseFinderList {

  private static final Logger log = Logger.getLogger(ResponseFinderList.class.getName());
  private final List<ResponseFinder> finders;
  private final Lock lock = new ReentrantLock();
  private final Condition foundIt = lock.newCondition();
  private final List<Thread> threads = new ArrayList<Thread>();

  public ResponseFinderList(List<ResponseFinder> finders) {
    this.finders = finders;
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
            log.fine(
                "finder " + finder.getClass() + " found something - " + (System.currentTimeMillis()
                                                                         - start) + "ms");
            try {
              lock.lock();
              foundIt.signal();
            } finally {
              lock.unlock();
            }
          } catch (InterruptedException e) {
            log.fine("search was interrupted.");
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
        foundIt.await();
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
    log.fine("all finders interrupted " + (System.currentTimeMillis() - start) + "ms");
    for (ResponseFinder finder : finders) {
      JSONObject response = finder.getResponse();
      if (response != null) {
        log.fine("returns response  " + (System.currentTimeMillis() - start) + "ms");
        return response;
      }
    }

    throw new RuntimeException("bug.One of the finder should have got something.");
  }


}
