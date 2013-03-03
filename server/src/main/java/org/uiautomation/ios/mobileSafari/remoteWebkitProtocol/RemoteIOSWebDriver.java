/*
 * Copyright 2012 ios-driver committers.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the Licence at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */

package org.uiautomation.ios.mobileSafari.remoteWebkitProtocol;

import com.google.common.collect.ImmutableList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.context.BaseWebInspector;
import org.uiautomation.ios.context.WebInspector;
import org.uiautomation.ios.mobileSafari.EventListener;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.mobileSafari.ResponseFinder;
import org.uiautomation.ios.mobileSafari.events.Event;
import org.uiautomation.ios.mobileSafari.message.ApplicationConnectedMessage;
import org.uiautomation.ios.mobileSafari.message.ApplicationDataMessage;
import org.uiautomation.ios.mobileSafari.message.ApplicationSentListingMessage;
import org.uiautomation.ios.mobileSafari.message.IOSMessage;
import org.uiautomation.ios.mobileSafari.message.ReportConnectedApplicationsMessage;
import org.uiautomation.ios.mobileSafari.message.ReportSetupMessage;
import org.uiautomation.ios.mobileSafari.message.WebkitApplication;
import org.uiautomation.ios.mobileSafari.message.WebkitDevice;
import org.uiautomation.ios.mobileSafari.message.WebkitPage;
import org.uiautomation.ios.server.DOMContext;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.configuration.Configuration;
import org.uiautomation.ios.server.instruments.InstrumentsManager;
import org.uiautomation.ios.webInspector.DOM.Page;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;
import org.uiautomation.ios.webInspector.DOM.RemoteWebNativeBackedElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class RemoteIOSWebDriver {


  public static void main(String[] args) throws Exception {
    RemoteIOSWebDriver driver = new RemoteIOSWebDriver(null);
    //driver.connect(uiCatalog);
    driver.switchTo(driver.getPages().get(0));
    driver.get("http://ebay.co.uk/");
    RemoteWebElement body = driver.findElementByCssSelector("body");
    System.out.println(body.getText());
    driver.get("http://google.co.uk/");
    body = driver.findElementByCssSelector("body");
    System.out.println(body.getText());

    driver.stop();

    driver = new RemoteIOSWebDriver(null);
    //driver.connect(uiCatalog);
    driver.switchTo(driver.getPages().get(0));
    driver.get("http://ebay.co.uk/");
    body = driver.findElementByCssSelector("body");
    System.out.println(body.getText());
    driver.get("http://google.co.uk/");
    body = driver.findElementByCssSelector("body");
    System.out.println(body.getText());


    /*driver.get("http://ebay.co.uk/common/frameset.html");

    JSONObject res = driver.currentInspector.sendCommand(Page.getResourceTree());
    System.out.println(res.toString(2));
    for (int i = 1; i < 10; i++) {
      long start = System.currentTimeMillis();
      driver.get("http://localhost:45188/common/sleep?time=" + i);
      System.out.println(
          "get duration for a page taking " + i + " sec " + (System.currentTimeMillis() - start)
          + " ms.");
      RemoteWebElement body = driver.findElementByCssSelector("body");
      //System.out.println(body.getText());
    } */
  }

  static String safari = "com.apple.mobilesafari";
  static String uiCatalog = "com.yourcompany.UICatalog";

  //private SimulatorSession simulator;
  //private Object usbProtocol;
  private String bundleId;
  private Lock simLock = new ReentrantLock();
  private Condition simRegistered = simLock.newCondition();
  private Condition simSentApps = simLock.newCondition();
  private Condition simSentPages = simLock.newCondition();
  private WebKitRemoteDebugProtocol protocol;
  private WebkitDevice device;
  private List<WebkitApplication> applications = new ArrayList<WebkitApplication>();
  private final ServerSideSession session;
  private final String connectionKey;
  private BaseWebInspector currentInspector;
  private Map<Integer, BaseWebInspector> inspectors = new HashMap<Integer, BaseWebInspector>();
  private static final Logger log = Logger.getLogger(RemoteIOSWebDriver.class.getName());
  private List<WebkitPage> pages = new ArrayList<WebkitPage>();
  private final List<WebInspector> created = new ArrayList<WebInspector>();


  public RemoteIOSWebDriver(ServerSideSession session, ResponseFinder... finders) {
    this.session = session;
    connectionKey = UUID.randomUUID().toString();
    if (InstrumentsManager.realDevice) {
      if (!Configuration.BETA_FEATURE) {
        Configuration.off();
      }
      protocol =
          new RealDeviceProtocolImpl(new DefaultMessageListener(this, session), finders);

    } else {
      protocol =
          new SimulatorProtocolImpl(new DefaultMessageListener(this, session), finders);

    }
    protocol.register();
    waitForSimToRegister();
    waitForSimToSendApps();

    if (applications.size() == 1) {
      connect(applications.get(0).getBundleId());
    } else {
      log.warning("session created but application size=" + applications.size());
    }
  }


  private void waitForSimToSendPages() {
    try {
      simLock.lock();
      if (pages != null && pages.size() > 0) {
        return;
      }
      simSentPages.await(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new WebDriverException("InterruptedException before getting the pages.");
    } finally {
      simLock.unlock();
    }
  }

  public void setPages(List<WebkitPage> pages) {
    this.pages = ImmutableList.copyOf(pages);
  }

  public List<WebkitPage> getPages() {
    return pages;
  }

  public boolean isConnected() {
    log.fine("Applications  " + applications.size());
    return !applications.isEmpty();
  }

  private void waitForSimToRegister() {
    try {
      simLock.lock();
      if (device != null) {
        return;
      }
      simRegistered.await(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new WebDriverException(
          "InterruptedException while waiting for the simulator to respond.");
    } finally {
      simLock.unlock();
    }
  }

  void signalSimSentPages() {
    try {
      simLock.lock();
      simSentPages.signal();
    } finally {
      simLock.unlock();
    }
  }

  void signalSimRegistered() {
    try {
      simLock.lock();
      simRegistered.signal();
    } finally {
      simLock.unlock();
    }
  }

  private void waitForSimToSendApps() {

    try {
      simLock.lock();
      if (applications != null && !applications.isEmpty()) {
        return;
      }
      simSentApps.await(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new WebDriverException(
          "InterruptedException while waiting for the simulator to send its apps.");
    } finally {
      simLock.unlock();
    }
  }

  void signalSimSentApps() {
    try {
      simLock.lock();
      simSentApps.signal();
    } finally {
      simLock.unlock();
    }
  }

  public void start() {
    protocol.start();
  }


  public void stop() {
    protocol.stop();
  }


  public RemoteWebElement createElement(String reference) {
    int pageId = Integer.parseInt(reference.split("_")[0]);
    int nodeId = Integer.parseInt(reference.split("_")[1]);

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
        waitForSimToSendPages();
        switchTo(getPages().get(0));
        System.out.println("currentInspector:" + currentInspector);
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
    currentInspector = connect(page);
    inspectors.put(page.getPageId(), currentInspector);
  }

  private BaseWebInspector connect(WebkitPage webkitPage) {
    for (WebkitPage page : getPages()) {
      if (page.equals(webkitPage)) {
        WebInspector
            inspector =
            new WebInspector(null, webkitPage.getPageId(), protocol, bundleId,
                             connectionKey, session);
        // TODO move to webinspector
        //simulatorProtocol.register();
        //simulatorProtocol.connect(bundleId);
        protocol.attachToPage(page.getPageId());
        // inspector.sendCommand(Runtime.evaluate("alert(ttt123)"));
        inspector.sendCommand(Page.enablePageEvent());

        boolean ok = created.add(inspector);
        if (ok) {
          protocol.addListener(inspector);
        }
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

  private void reset() {
    try {
      System.out.println("RESET");
      this.stop();
      RemoteIOSWebDriver driver = new RemoteIOSWebDriver(null);
      //driver.connect(uiCatalog);
      driver.switchTo(driver.getPages().get(0));
      driver.get("http://ebay.co.uk/");
      RemoteWebElement body = driver.findElementByCssSelector("body");
      System.out.println(body.getText());
      driver.get("http://google.co.uk/");
      body = driver.findElementByCssSelector("body");
      System.out.println(body.getText());

    } catch (Exception e) {
      e.printStackTrace();
    }
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

  public void close() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void quit() {
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
        destination = pages.indexOf(p);
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


class DefaultMessageListener implements MessageListener, EventListener {

  private static final Logger log = Logger.getLogger(DefaultMessageListener.class.getName());

  private final RemoteIOSWebDriver driver;
  private final ServerSideSession session;

  public DefaultMessageListener(RemoteIOSWebDriver driver, ServerSideSession session) {
    this.driver = driver;
    this.session = session;
  }


  @Override
  public void onMessage(IOSMessage message) {

    if (message instanceof ReportSetupMessage) {
      ReportSetupMessage m = (ReportSetupMessage) message;
      driver.setDevice(m.getDevice());
      driver.signalSimRegistered();
    }

    if (message instanceof ReportConnectedApplicationsMessage) {
      ReportConnectedApplicationsMessage m = (ReportConnectedApplicationsMessage) message;
      if (m.getApplications().size() == 0) {
        log.warning("ReportConnectedApplicationsMessage reported 0 app.");
      } else {
        driver.setApplications(m.getApplications());
        driver.signalSimSentApps();
      }

    }

    if (message instanceof ApplicationSentListingMessage) {
      ApplicationSentListingMessage m = (ApplicationSentListingMessage) message;
      int change = m.getPages().size() - driver.getPages().size();
      log.info("ApplicationSentListingMessage: message pages: " + m.getPages().size() + ", change: "
               + change);

      if (change != 0) {
        List<WebkitPage> pages = new ArrayList<WebkitPage>();
        pages.addAll(driver.getPages());
        for (WebkitPage p : driver.getPages()) {
          m.getPages().remove(p);
        }
        if (m.getPages().size() == 0) {
          throw new WebDriverException(m.getPages().size() + " new pages.");
        }
        // TODO there can be more than one 'new' UIWebView, picking the first one for now.
        WebkitPage newOne = m.getPages().get(0);

        int
            index =
            driver.getPages().size() == 0 ? 0
                                          : session.getRemoteWebDriver().getWindowHandleIndex()
                                            + 1;
        pages.add(index, newOne);

        driver.setPages(pages);
        driver.signalSimSentPages();

        if (driver.getPages().size() == 0) {
          //log.fine("first page. Nothing to do.");
        } else {
          WebkitPage focus = newOne;

          if (session != null) {
            waitForWindowSwitchingAnimation();
            driver.switchTo(focus);
          } else {
            driver.switchTo(focus);
          }
        }

      }

    }

    if (message instanceof ApplicationDataMessage) {
      //System.out.println(message);
    }

    if (message instanceof ApplicationConnectedMessage) {
      ApplicationConnectedMessage m = (ApplicationConnectedMessage) message;
      List<WebkitApplication> apps = new ArrayList<WebkitApplication>();
      System.out.println("message apps : " + m.getApplication());
      apps.add(m.getApplication());
      driver.setApplications(apps);
      driver.signalSimSentApps();
    }

    //System.err.println(message);
  }


  private void waitForWindowSwitchingAnimation() {
    try {
      Thread.sleep(400);
    } catch (InterruptedException ignore) {
    }
  }

  @Override
  public void onPageLoad() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void domHasChanged(Event event) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void frameDied(JSONObject message) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void setWindowHandles(List<WebkitPage> handles) {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
