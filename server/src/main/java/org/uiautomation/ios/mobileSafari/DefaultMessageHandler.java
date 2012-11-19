package org.uiautomation.ios.mobileSafari;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.json.JSONObject;
import org.uiautomation.ios.exceptions.IOSAutomationException;

public class DefaultMessageHandler implements MessageHandler {

  private final List<JSONObject> responses = new CopyOnWriteArrayList<JSONObject>();
  private final EventListener listener;
  private Thread t;

  public DefaultMessageHandler(EventListener listener) {
    this.listener = listener;
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
    try {
      JSONObject message = extractResponse(rawMessage);
      if (message == null) {
        return;
      }
      if (message.optInt("id", -1) != -1) {
        responses.add(message);
      } else if (isPageLoad(message)) {
        listener.onPageLoad();
      } else if ("Page.frameNavigated".equals(message.optString("method"))) {
        listener.frameNavigated(message);
      } else {
        System.err.println(message.toString());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private boolean isPageLoad(JSONObject message) {
    String method = message.optString("method");
    return "Page.loadEventFired".equals(method);
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
    long timeout = 5 * 1000;
    // TODO handle stop() in there
    long end = System.currentTimeMillis() + timeout;
    while (true) {

      if (System.currentTimeMillis() > end) {
        throw new TimeoutException("timeout waiting for a response for request id : " + id);
      }
      try {
        Thread.sleep(10);
        for (JSONObject o : responses) {
          if (o.optInt("id") == id) {
            // responses.remove(o);
            return o;
          }
        }
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();

      }

    }
  }

  @Override
  public void stop() {
    if (t != null) {
      t.interrupt();
    }

  }

}
