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

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.testng.annotations.*;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

/**
 * Checks that one can use a URL as the argument to -aut.
 */
public final class AutAsUrlTest {

  private IOSServer server;
  private IOSServerConfiguration config;
  private RemoteWebDriver driver;

  @BeforeClass
  public void startServer() throws Exception {
    String[] args = { "-beta", "-port", "4444", "-host", "localhost",
                      "-app", SampleApps.getUICatalogZipURL(), "-simulators" };
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
  public void canUseUrlInAut() {
    driver = new RemoteWebDriver(getRemoteURL(), SampleApps.uiCatalogCap());
    String expected = "UIATableCell";
    WebElement element = driver.findElement(By.tagName(expected));
    Assert.assertEquals(element.getClass(), RemoteWebElement.class);
    Assert.assertEquals(element.getTagName(), expected);
  }
}
