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
package org.uiautomation.ios.mobileSafari.remoteWebkitProtocol;

import org.json.JSONObject;
import org.openqa.selenium.TimeoutException;
import org.uiautomation.ios.mobileSafari.remoteWebkitProtocol.message.ApplicationDataMessage;
import org.uiautomation.ios.mobileSafari.remoteWebkitProtocol.message.IOSMessage;
import org.uiautomation.ios.mobileSafari.remoteWebkitProtocol.message.MessageFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;


public class DefaultMessageHandler implements MessageHandler {

  private final static long timeoutInMs = 5000;
  private final List<MessageListener> listeners = new CopyOnWriteArrayList<MessageListener>();
  private Thread t;
  private static final Logger log = Logger.getLogger(DefaultMessageHandler.class.getName());
  private List<ResponseFinder> extraFinders = new ArrayList<ResponseFinder>();
  private final MessageFactory factory = new MessageFactory();
  private final
  DefaultWebKitResponseFinder
      defaultFinder =
      new DefaultWebKitResponseFinder(timeoutInMs);

  public DefaultMessageHandler(MessageListener listener, ResponseFinder... finders) {
    listeners.add(listener);

    for (ResponseFinder finder : finders) {
      this.extraFinders.add(finder);
    }
  }

  @Override
  public void handle(final String msg) {
    t = new Thread(new Runnable() {

      @Override
      public void run() {
        process(msg);
      }
    });
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


  @Override
  public void addListener(MessageListener listener) {
    listeners.add(listener);
  }

  @Override
  public void stop() {
    if (t != null) {
      t.interrupt();
    }
  }


}
