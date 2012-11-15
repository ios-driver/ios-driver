package org.uiautomation.ios.mobileSafari;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONException;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.webInspector.DOM.IFrame;
import org.uiautomation.ios.webInspector.DOM.Node;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class DOMContext {

  private final List<IFrame> knownIFrame = new CopyOnWriteArrayList<IFrame>();
  private IFrame current;
  private Node currentDocument;
  private RemoteWebElement document;
  private volatile Boolean newPageLoaded = false;
  public static Object lock = new Object();
  private final WebInspector inspector;
  private final ServerSideSession session;

  public DOMContext(WebInspector inspector, ServerSideSession session) {
    this.inspector = inspector;
    this.session = session;
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
      System.err.println("getting document : "+(System.currentTimeMillis() - start)+"ms.");
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

}
