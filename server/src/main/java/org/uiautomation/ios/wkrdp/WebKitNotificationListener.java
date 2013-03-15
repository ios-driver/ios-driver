/*
 * Copyright 2013 ios-driver committers.
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

package org.uiautomation.ios.wkrdp;

import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.wkrdp.internal.WebKitSyncronizer;
import org.uiautomation.ios.wkrdp.message.*;

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
      log.fine ("pages " + (equals?"equals":"CHANGED") + ": " + driverPages + " -> " + messagePages + ": " + m);
      if (equals) return;

      driver.setPages(messagePages);
      sync.signalSimSentPages();

      if (messagePages.size() > 0) {
        if (session != null) {
          waitForWindowSwitchingAnimation();
        }
        WebkitPage focus = selectPage(driver.getPages());
        if (focus != null)
          driver.switchTo(focus);
      }
    }

    if (message instanceof ApplicationDataMessage) {
      //ApplicationDataMessage m = (ApplicationDataMessage)message;
      //System.out.println(m.getMessage());
    }

    if (message instanceof ApplicationConnectedMessage) {
      ApplicationConnectedMessage m = (ApplicationConnectedMessage) message;
      List<WebkitApplication> apps = new ArrayList<WebkitApplication>();
      System.out.println("message apps : " + m.getApplication());
      apps.add(m.getApplication());
      driver.setApplications(apps);
      sync.signalSimSentApps();
    }

    //System.err.println(message);
  }

  private WebkitPage selectPage(List<WebkitPage> pages) {
    for (WebkitPage page: pages)
      if (!isITunesAd(page.getURL()))
        return page;
    return null;
  }

  private boolean isITunesAd(String url) {
    return (url.contains("itunes.apple.com") && url.contains("style=banner"));
  }

  private void waitForWindowSwitchingAnimation() {
    try {
      Thread.sleep(400);
    } catch (InterruptedException ignore) {
    }
  }
}
