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

package org.uiautomation.ios.e2e.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;
import org.uiautomation.ios.*;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

public final class SessionTimeoutTest {

  private IOSServer server;
  private IOSServerConfiguration config;
  private RemoteWebDriver driver;

  @BeforeClass
  public void startServer() throws Exception {
    String[] args = { "-port", "4444", "-host", "localhost", "-sessionTimeout", "5", "-simulators" };
    config = IOSServerConfiguration.create(args);

    server = new IOSServer(config);
    server.start();
  }

  @AfterClass
  public void stopServer() throws Exception {
    if (server != null) {
      server.stop();
      server = null;
    }
  }

  @AfterMethod
  public void closeDriver() {
    if (driver != null) {
      driver.quit();
      driver = null;
    }
  }

  private URL getRemoteURL() {
    try {
      URL remote = new URL("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub");
      return remote;
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void canSpecifySessionTimeout() {
    long startTime = System.currentTimeMillis();
    RemoteWebDriver driver = new RemoteWebDriver(getRemoteURL(), IOSCapabilities.iphone("Safari"));
    driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
    try {
      driver.findElement(By.id("no_such_element"));
      Assert.fail("shouldn't find the element");
    } catch (Exception ignore) {
      // can throw anything depending on where the force stop happens
    }
    long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
    Assert.assertTrue("Elapsed: " + elapsedSeconds, elapsedSeconds > 5 && elapsedSeconds < 30);
  }
}
