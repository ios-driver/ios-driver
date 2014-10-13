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
package org.uiautomation.ios.logging;

import org.uiautomation.ios.wkrdp.WebInspector;
import org.uiautomation.ios.wkrdp.ConnectListener;
import org.uiautomation.ios.wkrdp.command.Network;
import org.uiautomation.ios.wkrdp.command.Page;
import org.uiautomation.ios.wkrdp.command.Timeline;
import org.uiautomation.ios.wkrdp.MessageListener;
import org.uiautomation.ios.wkrdp.message.ApplicationDataMessage;
import org.uiautomation.ios.wkrdp.message.IOSMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;

import java.util.logging.Level;

/*
 * A listener class that enables and logs Network and Timeline messages from DevTools.
 */
public class PerformanceListener implements MessageListener, ConnectListener {

  private static final String[] DOMAINS = new String[] {
      "Network.", "Page.", "Timeline."};

  private final Log log;

  public PerformanceListener(Log log) {
    this.log = log;
  }

  @Override
  public void onConnect(WebInspector inspector) {
    inspector.sendCommand(Network.enable());
    inspector.sendCommand(Page.enable());
    inspector.sendCommand(Timeline.start());
  }

  private boolean shouldLog(IOSMessage message) {
    if (message instanceof ApplicationDataMessage) {
      JSONObject messageJson = ((ApplicationDataMessage) message).getMessage();
      String method = messageJson.optString("method");
      for (String domain : DOMAINS) {
        if (method.startsWith(domain)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void onMessage(IOSMessage message) {
    if (!shouldLog(message)) {
      return;
    }
    ApplicationDataMessage appDataMessage = (ApplicationDataMessage) message;
    String pageKey = appDataMessage.getDestinationKey();
    JSONObject inspectorMessageJson = appDataMessage.getMessage();
    JSONObject wdLogMessageJson = new JSONObject();
    try {
      wdLogMessageJson.put("message", inspectorMessageJson);
      wdLogMessageJson.put("webview", pageKey);
    } catch (JSONException ex) {
      throw new WebDriverException(ex);
    }
    log.addEntry(Level.INFO, wdLogMessageJson.toString());
  }

}
