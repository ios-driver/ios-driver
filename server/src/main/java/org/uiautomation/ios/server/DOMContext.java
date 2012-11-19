package org.uiautomation.ios.server;

import org.json.JSONObject;
import org.openqa.selenium.TimeoutException;
import org.uiautomation.ios.mobileSafari.EventListener;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class DOMContext implements EventListener {

  private volatile boolean pageLoaded = false;

  private volatile boolean isReady = true;
  private NodeId parent;

  private final ServerSideSession session;

  private RemoteWebElement window;
  private RemoteWebElement document;
  private RemoteWebElement iframe;

  private RemoteWebElement mainDocument;
  private RemoteWebElement mainWindow;

  public RemoteWebElement getDocument() {
    int cpt = 0;
    while (!isReady) {
      cpt++;
      if (cpt > 20) {
        isReady = true;
        throw new TimeoutException("doc not ready.");
      }
      try {
        //System.err.println("cannot get document. Something is happening.");
        Thread.sleep(250);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return document;
  }

  public RemoteWebElement getWindow() {
    return window;
  }

  public void reset() {
    window = null;
    document = null;
    iframe = null;
    mainDocument = null;
  }

  // TODO freynaud. Cleanup. A reference to the main document of the page needs
  // to be kept.
  // calling getDocument again to have the document after siwtching to an iframe
  // breaks the nodeId reference.
  public void setCurrentFrame(RemoteWebElement iframe, RemoteWebElement document, RemoteWebElement window) {
    this.iframe = iframe;
    this.document = document;
    this.window = window;

    if (iframe != null) {
      /*System.out.println("SWITCH TO NEW FRAME : " + iframe.getNodeId() + ",document " + document.getNodeId() + ",window "
          + window.getNodeId());*/
    }

    // switchToDefaultContent. revert to main document if it was set.
    if (iframe == null && document == null) {
      this.document = mainDocument;
      this.window = mainWindow;
    }

    // setting the main document for the first time
    if (iframe == null && document != null) {
      //System.out.println("BACKUP DOCUMENT "+document.getNodeId().getId());
      mainDocument = document;
      mainWindow = window;
    }

  }

  public RemoteWebElement getCurrentFrame() {
    return iframe;
  }

  @Override
  public void onPageLoad() {
    pageLoaded = true;
    reset();
  }

  public void waitForPageToLoad(long timeout) {
    long start = System.currentTimeMillis();
    long deadLine = start + timeout;
    while (!pageLoaded) {
      if (System.currentTimeMillis() > deadLine) {
        throw new TimeoutException("failed to load the page after " + timeout + " ms.");
      }
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        // ignore
      }
    }
    pageLoaded = false;
    return;
  }

  public DOMContext(ServerSideSession session) {
    this.session = session;
  }

  // TODO freynaud completly wrong. Needs to keep track of the parent for a
  // frame, not just the top level parent.
  @Override
  public void domHasChanged(JSONObject message) {
    try {
      String method = message.optString("method");

      if ("DOM.childNodeRemoved".equals(method)) {
        JSONObject params = message.optJSONObject("params");
        int nodeId = params.getInt("nodeId");
        int parentNodeId = params.getInt("parentNodeId");
        if (iframe != null ? nodeId == iframe.getNodeId().getId() : false) {
          //System.err.println("current frame " + nodeId + " is gone.Parent : " + parentNodeId);
          isReady = false;
          parent = new NodeId(parentNodeId);

        }
        return;
      }

      if ("DOM.childNodeInserted".equals(method)) {
        // are we waiting for something ?
        if (isReady) {
          return;
        } else {
          JSONObject params = message.optJSONObject("params");

          int parentNodeId = params.getInt("parentNodeId");
          // is it the new node we're looking for ?
          if (parent.equals(new NodeId(parentNodeId))) {
            System.out.println("the new node is there !");
            JSONObject node = params.getJSONObject("node");
            int frameId = node.getInt("nodeId");
            RemoteWebElement frame = new RemoteWebElement(new NodeId(frameId), session);
            //System.out.println("should be a (i)frame, it's :" + node.getString("nodeName"));
            JSONObject contentDoc = node.getJSONObject("contentDocument");
            int contentDocId = contentDoc.getInt("nodeId");
            RemoteWebElement document = new RemoteWebElement(new NodeId(contentDocId), session);
            RemoteWebElement window = frame.getContentWindow();
            setCurrentFrame(frame, document, window);
            isReady = true;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    // current frame content has navigated to a new frame.
    /*
     * if (mainDocument == null) {
     * System.err.println("Framenavigated , but no update."); return; } try { //
     * case 1 (done): //
     * MESSAGE:{"method":"Page.frameNavigated","params":{"frame"
     * :{"id":"0.3","parentId"
     * :"0.1","loaderId":"0.5","name":"iframe1-name","securityOrigin"
     * :"http://localhost:47287","mimeType":"text/html","url":
     * "http://localhost:47287/common/resultPage.html?"}}} // TODO case 2 (
     * should also cover case 1) : //
     * {"method":"DOM.childNodeRemoved","params":{"nodeId":13,"parentNodeId":8}}
     * //
     * {"method":"DOM.childNodeInserted","params":{"node":{"childNodeCount":0,
     * "localName"
     * :"frame","nodeId":93,"nodeValue":"","nodeName":"FRAME","attributes"
     * :["name","third","src","formPage.html"],"nodeType":1,"contentDocument":{
     * "childNodeCount":1,"localName":"","nodeId":94,"documentURL":
     * "http://localhost:16167/common/resultPage.html?checky=furrfu&checkedchecky=on&selectomatic=one&multi=eggs&multi=sausages&select-default=One&select_with_spaces=One&snack=cheese+and+peas&hidden=fromage"
     * ,"nodeValue":"","nodeName":"#document","xmlVersion":"","nodeType":9}},
     * "parentNodeId":8,"previousNodeId":11}}
     * 
     * String name =
     * message.getJSONObject("params").getJSONObject("frame").getString("name");
     * List<RemoteWebElement> elements =
     * mainDocument.findElementsByCSSSelector("iframe"); for (RemoteWebElement
     * element : elements) { if (name.equals(element.getAttribute("name"))) {
     * setCurrentFrame(element, element.getContentDocument(),
     * element.getContentWindow()); return; } } } catch (Exception e) { // TODO
     * Auto-generated catch block e.printStackTrace(); }
     */
  }

  @Override
  public void frameDied(JSONObject message) {
    // if that's the one we're working on, deselect it.
    if (iframe != null) {
      if (!iframe.exists()) {
        System.err
            .println("the current frame is dead. Will need to switch to default content or another frame before being able to do anything.");
        isReady = true;
      }
    }

  }

}
