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
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.context.BaseWebInspector;
import org.uiautomation.ios.context.WebInspector;
import org.uiautomation.ios.mobileSafari.EventListener;
import org.uiautomation.ios.mobileSafari.ResponseFinder;
import org.uiautomation.ios.mobileSafari.SimulatorProtocolImpl;
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
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.webInspector.DOM.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimulatorSession {

  private SimulatorProtocolImpl simulatorProtocol;
  private WebkitDevice device;
  private List<WebkitApplication> applications;
  private List<WebkitPage> pages = new ArrayList<WebkitPage>();

  private String bundleId;
  private Lock simLock = new ReentrantLock();
  private Condition simRegistered = simLock.newCondition();
  private Condition simSentApps = simLock.newCondition();
  private Condition simSentPages = simLock.newCondition();

  private final List<WebInspector> created = new ArrayList<WebInspector>();

  private final String connectionKey;

  public SimulatorSession(ResponseFinder... finders) {
    connectionKey = UUID.randomUUID().toString();
    simulatorProtocol = new SimulatorProtocolImpl(new DefaultMessageListener(this), finders);
    simulatorProtocol.sendSetConnectionKey(connectionKey);
    waitForSimToRegister();
    waitForSimToSendApps();
  }

  public void stop() {
    simulatorProtocol.stop();
  }


  public void connect(String bundleId) {
    List<WebkitApplication> knownApps = getApplications();
    for (WebkitApplication app : knownApps) {
      if (bundleId.equals(app.getBundleId())) {
        this.bundleId = bundleId;
        simulatorProtocol.sendConnectToApplication(connectionKey, bundleId);
        waitForSimToSendPages();
        return;
      }
    }
    throw new WebDriverException(bundleId + " not in the list " + knownApps
                                 + ".Either it's not started, or it has no webview to connect to.");
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

  public boolean isConnected() {
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
      if (applications != null) {
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

  public synchronized void setDevice(WebkitDevice device) {
    this.device = device;
  }

  public synchronized WebkitDevice getDevice() {
    return device;
  }

  public synchronized void setApplications(List<WebkitApplication> applications) {
    this.applications = applications;
  }

  // TODO freynaud return a copy.
  public synchronized List<WebkitApplication> getApplications() {
    return applications;
  }

  public void setPages(List<WebkitPage> pages) {
    this.pages = pages;
  }

  public List<WebkitPage> getPages() {
    return pages;
  }

  public BaseWebInspector connect(WebkitPage webkitPage, ServerSideSession session) {
    for (WebkitPage page : getPages()) {
      if (page.equals(webkitPage)) {
        WebInspector
            inspector =
            new WebInspector(null, webkitPage.getPageId(), simulatorProtocol, bundleId,
                             connectionKey, session);
        // TODO move to webinspector
        simulatorProtocol.sendSetConnectionKey(connectionKey);
        simulatorProtocol.sendConnectToApplication(connectionKey, bundleId);
        simulatorProtocol.sendSenderKey(connectionKey, bundleId, inspector.getSenderKey(),
                                        "" + page.getPageId());
        inspector.sendCommand(Page.enablePageEvent());

        boolean ok = created.add(inspector);
        if (ok) {
          simulatorProtocol.addListener(inspector);
        }
        return inspector;
      }
    }
    throw new WebDriverException("Cannot connect to page " + webkitPage + ".Cannot find it.");
  }
}


class DefaultMessageListener implements MessageListener, EventListener {

  private final SimulatorSession simulator;

  public DefaultMessageListener(SimulatorSession simulator) {
    this.simulator = simulator;
  }


  @Override
  public void onMessage(IOSMessage message) {
    if (message instanceof ReportSetupMessage) {
      ReportSetupMessage m = (ReportSetupMessage) message;
      simulator.setDevice(m.getDevice());
      simulator.signalSimRegistered();
    }

    if (message instanceof ReportConnectedApplicationsMessage) {
      ReportConnectedApplicationsMessage m = (ReportConnectedApplicationsMessage) message;
      simulator.setApplications(m.getApplications());
      simulator.signalSimSentApps();
    }

    if (message instanceof ApplicationSentListingMessage) {
      ApplicationSentListingMessage m = (ApplicationSentListingMessage) message;
      simulator.setPages(m.getPages());
      simulator.signalSimSentPages();
      //
    }

    if (message instanceof ApplicationDataMessage) {
      //System.out.println(message);
    }

    if (message instanceof ApplicationConnectedMessage) {
      ApplicationConnectedMessage m = (ApplicationConnectedMessage) message;
      List<WebkitApplication> apps = new ArrayList<WebkitApplication>();
      apps.add(m.getApplication());
      simulator.setApplications(apps);
      simulator.signalSimSentApps();
    }

    //System.err.println(message);
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