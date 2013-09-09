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

import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.wkrdp.internal.WebKitSyncronizer;
import org.uiautomation.ios.wkrdp.message.ApplicationConnectedMessage;
import org.uiautomation.ios.wkrdp.message.ApplicationDataMessage;
import org.uiautomation.ios.wkrdp.message.ApplicationSentListingMessage;
import org.uiautomation.ios.wkrdp.message.IOSMessage;
import org.uiautomation.ios.wkrdp.message.ReportConnectedApplicationsMessage;
import org.uiautomation.ios.wkrdp.message.ReportSetupMessage;
import org.uiautomation.ios.wkrdp.message.WebkitApplication;
import org.uiautomation.ios.wkrdp.message.WebkitPage;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

// TODO freynaud delete the interface ?
public class WebKitNotificationListener implements MessageListener {

  private static final Logger log = Logger.getLogger(WebKitNotificationListener.class.getName());
  private final RemoteIOSWebDriver driver;
  private final ServerSideSession session;
  private final WebKitSyncronizer sync;

  public WebKitNotificationListener(RemoteIOSWebDriver driver, WebKitSyncronizer syncronizer,
                                    ServerSideSession session) {
    this.driver = driver;
    this.session = session;
    this.sync = syncronizer;
  }

  @Override
  public void onMessage(IOSMessage message) {
    if (message instanceof ReportSetupMessage) {
      ReportSetupMessage m = (ReportSetupMessage) message;
      driver.setDevice(m.getDevice());
      sync.signalSimRegistered();
    }

    if (message instanceof ReportConnectedApplicationsMessage) {
      ReportConnectedApplicationsMessage m = (ReportConnectedApplicationsMessage) message;
      if (m.getApplications().size() == 0) {
        log.warning("ReportConnectedApplicationsMessage reported 0 app.");
      } else {
        driver.setApplications(m.getApplications());
        sync.signalSimSentApps();
      }

    }

    if (message instanceof ApplicationSentListingMessage) {
      ApplicationSentListingMessage m = (ApplicationSentListingMessage) message;

      List<WebkitPage> messagePages = m.getPages();
      List<WebkitPage> driverPages = driver.getPages();
      boolean equals = WebkitPage.equals(messagePages, driverPages);
      log.fine(
          "pages " + (equals ? "equals" : "CHANGED") + ": " + driverPages + " -> " + messagePages
          + ": " + m);
      if (equals) {
        return;
      }

      if (session.getApplication().isSafari()) {
        int change = m.getPages().size() - driver.getPages().size();
        log.fine(
            "ApplicationSentListingMessage: message pages: " + m.getPages().size() + ", change: "
            + change);

        // a new page appeared. The driver needs to know.
        if (change > 0) {
          List<WebkitPage> pages = new ArrayList<WebkitPage>();
          // keep all the pages currently known to the driver
          pages.addAll(driver.getPages());

          // remove all the pages we already know of from the message
          for (WebkitPage p : driver.getPages()) {
            m.getPages().remove(p);
          }

          if (m.getPages().size() == 0) {
            throw new WebDriverException(m.getPages().size() + " new pages.");
          }
          // TODO there can be more than one 'new' UIWebView, picking the first one for now.
          WebkitPage newOne = m.getPages().get(0);

          int index =
              driver.getPages().size() == 0 ? 0
                                            :
              session.getDualDriver().getRemoteWebDriver().getWindowHandleIndex()
              + 1;
          pages.add(index, newOne);

          driver.setPages(pages);

          if (driver.getPages().size() == 0) {
            //log.fine("first page. Nothing to do.");
          } else if (newOne.isITunesAd()) {
            //log.fine("itunes ad - ignoring it.");
          } else {
            WebkitPage focus = newOne;

            if (session != null) {
              waitForWindowSwitchingAnimation();
              driver.switchTo(focus);
            } else {
              driver.switchTo(focus);
            }
          }
          // a page disappeared, the driver needs to know.
        } else if (change < 0) {
          List<WebkitPage> old = new ArrayList<WebkitPage>();
          old.addAll(driver.getPages());

          for (WebkitPage p : m.getPages()) {
            old.remove(p);
          }

          // TODO freynaud problem here.How to handle a window disappearing without loosing the state
          // of the currently selected page. driver.getPages() may need to become mutable again.
          // in the normal case, this is the page with the focus that is closed, so loosing the state
          // is ok. But for the extra window automatically closed ( ie the "there is an app for that
          // page, downlaod it on itune" page header, the header disappears when driver.get navigates
          // to a new page. The header disapeearing shouldn't impact the current state.
          for (WebkitPage p : old) {
            log.fine(
                "the page " + p + " has been deleted and must be removed from the driver cache");
            int currentFocus = driver.getCurrentPageID();
            if (p.getPageId() == currentFocus) {
              log.fine("the page deleted is the one with the focus.");
            } else {
              driver.setPages(m.getPages());
            }
          }
        }

        sync.signalSimSentPages();
      } else {
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
    }

    if (message instanceof ApplicationDataMessage) {
      //ApplicationDataMessage m = (ApplicationDataMessage)message;
    }

    if (message instanceof ApplicationConnectedMessage) {
      ApplicationConnectedMessage m = (ApplicationConnectedMessage) message;
      List<WebkitApplication> apps = new ArrayList<WebkitApplication>();
      apps.add(m.getApplication());
      driver.setApplications(apps);
      sync.signalSimSentApps();
    }
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
