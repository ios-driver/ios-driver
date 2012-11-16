package org.uiautomation.ios.server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.mobileSafari.DOMMessageHandler;
import org.uiautomation.ios.mobileSafari.EventListener;
import org.uiautomation.ios.mobileSafari.MessageHandler;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.mobileSafari.WebInspector;
import org.uiautomation.ios.webInspector.DOM.IFrame;
import org.uiautomation.ios.webInspector.DOM.Node;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;
import org.uiautomation.ios.webInspector.DOM.SetChildNodes;

public class DOMContext implements EventListener {

  /*private final List<IFrame> knownIFrame = new CopyOnWriteArrayList<IFrame>();
  private IFrame current;
  private Node currentDocument;
  private RemoteWebElement document;
  private volatile Boolean newPageLoaded = false;
  public static Object lock = new Object();
  private final WebInspector inspector;
  private final ServerSideSession session;
  private final List<JSONObject> responses = new CopyOnWriteArrayList<JSONObject>();*/
  
  
  private volatile boolean pageLoaded = false;
  private RemoteWebElement document;
  private RemoteWebElement iframe;

  
  public RemoteWebElement getDocument(){
    return document;
  }
  
  public void setCurrentFrame(RemoteWebElement iframe,RemoteWebElement document){
    this.iframe = iframe;
    this.document = document;
  }
  
  public RemoteWebElement getCurrentFrame(){
    return iframe;
  }
  
  @Override
  public void onPageLoad() {
   pageLoaded = true;
    
  }
  
  public void waitForPageToLoad() {
    while (!pageLoaded){
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        //ignore
      }
    }
    pageLoaded = false;
    return;
  }
  
  
  public DOMContext(){
    System.out.println("CREATING DOM CONTEXT");
  }

 
  
  /*public DOMContext(ServerSideSession session) {
    this.session = session;
    this.inspector = session.getWebInspector();
  }

  // id - {string|number|null|WebElement JSON Object} Identifier for the frame
  // to change focus to.
  public void setContext(IFrame iframe) {
    current = iframe;
    currentDocument = iframe.getContentDocument();
  }

  public void setContextToBase(Node document) {
    synchronized (lock) {
      current = null;
      currentDocument = document;
    }

  }

  public IFrame getIFrame(NodeId id) {
    for (IFrame f : knownIFrame) {
      if (f.getNodeId().equals(id)) {
        return f;
      }
    }
    throw new RuntimeException("no such frame");
  }

  public RemoteWebElement getCurrentDocument() {
    long start = System.currentTimeMillis();
    synchronized (lock) {
      try {
        document.readyState();
      } catch (Exception e) {
        System.err.println("corrupted document " + e.getMessage());
        try {
          document = getCurrentDocumentAndCheckTheRemoteObject();
        } catch (Exception e2) {
          System.err.println("returning a corrupted document " + e.getMessage());
        }
      }
      System.err.println("getting document : " + (System.currentTimeMillis() - start) + "ms.");
      return document;
    }
  }

  public void addIframe(IFrame iframe) {
    knownIFrame.add(iframe);
  }

  public void addIframe(List<IFrame> iframes) {
    if (iframes.size() != 0) {
      System.out.println("got " + iframes.size() + " new iframes.");
    }
    this.knownIFrame.addAll(iframes);
  }

  public List<IFrame> getIFrames() {
    return knownIFrame;
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append(knownIFrame.size() + " iframes, [");
    for (Node iframe : knownIFrame) {
      b.append("id=" + iframe.getContentDocument().getNodeId());
      b.append(",src=" + iframe.getContentDocument().getDocumentURL());
    }
    b.append("]");
    return b.toString();
  }

  public void onLoad() {
    synchronized (lock) {
      newPageLoaded = true;
      if (currentDocument == null) {
        document = getCurrentDocumentAndCheckTheRemoteObject();
      }
      // System.out.println("new page loaded event" +
      // currentDocument.getNodeName());
    }
  }

  public void waitForPageToLoad() {
    int cpt = 0;

    while (!newPageLoaded) {
      cpt++;
      try {
        // System.out.println("waiting ...new page loaded = " + newPageLoaded);
        Thread.sleep(250);
      } catch (InterruptedException e) {
      }
      if (cpt > 10) {
        break;
      }
    }

    synchronized (lock) {
      newPageLoaded = false;
    }

  }

  private RemoteWebElement getCurrentDocumentAndCheckTheRemoteObject() {

    while (true) {
      try {
        if (currentDocument == null) {
          currentDocument = inspector.getDocument();
        }
        NodeId id = currentDocument.getNodeId();
        try {
          RemoteWebElement element = new RemoteWebElement(id, session);
          return element;
        } catch (Exception e) {
          e.printStackTrace();
        }
        break;
      } catch (Exception e) {
        System.err.println("Workaround in DOMContext, the given document is corrupted, nodeId ");
        // ignore.
      }
    }
    return document;
  }

  private Thread t;

  private void process(String msg) {
    // System.out.println("got message : " + msg);

    msg = msg.replace(
        "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">",
        "");
    SAXReader reader = new SAXReader();
    try {
      Document document = reader.read(IOUtils.toInputStream(msg));
      org.dom4j.Node n = document.selectSingleNode("/plist/dict/dict/data");
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
            SetChildNodes notification = new SetChildNodes(o, inspector);
            addIframe(notification.getIFrames());
          } // else if ("DOM.documentUpdated".equals(o.optString("method"))) {
          else if ("Profiler.resetProfiles".equals(o.optString("method"))
              || "DOM.documentUpdated".equals(o.optString("method"))) {
            try {
              onLoad();
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
  }*/

}
