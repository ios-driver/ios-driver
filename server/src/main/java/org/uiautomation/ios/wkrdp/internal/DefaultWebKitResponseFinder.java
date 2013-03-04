package org.uiautomation.ios.wkrdp.internal;

import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.wkrdp.ResponseFinder;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
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
              new WebDriverException("timeout waiting for a response for request id : " + id);
          return;
        }
        try {
          Thread.sleep(10);
          for (JSONObject o : responses) {
            if (o.optInt("id") == id) {
              responses.remove(o);
              response = o;
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

