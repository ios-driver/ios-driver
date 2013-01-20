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

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.uiautomation.ios.context.WebInspector;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.mobileSafari.message.WebkitApplication;
import org.uiautomation.ios.mobileSafari.message.WebkitDevice;
import org.uiautomation.ios.mobileSafari.message.WebkitPage;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.webInspector.DOM.Page;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RemoteIOSWebDriver implements WebDriver, JavascriptExecutor {


  public static void main(String[] args) {
    RemoteIOSWebDriver driver = new RemoteIOSWebDriver(null);
    driver.connect(uiCatalog);
    driver.switchTo(driver.getPages().get(0));

    System.out.println("URL : " + driver.getCurrentUrl());
    driver.get("http://ebay.co.uk");
    System.out.println("URL : " + driver.getCurrentUrl());
    driver.get("http://apple.com/uk");
    System.out.println("URL : " + driver.getCurrentUrl());
    driver.get("http://perdu.com");


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
    String pageId = reference.split("_")[0];
    int nodeId = Integer.parseInt(reference.split("_")[1]);
    WebInspector inspector = inspectors.get(pageId);
    RemoteWebElement element = new RemoteWebElement(new NodeId(nodeId), inspector);

    return element;
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

  public RemoteWebElement getDocument() {
    return currentInspector.getDocument();
  }

  @Override
  public void get(String url) {
    currentInspector.get(url);
  }

  @Override
  public String getCurrentUrl() {
    try {
      return currentInspector
          .getCurrentUrl();  //To change body of implemented methods use File | Settings | File Templates.
    } catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      return null;
    }
  }

  @Override
  public String getTitle() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public List<WebElement> findElements(By by) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public WebElement findElement(By by) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public String getPageSource() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void close() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void quit() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Set<String> getWindowHandles() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public String getWindowHandle() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public TargetLocator switchTo() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Navigation navigate() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Options manage() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Object executeScript(String script, Object... args) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Object executeAsyncScript(String script, Object... args) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


}



