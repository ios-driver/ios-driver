package org.uiautomation.ios.mobileSafari.events;

import java.util.logging.Logger;

import org.json.JSONObject;
import org.uiautomation.ios.mobileSafari.events.inserted.ChildIframeInserted;

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
    JSONObject params = message.optJSONObject("params");

    if (NODE_REMOVED.equals(method)) {
      return ChildNodeRemoved.class;
    }

    if (NODE_INSERTED.equals(method) 
        && params != null
        && "IFRAME".equals(params.optJSONObject("node").optString("nodeName"))) {
      return ChildIframeInserted.class;
    }

    log.fine("message not handled " + message);
    return RawEvent.class;

  }

}
