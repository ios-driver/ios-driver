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

package org.uiautomation.ios.drivers;

import com.google.common.collect.ImmutableList;

import org.json.JSONArray;
import org.json.JSONException;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.utils.ClassicCommands;
import org.uiautomation.ios.wkrdp.BaseWebInspector;
import org.uiautomation.ios.wkrdp.DOMContext;
import org.uiautomation.ios.wkrdp.MessageListener;
import org.uiautomation.ios.wkrdp.WebInspector;
import org.uiautomation.ios.wkrdp.WebKitNotificationListener;
import org.uiautomation.ios.wkrdp.internal.RealDeviceProtocolImpl;
import org.uiautomation.ios.wkrdp.internal.WebKitRemoteDebugProtocol;
import org.uiautomation.ios.wkrdp.internal.WebKitSynchronizer;
import org.uiautomation.ios.wkrdp.message.WebkitApplication;
import org.uiautomation.ios.wkrdp.message.WebkitDevice;
import org.uiautomation.ios.wkrdp.message.WebkitPage;
import org.uiautomation.ios.wkrdp.model.NodeId;
import org.uiautomation.ios.wkrdp.model.RemoteWebElement;
import org.uiautomation.ios.wkrdp.model.RemoteWebNativeBackedElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoteIOSWebDriver {

  private String bundleId;

  private final WebKitRemoteDebugProtocol protocol;
  private WebkitDevice device;
  private List<WebkitApplication> applications = new ArrayList<>();
  private final ServerSideSession session;
  private final String connectionKey;
  private BaseWebInspector currentInspector;
  private final Map<Integer, BaseWebInspector> inspectors = new HashMap<>();
  private static final Logger log = Logger.getLogger(RemoteIOSWebDriver.class.getName());
  private List<WebkitPage> pages = new ArrayList<>();
  private final WebKitSynchronizer sync;
  private boolean isStarted = false;

  public RemoteIOSWebDriver(ServerSideSession session, WebKitRemoteDebugProtocol protocol) {
    this.session = session;
    connectionKey = UUID.randomUUID().toString();
    sync = new WebKitSynchronizer(this);
    this.protocol = protocol;

    MessageListener messageListener = new WebKitNotificationListener(this, sync, session);
    protocol.addListener(messageListener);

  }

  public void setPages(List<WebkitPage> pages) {
    this.pages = ImmutableList.copyOf(pages);
  }

  public List<WebkitPage> getPages() {
    return pages;
  }


  public boolean isStarted() {
    return isStarted;
  }

  public void start() {
    protocol.start();

    if (session != null) {
      session.getLogManager().onProtocolCreated(protocol);
    }

    try {
      protocol.register();
    } catch (WebDriverException wde) {
      log.warning("protocol already registered with connectionId: " + protocol.getConnectionId());
    }
    sync.waitForSimToRegister();
    sync.waitForSimToSendApps();

    log.fine("connectionKey=" + connectionKey);

    if (applications.size() == 1) {
      connect(applications.get(0).getBundleId());
      isStarted = true;
    } else {
      for (int index = 0; index < applications.size(); index++) {
        if (!applications.get(index).getApplicationName().equalsIgnoreCase("Safari")) {
          connect(applications.get(index).getBundleId());
          isStarted = true;
          break;
        }
      }
    }
    if (!isStarted) {
      showWarning();
    }

  }

  private void showWarning() {
    // Safari.
    if (session.getApplication().isSafari()) {
      if (applications.size() == 0) {
        if (protocol instanceof RealDeviceProtocolImpl) {
          throw new WebDriverException("is Safari started and with the focus ? ");
        } else {
          // kill left over.
          ClassicCommands.killall("xpcproxy_sim");
          throw new WebDriverException("session created but application size=" + applications.size()
                                       + ".The simulator wasn't closed properly.Try restarting your computer.");
        }
      } else {
        throw new WebDriverException("session created but application size=" + applications.size()
                                     + ".It should be 1. Do you have multiple tabs opened ?");
      }
      // Native app
    } else {
      log.warning("session created but application size=" + applications.size()
                  + ".Does the app have a webview ?");
    }
  }

  public void stop() {
    isStarted = false;
    protocol.stop();
  }

  // Native-backed (embedded web view) element references are encoded as e.g. "1_42", whereas plain elements are
  // encoded as e.g. "17".  The JavaScript code necessary to operate on the two types of elements differs
  // substantially.
  public static boolean isPlainElement(String elementId) {
    return (elementId.split("_").length == 1);
  }

  public static NodeId plainNodeId(String elementId) {
    return new NodeId(Integer.parseInt(elementId));
  }

  public RemoteWebElement createElement(String elementId) {
    int pageId = Integer.parseInt(elementId.split("_")[0]);
    int nodeId = Integer.parseInt(elementId.split("_")[1]);

    if (currentInspector.getPageIdentifier() != pageId) {
      throw new StaleElementReferenceException("Node " + nodeId
                                               + "is stale.It might still exist, but the "
                                               + "window with focus has changed.");
    }
    if (session != null) {
      return new RemoteWebNativeBackedElement(new NodeId(nodeId), currentInspector, session);
    } else {
      return new RemoteWebElement(new NodeId(nodeId), currentInspector);
    }
  }

  public void connect(String bundleId) {
    List<WebkitApplication> knownApps = getApplications();
    for (WebkitApplication app : knownApps) {
      if (bundleId.equals(app.getBundleId())) {
        this.bundleId = bundleId;
        protocol.connect(bundleId);
        sync.waitForSimToSendPages();
        log.fine("bundleId=" + bundleId);
        if (getPages() != null && getPages().size() > 0) {
          switchTo(Collections.max(getPages()));
          if (getPages().size() > 1) {
            log.warning("Application started, but already have " + getPages().size()
                + " webviews. Connecting to the one with highest page id.");
          }
        } else {
            log.warning("Application started, but doesn't have any page.");
        }
        return;
      }
    }
    throw new WebDriverException(bundleId + " not in the list " + knownApps
                                 + ".Either it's not started, or it has no webview to connect to.");
  }

  public synchronized void setApplications(List<WebkitApplication> applications) {
    this.applications = applications;
  }

  // TODO freynaud return a copy.
  public synchronized List<WebkitApplication> getApplications() {
    return applications;
  }

  public void switchTo(String pageId) {
    for (WebkitPage p : getPages()) {
      if ((p.getPageId() + "").equals(pageId)) {
        switchTo(p);
        return;
      }
    }
    throw new WebDriverException("no such page " + pageId);
  }

  public void switchTo(WebkitPage page) {
    currentInspector = connect(page);
    inspectors.put(page.getPageId(), currentInspector);
    log.fine("pageId=" + page.getPageId());
  }

  private BaseWebInspector connect(WebkitPage webkitPage) {
    for (WebkitPage page : getPages()) {
      if (page.equals(webkitPage)) {
        protocol.attachToPage(page.getPageId());

        WebInspector inspector = new WebInspector(webkitPage.getPageId(), protocol, session);
        protocol.addListener(inspector);
        return inspector;
      }
    }
    throw new WebDriverException("Cannot connect to page " + webkitPage + ".Cannot find it.");
  }

  public void waitForPageToLoad() {
    //currentInspector.waitForPageToLoad();
  }

  /*public RemoteWebElement getDocument() {
    return currentInspector.getDocument();
  }*/

  public void get(String url) {
    currentInspector.get(url);
  }

  public List<Cookie> getCookies() {
    return currentInspector.getCookies();
  }

  public void deleteCookie(String name, String url) {
    currentInspector.deleteCookie(name, url);
  }

  public String getCurrentUrl() {
      return currentInspector.getCurrentUrl();
  }

  public String getTitle() {
    return currentInspector.getTitle();
  }

  public int getCurrentPageID() {
    if (currentInspector == null) {
      return -1;
    }
    return currentInspector.getPageIdentifier();
  }

  public List<RemoteWebElement> findElements(By by) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public RemoteWebElement findElement(By by) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public RemoteWebElement findElementByCssSelector(String cssSelector) throws Exception {
    return currentInspector.findElementByCSSSelector(cssSelector);
  }

  public List<RemoteWebElement> findElementsByCssSelector(String cssSelector) {
    return currentInspector.findElementsByCSSSelector(cssSelector);
  }

  public String getPageSource() {
    return currentInspector.getPageSource();
  }

  public Dimension getSize() throws Exception {
    return currentInspector.getSize();
  }

  public void close() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void quit() {
    stop();
  }

  public List<WebkitPage> getWindowHandles() {
    return getPages();
  }

  public String getWindowHandle() {
    return "" + currentInspector.getPageIdentifier();
  }

  public int getWindowHandleIndex() {
    int pageId = currentInspector.getPageIdentifier();
    for (WebkitPage p : getPages()) {
      if (p.getPageId() == pageId) {
        return getPages().indexOf(p);
      }
    }
    throw new WebDriverException("Cannot find current page.");
  }

  public int getWindowHandleIndexDifference(String pageId) {
    // first, sort pages.
    List<WebkitPage> pages = getPages();
    int currentIndex = -1;
    for (WebkitPage p : pages) {
      if (p.getPageId() == currentInspector.getPageIdentifier()) {
        currentIndex = pages.indexOf(p);
      }
    }
    int destination = -1;
    for (WebkitPage p : pages) {
      if ((p.getPageId() + "").equals(pageId)) {
        if (p.isITunesAd()) {
          log.warning(
              "Trying to switch to an ad. You will need to be on the correct page already to do "
              + "that.If you're not, random error will occur.");
          return 0;
        } else {
          destination = pages.indexOf(p);
        }
      }
    }
    return destination - currentIndex;
  }

  public WebDriver.TargetLocator switchTo() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void back() throws JSONException {
    currentInspector.back();
  }

  public void forward() {
    try {
      currentInspector.forward();
    } catch (Exception e) {
      log.log(Level.SEVERE,"forward error",e);
    }
  }

  public void refresh() {
    try {
      currentInspector.refresh();
    } catch (Exception e) {
      log.log(Level.SEVERE,"refresh error",e);
    }
  }

  public WebDriver.Options manage() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public Object executeScript(String script, JSONArray args) {
    return currentInspector.executeScript(script, args);
  }

  public Object executeAsyncScript(String script, JSONArray args) {
    return currentInspector.executeAsyncScript(script, args);
  }

  // TODO remove.
  public RemoteWebElement getDocument() {
    return currentInspector.getDocument();
  }

  public boolean isLoading() {
    return !currentInspector.isReady();
  }

  public DOMContext getContext() {
    return currentInspector.getContext();
  }

  public String getLoadedFlag() {
    return currentInspector.getLoadedFlag();
  }

  public synchronized void setDevice(WebkitDevice device) {
    this.device = device;
  }

  public synchronized WebkitDevice getDevice() {
    return device;
  }
}
