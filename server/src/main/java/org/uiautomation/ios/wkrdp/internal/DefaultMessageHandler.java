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
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.wkrdp.WebInspector;
import org.uiautomation.ios.wkrdp.ConnectListener;
import org.uiautomation.ios.wkrdp.MessageHandler;
import org.uiautomation.ios.wkrdp.MessageListener;
import org.uiautomation.ios.wkrdp.message.ApplicationDataMessage;
import org.uiautomation.ios.wkrdp.message.IOSMessage;
import org.uiautomation.ios.wkrdp.message.MessageFactory;

import com.google.common.util.concurrent.SettableFuture;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.NoSuchElementException;

public class DefaultMessageHandler implements MessageHandler {

  private final List<MessageListener> listeners = new CopyOnWriteArrayList<MessageListener>();
  private Set<Thread> threads = new HashSet<Thread>();
  private boolean stopped;
  private static final Logger log = Logger.getLogger(DefaultMessageHandler.class.getName());
  private final MessageFactory factory;
  private final Map<Integer, SettableFuture<JSONObject>> futureResponses = new HashMap<>();
  private static int threadCount;

  public DefaultMessageHandler(ServerSideSession session) {
    factory = new MessageFactory(session);
  }

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
      int id = content.optInt("id", -1);
      if (id != -1) {
        SettableFuture<JSONObject> response = futureResponses.remove(id);
        if (response == null) {
          throw new NoSuchElementException("command id: " + id);
        } else if (response.isDone()) {
          throw new IllegalArgumentException("The command id " + id + " has already received "
              + "a corresponding reply");
        }
        response.set(content);
      }
    }
  }

  public SettableFuture<JSONObject> createMessageFuture(int commandId) {
    Integer id = new Integer(commandId);
    if (futureResponses.containsKey(id)) {
      throw new IllegalArgumentException("Command id already used");
    }
    SettableFuture<JSONObject> res = SettableFuture.create();
    futureResponses.put(id, res);
    return res;
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
    if (!futureResponses.isEmpty()) {
      throw new WebDriverException("No response received for commands before stop: "
          + futureResponses.keySet());
    }
    stopped = true;
    for (Thread t : threads) {
      t.interrupt();
    }
  }

}