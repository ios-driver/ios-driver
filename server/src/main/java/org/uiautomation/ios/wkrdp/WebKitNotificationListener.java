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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.drivers.RemoteIOSWebDriver;
import org.uiautomation.ios.wkrdp.internal.WebKitSynchronizer;
import org.uiautomation.ios.wkrdp.message.ApplicationConnectedMessage;
import org.uiautomation.ios.wkrdp.message.ApplicationSentListingMessage;
import org.uiautomation.ios.wkrdp.message.IOSMessage;
import org.uiautomation.ios.wkrdp.message.ReportConnectedApplicationsMessage;
import org.uiautomation.ios.wkrdp.message.ReportSetupMessage;
import org.uiautomation.ios.wkrdp.message.WebkitApplication;
import org.uiautomation.ios.wkrdp.message.WebkitPage;

// TODO freynaud delete the interface ?
public class WebKitNotificationListener implements MessageListener {

  private static final Logger log = Logger.getLogger(WebKitNotificationListener.class.getName());

  private final RemoteIOSWebDriver driver;

  private final ServerSideSession session;

  private final WebKitSynchronizer sync;

  private List<WebkitApplication> registeredApplications;

  private Lock applicationRegistrationLock;

  private Lock pagesProcessLock;

  public WebKitNotificationListener(RemoteIOSWebDriver driver, WebKitSynchronizer syncronizer, ServerSideSession session) {
    this.driver = driver;
    this.session = session;
    this.sync = syncronizer;
    this.registeredApplications = new ArrayList<>();
    this.applicationRegistrationLock = new ReentrantLock();
    this.pagesProcessLock = new ReentrantLock();
  }

  @Override
  public void onMessage(IOSMessage message) {
    handleReportSetupMessage(message);
    handleReportConnectedApplicationsMessage(message);
    handleApplicationSentListingMessage(message);
    handleApplicationDataMessage(message);
    handleApplicationConnectedMessage(message);
  }

  private void handleReportSetupMessage(IOSMessage message) {
    if (!(message instanceof ReportSetupMessage)) {
      return;
    }
    ReportSetupMessage reportSetupMessage = ReportSetupMessage.class.cast(message);
    driver.setDevice(reportSetupMessage.getDevice());
    sync.signalSimRegistered();
  }

  private void handleReportConnectedApplicationsMessage(IOSMessage message) {
    if (!(message instanceof ReportConnectedApplicationsMessage)) {
      return;
    }
    try {
      applicationRegistrationLock.lock();
      ReportConnectedApplicationsMessage reportConnectedApplicationsMessage = ReportConnectedApplicationsMessage.class
          .cast(message);
      registeredApplications.addAll(reportConnectedApplicationsMessage.getApplications());
      if (!isDriverSuccessfullyNotified()) {
        log.log(Level.WARNING,
            "No connectable applications found in ReportConnectedApplicationsMessage will wait before signaling driver");
      }
    } finally {
      applicationRegistrationLock.unlock();
    }
  }

  private void handleApplicationSentListingMessage(IOSMessage message) {
    if (!isValidForProcessing(message)) {
      return;
    }
    try {
      pagesProcessLock.lock();
      ApplicationSentListingMessage applicationSentListingMessage = ApplicationSentListingMessage.class.cast(message);
      boolean pagesSimilar = arePagesNotChanged(applicationSentListingMessage);
      if (log.isLoggable(Level.FINE)) {
        log.fine("pages " + (pagesSimilar ? "are equal " : "have changed ") + ": " + driver.getPages() + "-> "
            + applicationSentListingMessage.getPages() + ": " + applicationSentListingMessage);
      }
      if (pagesSimilar) {
        driver.setPages(applicationSentListingMessage.getPages()); // Update the titles.
        return;
      }
      if (session == null || session.getApplication().isSafari()) {
        processPagesForSafariApp(applicationSentListingMessage);
      } else {
        processPagesForHybridApp(applicationSentListingMessage);
      }
    } finally {
      pagesProcessLock.unlock();
    }
  }

  private void handleApplicationDataMessage(IOSMessage message) {
    // TODO to be implemented 
  }

  private void handleApplicationConnectedMessage(IOSMessage message) {
    if (!(message instanceof ApplicationConnectedMessage)) {
      return;
    }
    try {
      applicationRegistrationLock.lock();
      ApplicationConnectedMessage applicationConnectedMessage = ApplicationConnectedMessage.class.cast(message);
      registeredApplications.add(applicationConnectedMessage.getApplication());
      if (!isDriverSuccessfullyNotified()) {
        log.log(Level.WARNING,
            "No connectable applications found in ApplicationConnectedMessage will wait before signaling driver");
      }
    } finally {
      applicationRegistrationLock.unlock();
    }
  }

  private boolean isDriverSuccessfullyNotified() {
    for (WebkitApplication application : registeredApplications) {
      if (application.isConnectableByWkrdProtocol()) {
        driver.setApplications(registeredApplications);
        sync.signalSimSentApps();
        return true;
      }
    }
    return false;
  }

  private boolean isValidForProcessing(IOSMessage message) {
    if (!(message instanceof ApplicationSentListingMessage)) {
      return false;
    }
    ApplicationSentListingMessage applicationSentListingMessage = ApplicationSentListingMessage.class.cast(message);
    if (!applicationSentListingMessage.isPagesAvailable()) {
      if (log.isLoggable(Level.INFO)) {
        log.log(Level.INFO,
            "ApplicationSentListingMessage '_rpc_applicationSentListing:' received with no pages skipping process");
      }
      return false;
    }
    if (!isConnectableByWkrdProtocol(applicationSentListingMessage.getApplicationIdentifier())) {
      if (log.isLoggable(Level.INFO)) {
        log.log(Level.INFO, "Application id: " + applicationSentListingMessage.getApplicationIdentifier()
            + " not allowed for processing");
      }
      return false;
    }
    return true;
  }

