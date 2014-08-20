/*
 * Copyright 2012-2014 eBay Software Foundation and ios-driver committers
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

package org.uiautomation.ios.server;


import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.IOSServer;
import org.uiautomation.ios.IOSServerConfiguration;
import org.uiautomation.ios.IOSServerManager;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.application.MobileSafariLocator;
import org.uiautomation.ios.command.configuration.Configuration;
import org.uiautomation.ios.communication.HttpClientFactory;
import org.uiautomation.ios.instruments.InstrumentsFailedToStartException;
import org.uiautomation.ios.utils.IOSVersion;

import java.net.URL;
import java.util.Set;

public class ServerCanStopTest {


  @Test
  public void end2end() throws Exception {

    IOSServerConfiguration config = new IOSServerConfiguration();
    config.setPort(4444);
    config.setHost("localhost");

    HttpClient client = HttpClientFactory.getClient();
    URL u = new URL("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub/manage/stopgracefully");

    BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("GET", u.toExternalForm());

    HttpHost h = new HttpHost(u.getHost(), u.getPort());

    final IOSServer server = new IOSServer(config);
    server.start();


    waitForServerRun(server);
    HttpResponse response = client.execute(h, r);

    Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
    server.stopGracefully();
    ServerSideSession s = server.getDriver().createSession(IOSCapabilities.iphone("Safari"));
    Assert.assertNull(s);
    waitForServerToStop(server);

  }

  @Test
  public void finishesInstantlyWhenNoTest()
      throws InstrumentsFailedToStartException, InterruptedException {
    IOSServerConfiguration config = new IOSServerConfiguration();
    config.setPort(4444);
    config.setHost("localhost");
    Configuration.SIMULATORS_ENABLED = true;

    final IOSServerManager mgr = new IOSServerManager(config);

    Assert.assertEquals(mgr.getSessions().size(), 0);
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          mgr.stopGracefully();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
    t.start();

    Assert.assertEquals(mgr.getSessions().size(), 0);

    while (mgr.isRunning()) {
      Thread.sleep(250);
    }

    t.join();
  }

  @Test
  public void waitForTestsToFinish()
      throws InstrumentsFailedToStartException, InterruptedException {
    IOSServerConfiguration config = new IOSServerConfiguration();
    config.setPort(4444);
    config.setHost("localhost");
    Configuration.SIMULATORS_ENABLED = true;

    final IOSServerManager mgr = new IOSServerManager(config);
    addSafari(mgr, new StringBuilder());

    IOSCapabilities saf = IOSCapabilities.iphone("Safari");
    ServerSideSession session = mgr.createSession(saf);
    Assert.assertEquals(mgr.getSessions().size(), 1);
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {

        try {
          mgr.stopGracefully();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });

    t.start();

    mgr.stop(session.getSessionId());
    Assert.assertEquals(mgr.getSessions().size(), 0);

    while (mgr.isRunning()) {
      Thread.sleep(250);
    }

    t.join();
  }


  @Test
  public void doNotAllowNewTestsAfterShutdown()
      throws InstrumentsFailedToStartException, InterruptedException {
    IOSServerConfiguration config = new IOSServerConfiguration();
    config.setPort(4444);
    config.setHost("localhost");
    Configuration.SIMULATORS_ENABLED = true;

    final IOSServerManager mgr = new IOSServerManager(config);
    addSafari(mgr, new StringBuilder());

    IOSCapabilities saf = IOSCapabilities.iphone("Safari");
    ServerSideSession session = mgr.createSession(saf);
    Assert.assertEquals(mgr.getSessions().size(), 1);
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          mgr.stopGracefully();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });

    t.start();

    mgr.waitForState(IOSServerManager.State.stopping);
    ServerSideSession session2 = mgr.createSession(saf);
    Assert.assertNull(session2);

    mgr.stop(session.getSessionId());
    Assert.assertEquals(mgr.getSessions().size(), 0);

    while (mgr.isRunning()) {
      Thread.sleep(250);
    }

    t.join();
  }


  private void addSafari(IOSServerManager driver, StringBuilder b) {
    String hostSDK = driver.getHostInfo().getSDK();
    b.append(String.format("\nUsing Xcode install: %s",
                           driver.getHostInfo().getXCodeInstall().getPath()));
    b.append(
        String.format("\nUsing instruments: %s", driver.getHostInfo().getInstrumentsVersion()));
    b.append(String.format("\nUsing iOS version %s", hostSDK));

    boolean safari = false;
    // automatically addSafari safari for host SDK and above as instruments starts simulator on host SDK version
    for (String s : driver.getHostInfo().getInstalledSDKs()) {
      IOSVersion version = new IOSVersion(s);
      if (version.isGreaterOrEqualTo("6.0")) {
        safari = true;
        driver.addSupportedApplication(MobileSafariLocator.locateSafariInstall(s));
      }
    }
    if (safari) {
      b.append("\niOS >= 6.0. Safari and hybrid apps are supported.");
    } else {
      b.append("\niOS < 6.0. Safari and hybrid apps are NOT supported.");
    }
  }

  private void waitForServerRun(IOSServer server) throws InterruptedException {
    while (!server.isRunning()) {
      Thread.sleep(1000);
    }
  }

  private void waitForServerToStop(IOSServer server) throws InterruptedException {
    while (server.isRunning()) {
      Thread.sleep(1000);
    }
  }
}
