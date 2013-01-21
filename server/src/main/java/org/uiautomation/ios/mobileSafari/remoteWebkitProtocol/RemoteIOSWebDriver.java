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

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.uiautomation.ios.context.WebInspector;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.mobileSafari.message.WebkitApplication;
import org.uiautomation.ios.mobileSafari.message.WebkitDevice;
import org.uiautomation.ios.mobileSafari.message.WebkitPage;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;
import org.uiautomation.ios.webInspector.DOM.RemoteWebNativeBackedElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RemoteIOSWebDriver implements JavascriptExecutor {


  public static void main(String[] args) throws Exception {
    RemoteIOSWebDriver driver = new RemoteIOSWebDriver(null);
    driver.connect(uiCatalog);
    driver.switchTo(driver.getPages().get(0));

    //driver.get("http://ebay.co.uk");
    System.out.println("URL : " + driver.getCurrentUrl());
    RemoteWebElement el = driver.findElementByCssSelector("#v4-1");
    System.out.println(el.getText());

  }

  static String safari = "com.apple.mobilesafari";
  static String uiCatalog = "com.yourcompany.UICatalog";
  private SimulatorSession simulator;
  private Object usbProtocol;
  private WebkitDevice device;
  private List<WebkitApplication> applications = new ArrayList<WebkitApplication>();
  private final ServerSideSession session;

  private WebInspector currentInspector;
  private Map<Integer, WebInspector> inspectors = new HashMap<Integer, WebInspector>();

  public RemoteIOSWebDriver(ServerSideSession session) {
    simulator = new SimulatorSession();
    usbProtocol = new Object();
    this.session = session;
  }

  private SimulatorSession getSimulator() {
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


  public void switchTo(WebkitPage page) {
    currentInspector = simulator.connect(page, session);
    inspectors.put(page.getPageId(), currentInspector);
  }

  public void waitForPageToLoad() {

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
    return null;  //To change body of implemented methods use File | Settings | File Templates.
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
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void close() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void quit() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public Set<String> getWindowHandles() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String getWindowHandle() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public WebDriver.TargetLocator switchTo() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public WebDriver.Navigation navigate() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public WebDriver.Options manage() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public Object executeScript(String script, Object... args) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public Object executeAsyncScript(String script, Object... args) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.

  }

  // TODO remove.
  public RemoteWebElement getDocument() {
    return currentInspector.getDocument();
  }
}



