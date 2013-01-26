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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.context.BaseWebInspector;
import org.uiautomation.ios.context.WebInspector;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.mobileSafari.ResponseFinder;
import org.uiautomation.ios.mobileSafari.message.WebkitApplication;
import org.uiautomation.ios.mobileSafari.message.WebkitDevice;
import org.uiautomation.ios.mobileSafari.message.WebkitPage;
import org.uiautomation.ios.server.DOMContext;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.webInspector.DOM.Page;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;
import org.uiautomation.ios.webInspector.DOM.RemoteWebNativeBackedElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteIOSWebDriver {


  private DOMContext context;

  public static void main(String[] args) throws Exception {
    RemoteIOSWebDriver driver = new RemoteIOSWebDriver(null);
    driver.connect(uiCatalog);
    driver.switchTo(driver.getPages().get(0));
    driver.get("http://ebay.co.uk/");

    RemoteWebElement body = driver.findElementByCssSelector("body");
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
  private SimulatorSession simulator;
  private Object usbProtocol;
  private WebkitDevice device;
  private List<WebkitApplication> applications = new ArrayList<WebkitApplication>();
  private final ServerSideSession session;

  private BaseWebInspector currentInspector;
  private Map<Integer, BaseWebInspector> inspectors = new HashMap<Integer, BaseWebInspector>();

  public RemoteIOSWebDriver(ServerSideSession session, ResponseFinder... finders) {
    simulator = new SimulatorSession(finders);
    usbProtocol = new Object();
    this.session = session;
  }

  public void stop() {
    simulator.stop();
  }

  public SimulatorSession getSimulator() {
    return simulator;
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

  public WebInspector connect(String bundleId, String deviceId) {
    return null;
  }

  public void connect(String bundleId) {
    simulator.connect(bundleId);
  }

  public List<WebkitPage> getPages() {
    return simulator.getPages();
  }

  public void switchTo(String pageId) {
    for (WebkitPage p : getPages()) {
      if ((p.getPageId() + "").equals(pageId)) {
        switchTo(p);
      }
    }
    throw new WebDriverException("no such page " + pageId);
  }

  public void switchTo(WebkitPage page) {
    currentInspector = simulator.connect(page, session);
    inspectors.put(page.getPageId(), currentInspector);
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
}



