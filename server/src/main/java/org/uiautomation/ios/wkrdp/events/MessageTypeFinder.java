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
package org.uiautomation.ios.wkrdp.events;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.uiautomation.ios.wkrdp.events.inserted.ChildIframeInserted;

public class MessageTypeFinder {

  private static final Logger log = Logger.getLogger(MessageTypeFinder.class.getName());

  private static final String NODE_REMOVED = "DOM.childNodeRemoved";
  private static final String NODE_INSERTED = "DOM.childNodeInserted";

  private final JSONObject message;

  public MessageTypeFinder(JSONObject message) {
    this.message = message;
  }

  public Class<? extends Event> getAssociatedEvent() {
    String method = message.optString("method");

    if (NODE_REMOVED.equals(method)) {
      return ChildNodeRemoved.class;
    }

    if (isFrameOrIFrame(message)) {
      if (log.isLoggable(Level.FINE))
        log.fine("ChildIframeInserted " + message);
      return ChildIframeInserted.class;
    }

    if (log.isLoggable(Level.FINER))
      log.finer("message not handled: " + message);
    return RawEvent.class;
  }

  private boolean isFrameOrIFrame(JSONObject message) {

    String method = message.optString("method");
    JSONObject params = message.optJSONObject("params");

    if (NODE_INSERTED.equals(method) && params != null
        && "IFRAME".equals(params.optJSONObject("node").optString("nodeName"))) {
      if (log.isLoggable(Level.FINE))
        log.fine("ChildIframeInserted " + message);
      return true;
    }
    if (NODE_INSERTED.equals(method) && params != null
        && "FRAME".equals(params.optJSONObject("node").optString("nodeName"))) {
      if (log.isLoggable(Level.FINE))
        log.fine("ChildIframeInserted " + message);
      return true;
    }
    return false;
  }
}
