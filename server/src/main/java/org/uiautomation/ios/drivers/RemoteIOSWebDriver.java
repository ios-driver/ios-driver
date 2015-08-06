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

import java.lang.Thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.uiautomation.ios.wkrdp.message.WebkitApplication;
import org.uiautomation.ios.wkrdp.message.WebkitDevice;
import org.uiautomation.ios.wkrdp.message.WebkitPage;
import org.uiautomation.ios.wkrdp.model.NodeId;
import org.uiautomation.ios.wkrdp.model.RemoteWebElement;
import org.uiautomation.ios.wkrdp.model.RemoteWebNativeBackedElement;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Monitor;

public class RemoteIOSWebDriver {

  private final WebKitRemoteDebugProtocol protocol;
  private WebkitDevice device = null;
  private ImmutableList<WebkitApplication> applications = ImmutableList.of();
  private final ServerSideSession session;
  private final String connectionKey;
  private BaseWebInspector currentInspector;
  private final Map<Integer, BaseWebInspector> inspectors = new HashMap<>();
  private static final Logger log = Logger.getLogger(RemoteIOSWebDriver.class.getName());
  private ImmutableList<WebkitPage> pages = ImmutableList.of();
  private boolean isStarted = false;
  private final Monitor monitor = new Monitor();
  private final Monitor.Guard deviceGuard =  new Monitor.Guard(monitor) {
    public boolean isSatisfied() { return device != null; }
  };
  private final Monitor.Guard targetApplicationGuard =  new Monitor.Guard(monitor) {
    public boolean isSatisfied() {
      if (applications.isEmpty()) { return false; }
      String target = session.getCapabilities().getBundleId();
      if (target == null) { return true; }
      for (WebkitApplication app : applications) {
        if (target.equals(app.getBundleId())) { return true; }
      }
      return false;
    }
  };
  private final Monitor.Guard pagesGuard =  new Monitor.Guard(monitor) {
    public boolean isSatisfied() { return !pages.isEmpty(); }
  };

  public RemoteIOSWebDriver(ServerSideSession session, WebKitRemoteDebugProtocol protocol) {
    this.session = session;
    connectionKey = UUID.randomUUID().toString();
    this.protocol = protocol;
    MessageListener messageListener = new WebKitNotificationListener(this, session);
    protocol.addListener(messageListener);
  }

  public void setPages(List<WebkitPage> pages) {
    monitor.enter();
    this.pages = ImmutableList.copyOf(pages);
    monitor.leave();
  }

  public ImmutableList<WebkitPage> getPages() {
    return pages;
  }

  public ImmutableList<WebkitPage> waitForPages() {
    return waitForPages(15, TimeUnit.SECONDS);
  }

  public ImmutableList<WebkitPage> waitForPages(int timeInUnits, TimeUnit unit) {
    try {
      boolean success = monitor.enterWhen(pagesGuard, timeInUnits, unit);
      if (!success) {
        throw new WebDriverException("Timeout waiting for device");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new WebDriverException("Unable to get pages", e);
    }
    try {
      return pages;
    } finally {
      monitor.leave();
    }
  }

  public boolean hasPages() {
    return !pages.isEmpty();
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
    waitForDevice();
    List<WebkitApplication> loadedApps = waitForTargetApplicationList();
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "connectionKey=" + connectionKey);
    }
    for (WebkitApplication application : loadedApps) {
      if (application.isConnectableByWkrdProtocol()) {
        connect(application.getBundleId());
        isStarted = true;
        break;
      }
    }
    if (!isStarted) {
      showWarning(loadedApps);
    }
  }

  private void showWarning(List<WebkitApplication> apps) {
    // Safari.
    int numApps = apps.size();
    if (session.getApplication().isSafari()) {
      if (numApps == 0) {
        if (protocol instanceof RealDeviceProtocolImpl) {
          throw new WebDriverException("is Safari started and with the focus ? ");
        } else {
          // kill left over.
          ClassicCommands.killall("xpcproxy_sim");
          throw new WebDriverException("session created but application size=" + numApps
                                       + ".The simulator wasn't closed properly.Try restarting your computer.");
        }
      } else {
        throw new WebDriverException("session created but application size=" + numApps
                                     + ".It should be 1. Do you have multiple tabs opened ?");
      }
      // Native app
    } else {
      log.warning("session created but application size=" + numApps
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
    List<WebkitApplication> knownApps = waitForTargetApplicationList();
    for (WebkitApplication app : knownApps) {
      if (bundleId.equals(app.getBundleId())) {
        protocol.connect(bundleId);
        log.fine("bundleId=" + bundleId);
        List<WebkitPage> loadedPages = waitForPages();
        if (loadedPages.size() > 0) {
          switchTo(Collections.max(loadedPages));

          if (loadedPages.size() > 1) {
            log.warning("Application started, but already have " + loadedPages.size()
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

  public void setApplications(List<WebkitApplication> applications) {
    monitor.enter();
    this.applications = ImmutableList.copyOf(applications);
    monitor.leave();
  }

  public ImmutableList<WebkitApplication> waitForTargetApplicationList() {
    return waitForTargetApplicationList(10, TimeUnit.SECONDS);
  }

  public ImmutableList<WebkitApplication> waitForTargetApplicationList(int timeInUnits, TimeUnit unit) {
    try {
      boolean success = monitor.enterWhen(targetApplicationGuard, timeInUnits, unit);
      if (!success) {
        throw new WebDriverException("Timeout waiting for target application");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new WebDriverException("Unable to get target application", e);
    }
    try {
      return applications;
    } finally {
      monitor.leave();
    }
  }

  public void switchTo(String pageId) {
    for (WebkitPage p : waitForPages()) {
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
    for (WebkitPage page : waitForPages()) {
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
    return waitForPages();
  }

  public String getWindowHandle() {
    return "" + currentInspector.getPageIdentifier();
  }

  public int getWindowHandleIndex() {
    int pageId = currentInspector.getPageIdentifier();
    List<WebkitPage> loadedPages = waitForPages();
    for (WebkitPage p : loadedPages) {
      if (p.getPageId() == pageId) {
        return loadedPages.indexOf(p);
      }
    }
    throw new WebDriverException("Cannot find current page.");
  }

  public int getWindowHandleIndexDifference(String pageId) {
    // first, sort pages.
    List<WebkitPage> pages = waitForPages();
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

  public void setDevice(WebkitDevice device) {
    monitor.enter();
    this.device = device;
    monitor.leave();
  }

  public WebkitDevice waitForDevice() {
    return waitForDevice(10, TimeUnit.SECONDS);
  }

  public WebkitDevice waitForDevice(int timeInUnits, TimeUnit unit) {
    try {
      boolean success = monitor.enterWhen(deviceGuard, timeInUnits, unit);
      if (!success) {
        throw new WebDriverException("Timeout waiting for device");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new WebDriverException("Unable to get pages", e);
    }
    try {
      return device;
    } finally {
      monitor.leave();
    }
  }
}