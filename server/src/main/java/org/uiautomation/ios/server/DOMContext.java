package org.uiautomation.ios.server;

import org.openqa.selenium.TimeoutException;
import org.uiautomation.ios.mobileSafari.EventListener;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class DOMContext implements EventListener {

  private volatile boolean pageLoaded = false;
  
  private RemoteWebElement window;
  private RemoteWebElement document;
  private RemoteWebElement iframe;

  private RemoteWebElement mainDocument;

  public RemoteWebElement getDocument() {
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
  public void setCurrentFrame(RemoteWebElement iframe, RemoteWebElement document,RemoteWebElement window) {
    this.iframe = iframe;
    this.document = document;
    this.window = window;

    // switchToDefaultContent. revert to main document if it was set.
    if (iframe == null && document == null) {
      this.document = mainDocument;
    }

    // setting the main document for the first time
    if (iframe == null && document != null) {
      mainDocument = document;
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

  public DOMContext() {

  }

}
