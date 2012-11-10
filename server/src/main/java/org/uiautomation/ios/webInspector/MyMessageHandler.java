package org.uiautomation.ios.webInspector;



import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.webInspector.DOM.SetChildNodes;

public class MyMessageHandler implements MessageHandler {


  private final DOMContext cache;
  private final List<JSONObject> responses = new CopyOnWriteArrayList<JSONObject>();
  private final WebInspector inspector;

  public MyMessageHandler(DOMContext cache, WebInspector inspector) {
    this.inspector = inspector;
    this.cache = cache;
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


  private void process(String msg) {
    // System.out.println("got message : " + msg);

    msg =
        msg.replace(
            "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">",
            "");
    SAXReader reader = new SAXReader();
    try {
      Document document = reader.read(IOUtils.toInputStream(msg));
      Node n = document.selectSingleNode("/plist/dict/dict/data");
      if (n != null) {
        String encoded = n.getText();
        byte[] bytes = Base64.decodeBase64(encoded);
        String s = new String(bytes);
        JSONObject o = new JSONObject(s);
        int id = o.optInt("id", -1);
        if (id != -1) {
          responses.add(o);
        } else {
          if ("DOM.setChildNodes".equals(o.optString("method"))) {

            SetChildNodes notification = new SetChildNodes(o);
            cache.addIframe(notification.getIFrames());
          } else if ("DOM.documentUpdated".equals(o.optString("method"))) {
            try {
              System.out.println("UPDATED 1/3");
              org.uiautomation.ios.webInspector.DOM.Node d = inspector.getDocument();
              System.out.println("UPDATED 2/3");
              cache.onLoad(d);
              System.out.println("UPDATED 3/3");
            } catch (Exception e) {
              e.printStackTrace();
            }
          } else {
            System.err.println(o.toString());
          }
        }
      }

    } catch (DocumentException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }


  @Override
  public JSONObject getResponse(int id) {
    while (true) {
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
}
