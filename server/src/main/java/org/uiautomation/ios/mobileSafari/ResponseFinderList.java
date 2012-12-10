package org.uiautomation.ios.mobileSafari;


import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class ResponseFinderList {

  private static final Logger log = Logger.getLogger(ResponseFinderList.class.getName());
  private final List<ResponseFinder> finders;
  private final Lock lock = new ReentrantLock();
  private final Condition foundIt = lock.newCondition();

  public ResponseFinderList(List<ResponseFinder> finders) {
    this.finders = finders;
  }

  public JSONObject findResponse(final int id) {
    log.fine("using " + finders.size() + "finders");
    // start all the finders.
    for (final ResponseFinder finder : finders) {
      Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          finder.startSearch(id);
          log.fine("finder " + finder.getClass() + " found something.");
          try {
            lock.lock();
            foundIt.signal();
          } finally {
            lock.unlock();
          }
        }
      });
      t.start();
    }

    // when for one to finish
    try {
      try {
        lock.lock();
        foundIt.await();
      } finally {
        lock.unlock();
      }

    } catch (InterruptedException e) {
    }
    // stop the others
    for (ResponseFinder finder : finders) {
      finder.interruptSearch();
    }

    for (ResponseFinder finder : finders) {
      JSONObject response = finder.getResponse();
      if (response != null) {
        return response;
      }
    }
    throw new RuntimeException("bug.");
  }


}
