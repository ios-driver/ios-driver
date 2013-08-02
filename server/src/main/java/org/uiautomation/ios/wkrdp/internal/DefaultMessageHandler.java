/*
 * Copyright 2013 ios-driver committers.
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
import org.openqa.selenium.TimeoutException;
import org.uiautomation.ios.context.WebInspector;
import org.uiautomation.ios.wkrdp.ConnectListener;
import org.uiautomation.ios.wkrdp.MessageHandler;
import org.uiautomation.ios.wkrdp.MessageListener;
import org.uiautomation.ios.wkrdp.ResponseFinder;
import org.uiautomation.ios.wkrdp.ResponseFinderList;
import org.uiautomation.ios.wkrdp.message.ApplicationDataMessage;
import org.uiautomation.ios.wkrdp.message.IOSMessage;
import org.uiautomation.ios.wkrdp.message.MessageFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DefaultMessageHandler implements MessageHandler {

  private final static long timeoutInMs = 5000;
  private final List<MessageListener> listeners = new CopyOnWriteArrayList<MessageListener>();
  private Set<Thread> threads = new HashSet<Thread>();
  private boolean stopped;
  private static final Logger log = Logger.getLogger(DefaultMessageHandler.class.getName());
  private List<ResponseFinder> extraFinders = new ArrayList<ResponseFinder>();
  private final MessageFactory factory = new MessageFactory();
  private final
  DefaultWebKitResponseFinder
      defaultFinder =
      new DefaultWebKitResponseFinder(timeoutInMs);

  public DefaultMessageHandler(MessageListener listener, ResponseFinder... finders) {
    listeners.add(listener);

    Collections.addAll(this.extraFinders, finders);
  }

  private static int threadCount;

  @Override
  public synchronized void handle(final String msg) {
    if (stopped) {
      throw new IllegalStateException("stopped");
    }

    Thread t = new Thread("DefaultMessageHandler-" + ++threadCount) {
      @Override
      public void run() {
        process(msg);
        threads.remove(this);
      }
    };
    threads.add(t);
    t.start();
  }

  private void process(String rawMessage) {
    IOSMessage message = factory.create(rawMessage);

    for (MessageListener l : listeners) {
      l.onMessage(message);
    }

    if (message instanceof ApplicationDataMessage) {
      JSONObject content = ((ApplicationDataMessage) message).getMessage();
      if ((content.optInt("id", -1) != -1)) {
        defaultFinder.addResponse(content);
      }
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

    List<ResponseFinder> finders = new ArrayList<ResponseFinder>();
    finders.add(defaultFinder);
    finders.addAll(extraFinders);

    ResponseFinderList all = new ResponseFinderList(finders, timeoutInMs);
    try {
      JSONObject res = all.findResponse(id);
      log.fine(
          "response " + id + " , " + (System.currentTimeMillis() - start) + "ms. " + res
              .toString());

      return res;
    } catch (RuntimeException e) {
      throw e;
    }
  }


  @Override
  public void addListener(MessageListener listener) {
    listeners.add(listener);
    log.log(Level.INFO,
        "DefaultMessageHandler addListener type: " + listener.getClass().getName(),
        new Exception("log the stack"));
    // Let all interested listeners know if this is a new WebInspector.
    if (listener instanceof WebInspector) {
      WebInspector inspector = (WebInspector) listener;
      for (MessageListener curListener : listeners) {
        if (curListener instanceof ConnectListener) {
          ((ConnectListener) curListener).onConnect(inspector);
        }
      }
    }
  }

  @Override
  public synchronized void stop() {
    stopped = true;
    for (Thread t : threads) {
      t.interrupt();
    }
  }


}
