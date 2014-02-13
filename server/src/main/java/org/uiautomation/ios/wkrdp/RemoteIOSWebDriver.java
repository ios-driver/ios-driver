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

package org.uiautomation.ios.wkrdp;

import com.google.common.collect.ImmutableList;

import org.json.JSONArray;
import org.json.JSONException;
import org.libimobiledevice.ios.driver.binding.LibImobileDeviceWrapperFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.context.BaseWebInspector;
import org.uiautomation.ios.context.WebInspector;
import org.uiautomation.ios.server.DOMContext;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.RealDevice;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.configuration.Configuration;
import org.uiautomation.ios.wkrdp.internal.RealDeviceProtocolImpl;
import org.uiautomation.ios.wkrdp.internal.SimulatorProtocolImpl;
import org.uiautomation.ios.wkrdp.internal.WebKitRemoteDebugProtocol;
import org.uiautomation.ios.wkrdp.internal.WebKitSynchronizer;
import org.uiautomation.ios.wkrdp.message.WebkitApplication;
import org.uiautomation.ios.wkrdp.message.WebkitDevice;
import org.uiautomation.ios.wkrdp.message.WebkitPage;
import org.uiautomation.ios.wkrdp.model.NodeId;
import org.uiautomation.ios.wkrdp.model.RemoteWebElement;
import org.uiautomation.ios.wkrdp.model.RemoteWebNativeBackedElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class RemoteIOSWebDriver {

  public static boolean TMP = true;

  private LibImobileDeviceWrapperFactory factory = LibImobileDeviceWrapperFactory.INSTANCE;

  public static void main(String[] args) throws Exception {
    try {
      LogManager.getLogManager()
          .readConfiguration(IOSServerManager.class.getResourceAsStream("/ios-logging.properties"));
    } catch (Exception e) {
      System.err.println("Cannot configure logger.");
    }

    Runnable r = new Runnable() {
      @Override
      public void run() {
        RemoteIOSWebDriver driver = new RemoteIOSWebDriver(null);
        driver.connect(safari);
        driver.switchTo(driver.getPages().get(0));
        driver.get("http://perdu.com");
        RemoteWebElement body = null;
        try {
          body = driver.findElementByCssSelector("body");
          System.out.println(body.getText());
        } catch (Exception e) {
          e.printStackTrace();
        }
        driver.quit();
      }
    };

    new Thread(r).start();
    //new Thread(r).start();

//    driver.get("http://google.co.uk/");
//    body = driver.findElementByCssSelector("body");
//
//    driver.stop();
//
//    driver = new RemoteIOSWebDriver(null);
//    //driver.connect(uiCatalog);
//    driver.switchTo(driver.getPages().get(0));
//    driver.get("http://ebay.co.uk/");
//    body = driver.findElementByCssSelector("body");
//    driver.get("http://google.co.uk/");
//    body = driver.findElementByCssSelector("body");
  }

  static String safari = "com.apple.mobilesafari";
  static String uiCatalog = "com.yourcompany.UICatalog";

  //private SimulatorSession simulator;
  //private Object usbProtocol;
  private String bundleId;

  private WebKitRemoteDebugProtocol protocol;
  private WebkitDevice device;
  private List<WebkitApplication> applications = new ArrayList<WebkitApplication>();
  private final ServerSideSession session;
  private final String connectionKey;
  private BaseWebInspector currentInspector;
  private Map<Integer, BaseWebInspector> inspectors = new HashMap<Integer, BaseWebInspector>();
  private static final Logger log = Logger.getLogger(RemoteIOSWebDriver.class.getName());
  private List<WebkitPage> pages = new ArrayList<WebkitPage>();
  private final WebKitSynchronizer sync;
  private static boolean ok = true;

  public RemoteIOSWebDriver(ServerSideSession session, ResponseFinder... finders) {
    this.session = session;
    connectionKey = UUID.randomUUID().toString();
    sync = new WebKitSynchronizer(this);
    MessageListener notification = new WebKitNotificationListener(this, sync, session);
    if (session != null && session.getDevice() instanceof RealDevice) {
      if (!Configuration.BETA_FEATURE) {
        Configuration.off();
      }
      String uuid = ((RealDevice) session.getDevice()).getUuid();
      protocol = new RealDeviceProtocolImpl(uuid, notification, finders);

    } else {
      protocol = new SimulatorProtocolImpl(notification, finders);

    }



    //protocol = new RealDeviceProtocolImpl("ff4827346ed6b54a98f51e69a261a140ae2bf6b3", notification, finders);
    if (session != null) {
      session.getLogManager().onProtocolCreated(protocol);
    }

    protocol.register();
    sync.waitForSimToRegister();
    sync.waitForSimToSendApps();

    log.fine("connectionKey=" + connectionKey);

    if (applications.size() == 1) {
      connect(applications.get(0).getBundleId());
    } else {
      log.warning("session created but application size=" + applications.size());
    }


    Response r = session.getCachedCapabilityResponse();
    if (r == null){
      r = new Response();
      r.setSessionId(session.getSessionId());
      Map<String, Object> o = new HashMap<>();
      List<String> ls = session.getApplication().getSupportedLanguagesCodes();

      o.put("supportedLocales", ls);
      o.put("takesScreenshot", true);
      o.put(IOSCapabilities.CONFIGURABLE, true);
      o.put(IOSCapabilities.ELEMENT_TREE, true);
      o.put(IOSCapabilities.IOS_SEARCH_CONTEXT, true);
      o.put(IOSCapabilities.IOS_TOUCH_SCREEN, true);

      o.put("rotatable", true);
      o.put("locationContextEnabled", true);

      o.put("browserName", session.getCapabilities().getBundleName());
      o.put("browserVersion", session.getApplication().getCapabilities().getBundleVersion());

      o.put("platform", "IOS");
      o.put("platformName", "IOS");
      o.put("platformVersion", session.getCapabilities().getSDKVersion());

      o.put("javascriptEnabled", true);
      o.put("cssSelectors", true);
      o.put("takesElementScreenshot", false);

      o.put(IOSCapabilities.SIMULATOR, false);
      o.put(IOSCapabilities.DEVICE, session.getCapabilities().getDevice());
      o.put(IOSCapabilities.VARIATION, session.getCapabilities().getDeviceVariation());
      r.setValue(o);
      session.setCapabilityCachedResponse(r);
    }
  }

  public void setPages(List<WebkitPage> pages) {
    this.pages = ImmutableList.copyOf(pages);
  }

  public List<WebkitPage> getPages() {
    return pages;
  }

  /*public boolean isConnected() {
    log.fine("Applications  " + applications.size());
    return !applications.isEmpty();
  }*/

  public void start() {
    protocol.start();
  }

  public void stop() {
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
        switchTo(getPages().get(0));
        if (getPages().size() > 1) {
          log.warning("Application started, but already have " + getPages().size()
                      + " webviews. Connecting to the first one.");
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
    log.warning("connecting to " + page.asJSON());
    currentInspector = connect(page);
    inspectors.put(page.getPageId(), currentInspector);
    log.fine("pageId=" + page.getPageId());
  }

  private BaseWebInspector connect(WebkitPage webkitPage) {
    for (WebkitPage page : getPages()) {
      if (page.equals(webkitPage)) {
        protocol.attachToPage(page.getPageId());

        WebInspector inspector = new WebInspector(
            null, webkitPage.getPageId(), protocol, bundleId, connectionKey, session);
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
    try {
      return currentInspector
          .getCurrentUrl();  //To change body of implemented methods use File | Settings | File Templates.
    } catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      return null;
    }
  }

  public String getTitle() {
    return currentInspector.getTitle();
  }

  public int getCurrentPageID() {
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
    //To change body of implemented methods use File | Settings | File Templates.
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
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  public void refresh() {
    try {
      currentInspector.refresh();
    } catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  public WebDriver.Options manage() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public Object executeScript(String script, JSONArray args) {
    return currentInspector.executeScript(script, args);
  }

  public Object executeAsyncScript(String script, Object... args) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
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