  private boolean isConnectableByWkrdProtocol(String applicationIdentifier) {
    int attempts = 2;
    try {
      return checkForAvailabilityIn(applicationIdentifier, attempts)
          && getApplication(applicationIdentifier).isConnectableByWkrdProtocol();
    } catch (InterruptedException e) {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "InterruptedException occured while checking availability of application in list of registered applications.");
      }
    } catch (IllegalStateException illegalStateException) {
      log.log(Level.WARNING, "Unknown error, application with identifier: " + applicationIdentifier
          + " is not retrievable from the list of registered applications");
    }
    return false;
  }

  private boolean checkForAvailabilityIn(String applicationIdentifier, int times) throws InterruptedException {
    while (times > 0) {
      for (WebkitApplication application : registeredApplications) {
        if (application.getBundleId().equals(applicationIdentifier)) {
          if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Application available in the list of registered applications");
          }
          return true;
        }
      }
      if (--times == 0) {
        break;
      }
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Sleeping for 10 ms in checking availability...");
      }
      Thread.sleep(10);
    }
    return false;
  }

  private WebkitApplication getApplication(String applicationIdentifier) {
    for (WebkitApplication application : registeredApplications) {
      if (applicationIdentifier.equals(application.getBundleId())) {
        return application;
      }
    }
    throw new IllegalStateException("Call getApplication() only if checkForAvailabilityIn() returns true");
  }

  private void processPagesForSafariApp(ApplicationSentListingMessage applicationSentListingMessage) {
    boolean isPagesAdded = isPagesAdded(applicationSentListingMessage);
    if (log.isLoggable(Level.FINE)) {
      log.fine("ApplicationSentListingMessage: message pages count: " + applicationSentListingMessage.getPages().size()
          + ", pages added?: " + isPagesAdded);
    }

    // A new page appeared. The driver needs to know.
    if (isPagesAdded) {
      processAddedPages(applicationSentListingMessage);
    } else {
      processRemovedPages(applicationSentListingMessage);
    }
    sync.signalSimSentPages();
  }

  private boolean arePagesNotChanged(ApplicationSentListingMessage applicationSentListingMessage) {
    return WebkitPage.equals(applicationSentListingMessage.getPages(), driver.getPages());
  }

  private boolean isPagesAdded(ApplicationSentListingMessage applicationSentListingMessage) {
    return applicationSentListingMessage.getPages().size() - driver.getPages().size() > 0;
  }

  private void processAddedPages(ApplicationSentListingMessage applicationSentListingMessage) {
    List<WebkitPage> pages = new ArrayList<WebkitPage>(driver.getPages());

    // Remove all the pages we already know of from the message
    for (WebkitPage p : driver.getPages()) {
      applicationSentListingMessage.getPages().remove(p);
    }

    // TODO there can be more than one 'new' UIWebView, picking the max one for now.
    WebkitPage newOne = Collections.max(applicationSentListingMessage.getPages());
    pages.addAll(applicationSentListingMessage.getPages());
    driver.setPages(pages);
    if (driver.getPages().size() > 0 && !newOne.isITunesAd()) {
      WebkitPage focus = newOne;
      if (session != null) {
        waitForWindowSwitchingAnimation();
        driver.switchTo(focus);
      } else {
        driver.switchTo(focus);
      }
    }
  }

  private void processRemovedPages(ApplicationSentListingMessage applicationSentListingMessage) {
    List<WebkitPage> old = new ArrayList<WebkitPage>(driver.getPages());
    for (WebkitPage p : applicationSentListingMessage.getPages()) {
      old.remove(p);
    }

    // TODO freynaud problem here.How to handle a window disappearing without loosing the state
    // of the currently selected page. driver.getPages() may need to become mutable again.
    // in the normal case, this is the page with the focus that is closed, so loosing the state
    // is ok. But for the extra window automatically closed ( ie the "there is an app for that
    // page, downlaod it on itune" page header, the header disappears when driver.get navigates
    // to a new page. The header disapeearing shouldn't impact the current state.
    for (WebkitPage p : old) {
      log.fine("the page " + p + " has been deleted and must be removed from the driver cache");
      int currentFocus = driver.getCurrentPageID();
      if (p.getPageId() == currentFocus) {
        log.fine("the page deleted is the one with the focus.");
      } else {
        driver.setPages(applicationSentListingMessage.getPages());
      }
    }
  }

  private void processPagesForHybridApp(ApplicationSentListingMessage applicationSentListingMessage) {
    List<WebkitPage> messagePages = applicationSentListingMessage.getPages();
    driver.setPages(messagePages);
    if (messagePages.size() > 0) {
      if (session != null) {
        waitForWindowSwitchingAnimation();
      }
      WebkitPage focus = selectPage(driver.getPages());
      if (focus != null) {
        driver.switchTo(focus);
      }
    }
    sync.signalSimSentPages();
  }

  private WebkitPage selectPage(List<WebkitPage> pages) {
    for (WebkitPage page : pages) {
      if (!page.isITunesAd()) {
        return page;
      }
    }
    return null;
  }

  private void waitForWindowSwitchingAnimation() {
    try {
      Thread.sleep(400);
    } catch (InterruptedException ignore) {
    }
  }

}
