package org.uiautomation.ios.server;

import org.uiautomation.ios.mobileSafari.EventListener;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class DOMContext implements EventListener {

  private volatile boolean pageLoaded = false;
  private RemoteWebElement document;
  private RemoteWebElement iframe;

  private RemoteWebElement mainDocument;

  public RemoteWebElement getDocument() {
    return document;
  }

  public void reset() {
    document = null;
    iframe = null;
    mainDocument = null;
  }

  public void setCurrentFrame(RemoteWebElement iframe, RemoteWebElement document) {
    this.iframe = iframe;
    this.document = document;

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

  public void waitForPageToLoad() {
    while (!pageLoaded) {
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
