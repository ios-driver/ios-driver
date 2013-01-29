/*
 * Copyright 2012 ios-driver committers.
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
package org.uiautomation.ios.mobileSafari;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.json.JSONObject;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.mobileSafari.events.ChildNodeRemoved;
import org.uiautomation.ios.mobileSafari.events.Event;
import org.uiautomation.ios.mobileSafari.events.inserted.ChildIframeInserted;
import org.uiautomation.ios.mobileSafari.message.ApplicationDataMessage;
import org.uiautomation.ios.mobileSafari.message.IOSMessage;
import org.uiautomation.ios.mobileSafari.message.MessageFactory;
import org.uiautomation.ios.mobileSafari.remoteWebkitProtocol.MessageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;


public class DefaultMessageHandler implements MessageHandler {

  private final List<JSONObject> responses = new CopyOnWriteArrayList<JSONObject>();
  private final List<MessageListener> listeners = new CopyOnWriteArrayList<MessageListener>();
  private Thread t;
  private static boolean showIgnoredMessaged = true;
  private static final Logger log = Logger.getLogger(DefaultMessageHandler.class.getName());
  private List<ResponseFinder> extraFinders = new ArrayList<ResponseFinder>();
  private final MessageFactory factory = new MessageFactory();


  public DefaultMessageHandler(MessageListener listener, ResponseFinder... finders) {
    listeners.add(listener);
    for (ResponseFinder finder : finders) {
      this.extraFinders.add(finder);
    }
  }

  @Override
  public void handle(final String msg) {
    Thread t = new Thread(new Runnable() {

      @Override
      public void run() {
        process(msg);
      }
    });
    t.start();
  }

  private void process(String rawMessage) {
    IOSMessage message = factory.create(rawMessage);

    //System.out.println(message);
    for (MessageListener l : listeners) {
      l.onMessage(message);
    }

    if (message instanceof ApplicationDataMessage) {
      JSONObject content = ((ApplicationDataMessage) message).getMessage();
      //System.out.println("METHOD"+content.optString("method"));
      if ((content.optInt("id", -1) != -1)) {
        responses.add(content);
      }
    }

    /*if (listener instanceof MessageListener) {
      ((MessageListener) listener).onMessage(message);
    }

    if (message instanceof ApplicationDataMessage) {
      JSONObject content = ((ApplicationDataMessage) message).getMessage();
      if ((content.optInt("id", -1) != -1)) {
        responses.add(content);
        return;
        //not a command response.
        // should be an event.
      } else {
        Event e = EventFactory.createEvent(content);
        if (isPageLoad(content)) {
          listener.onPageLoad();
          return;
        }

        if (isImportantDOMChange(e)) {
          listener.domHasChanged(e);
        }

        if ("Page.frameDetached".equals(content.optString("method"))) {
          listener.frameDied(content);
          return;
        } else {
          if (showIgnoredMessaged) {
            System.err.println(System.currentTimeMillis() + "\t" + message.toString());
          }

        }
      }
    } else if (message instanceof ApplicationSentListingMessage) {
      ApplicationSentListingMessage listingMessage = (ApplicationSentListingMessage) message;
      listener.setWindowHandles(listingMessage.getPages());
    } else {
      //System.out.println(message);
    }*/

  }

  private boolean isImportantDOMChange(Event e) {

    return (e instanceof ChildIframeInserted || e instanceof ChildNodeRemoved);
  }

  private boolean isPageLoad(JSONObject message) {
    String method = message.optString("method");
    return "Page.loadEventFired".equals(method); //  || "Profiler.resetProfiles".equals(method);
    // return "Profiler.resetProfiles".equals(method) ||
    // "DOM.documentUpdated".equals(method);
  }


  private JSONObject extractResponse(String message) throws Exception {
    message = message.replace(
        "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">",
        "");
    SAXReader reader = new SAXReader();
    Document document = reader.read(IOUtils.toInputStream(message));
    Node n = document.selectSingleNode("/plist/dict/dict/data");
    if (n != null) {
      String encoded = n.getText();
      byte[] bytes = Base64.decodeBase64(encoded);
      String s = new String(bytes);
      JSONObject o = new JSONObject(s);
      return o;
    } else {
      return null;
    }
  }


  @Override
  public JSONObject getResponse(int id) throws TimeoutException {
    // there can be 2 things happening here.
    // 1) the response is received.
    // 2) the response is never received because there is an alert.

    // ResponseFinder.
    // startSearch
    // interruptSearch
    // waitForResult

    long start = System.currentTimeMillis();
    long timeout = 5 * 1000;
    final ResponseFinder defaultFinder = new DefaultResponseFinder(timeout);

    List<ResponseFinder> finders = new ArrayList<ResponseFinder>();
    finders.add(defaultFinder);
    finders.addAll(extraFinders);

    ResponseFinderList all = new ResponseFinderList(finders);
    try {
      JSONObject res = all.findResponse(id);
      log.fine(
          "response " + id + " , " + (System.currentTimeMillis() - start) + "ms. " + res
              .toString());

      return res;
    } catch (RuntimeException e) {
      System.out.println("Cannot find response for " + id);
      throw e;
    }
  }


  class DefaultResponseFinder implements ResponseFinder {

    private long start;
    private long end;
    private final long timeout;

    private volatile boolean ok = true;
    private WebDriverException exception;
    private JSONObject response;

    private DefaultResponseFinder(long timeout) {
      this.timeout = timeout;

    }

    private void reset() {
      start = System.currentTimeMillis();
      this.end = start + timeout;
      ok = true;
      exception = null;
      response = null;
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


  @Override
  public void stop() {
    if (t != null) {
      t.interrupt();
    }
  }

  @Override
  public void addListener(MessageListener listener) {
    listeners.add(listener);
  }

}
