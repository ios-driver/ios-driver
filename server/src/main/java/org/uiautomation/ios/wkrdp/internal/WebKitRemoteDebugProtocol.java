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

import com.google.common.collect.ImmutableMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.utils.PlistManager;
import org.uiautomation.ios.wkrdp.MessageHandler;
import org.uiautomation.ios.wkrdp.MessageListener;
import org.uiautomation.ios.wkrdp.RemoteExceptionException;
import org.uiautomation.ios.wkrdp.ResponseFinder;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Communication logic for the WKRDP to connect to an app, and to a specific webview inside that
 * app.
 *
 * @see WebKitRemoteDebugProtocol#sendWebkitCommand(org.json.JSONObject, int) to use the protocol
 *      itself.
 */
public abstract class WebKitRemoteDebugProtocol {

  private static final Logger log = Logger.getLogger(WebKitRemoteDebugProtocol.class.getName());
  protected final MessageHandler handler;
  private Thread listen;
  private String connectionId;
  private String bundleId;
  private final PlistManager plist = new PlistManager();
  private final static String senderBase = "E0F4C128-F4FF-4D45-A538-BA382CD660";
  private int commandId = 0;

  private volatile boolean keepGoing = true;
  private volatile boolean readyToBeStopped = true;


  public abstract void start();

  public abstract void stop();

  protected abstract void read() throws Exception;

  protected abstract void sendMessage(String message);


  protected void startListenerThread() {
    listen = new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          readyToBeStopped = false;
          keepGoing = true;
          while (keepGoing) {
            read();
            sleepTight(50);
          }

        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          readyToBeStopped = true;
        }
      }
    });

    listen.start();
  }


  public WebKitRemoteDebugProtocol(MessageListener listener,
                                   ResponseFinder... finders) {
    this.handler = new DefaultMessageHandler(listener, finders);
  }

  public void addListener(MessageListener listener) {
    handler.addListener(listener);
  }


  public void register() {
    if (connectionId != null) {
      throw new WebDriverException("Session already created.");
    }
    connectionId = UUID.randomUUID().toString();

    Map<String, String> var = ImmutableMap.of("$WIRConnectionIdentifierKey", connectionId);
    sendSystemCommand(PlistManager.SET_CONNECTION_KEY, var);
  }

  public void connect(String bundleId) {
    if (connectionId == null) {
      throw new WebDriverException("Cannot connect to app " + bundleId + ".Call register first.");
    }
    Map<String, String> var = ImmutableMap.of
        (
            "$WIRConnectionIdentifierKey", this.connectionId,
            "$bundleId", bundleId
        );
    sendSystemCommand(PlistManager.CONNECT_TO_APP, var);
    this.bundleId = bundleId;
  }

  public void attachToPage(int pageId) {
    String senderKey = generateSenderString(pageId);
    if (connectionId == null || bundleId == null) {
      throw new WebDriverException("You need to call register and connect first.");
    }

    Map<String, String> var = ImmutableMap.of
        (
            "$WIRConnectionIdentifierKey", connectionId,
            "$bundleId", bundleId,
            "$WIRSenderKey", senderKey,
            "$WIRPageIdentifierKey", "" + pageId
        );
    sendSystemCommand(PlistManager.SET_SENDER_KEY, var);
  }

  private void sendSystemCommand(String templateName, Map<String, String> variables) {
    String xml = plist.loadFromTemplate(templateName);
    for (String key : variables.keySet()) {
      xml = xml.replace(key, variables.get(key));
    }
    sendMessage(xml);
  }


  public synchronized JSONObject sendWebkitCommand(JSONObject command, int pageId) {
    String sender = generateSenderString(pageId);
    try {
      commandId++;
      command.put("id", commandId);

      long start = System.currentTimeMillis();

      String xml = plist.JSONCommand(command);
      Map<String, String> var = ImmutableMap.of
          (
              "$WIRConnectionIdentifierKey", connectionId,
              "$bundleId", bundleId,
              "$WIRSenderKey", sender,
              "$WIRPageIdentifierKey", "" + pageId
          );
      for (String key : var.keySet()) {
        xml = xml.replace(key, var.get(key));
      }
      sendMessage(xml);
      JSONObject response = handler.getResponse(command.getInt("id"));
      JSONObject error = response.optJSONObject("error");
      if (error != null) {
        throw new RemoteExceptionException(error, command);
      } else if (response.optBoolean("wasThrown", false)) {
        throw new WebDriverException("remote JS exception " + response.toString(2));
      } else {
        if (log.isLoggable(Level.FINE))
          log.fine(System.currentTimeMillis() + "\t\t" + (System.currentTimeMillis() - start) + "ms\t"
                 + command.getString("method") + " " + command);
        JSONObject res = response.getJSONObject("result");
        if (res == null) {
          System.err.println("GOT a null result from " + response.toString(2));
        }
        return res;
      }
    } catch (JSONException e) {
      throw new WebDriverException(e);
    } catch (Exception e){
      throw new WebDriverException("the server didn't respond. Is the app still alive ?");
    }
  }

  private String generateSenderString(int pageIdentifierKey) {
    if (pageIdentifierKey < 10) {
      return senderBase + "0" + pageIdentifierKey;
    } else {
      return senderBase + pageIdentifierKey;
    }

  }

  public void stopListenerThread() {
    if (handler != null) {
      handler.stop();
    }

    keepGoing = false;
    if (listen != null) {
      listen.interrupt();
    }
    while (!readyToBeStopped) {
      sleepTight(50);
    }
  }

  private static void sleepTight(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException ignore) {
      Thread.currentThread().interrupt();
    }
  }
}
