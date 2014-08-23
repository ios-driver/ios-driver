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

import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.IOSServer;
import org.uiautomation.ios.IOSServerConfiguration;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static org.testng.FileAssert.fail;

@Test
public final class SessionTimeoutTest {

  private IOSServer server;
  private IOSServerConfiguration config;
  private RemoteIOSDriver driver;

  private final int idleBetweenCommands = 2;

  @BeforeClass
  public void startServer() throws Exception {
    String[] args = {"-port", "4444", "-host", "127.0.0.1",
                     "-sessionTimeout", "20",
                     "-maxIdleBetweenCommands", String.format("%d", idleBetweenCommands)};
    config = IOSServerConfiguration.create(args);

    server = new IOSServer(config);
    server.start();
  }

  @AfterClass
  public void stopServer() throws Exception {
    if (server != null) {
      server.stopGracefully();
    }
  }

  @AfterMethod
  public void closeDriver() {
    if (driver != null) {
      try {
        driver.quit();
      } catch (Exception ignore) {
      }
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
  public void idlBetweenCommandsHasBeenSet() {
    Assert.assertEquals(config.getMaxIdleTimeBetween2CommandsInSeconds(), idleBetweenCommands);
  }


  @Test
  public void canSetTimeoutBetween2Commands() throws InterruptedException {
    driver = new RemoteIOSDriver(getRemoteURL(), IOSCapabilities.iphone("Safari"));
    Assert.assertNotNull(driver.getCurrentUrl());
    Assert.assertNotNull(driver.getCurrentUrl());
    Thread.sleep(idleBetweenCommands * 1000 + 100);
    try {
      driver.getCurrentUrl();
    } catch (WebDriverException e) {
      Assert.assertTrue(e.getMessage().startsWith(ServerSideSession.StopCause.timeOutBetweenCommand.name()));
      //Thread.sleep(10000);
      return;
    }
    fail("should have timed out");
  }

  @Test
  public void canSetTimeoutSessionStart() {
    throw new RuntimeException("NI");
  }

  @Test
  public void canSetOverwallSessionTimeout() {
    throw new RuntimeException("NI");
  }

//  @Test
//  public void canSpecifySessionTimeout() {
//    RemoteWebDriver driver = new RemoteWebDriver(getRemoteURL(), IOSCapabilities.iphone("Safari"));
//    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//    WebElement element = null;
//    long startTime = System.currentTimeMillis();
//    try {
//      element = driver.findElement(By.id("no_such_element"));
//    } catch (Exception ignore) {
//      // can throw anything depending on where the force stop happens
//    }
//    Assert.assertNull(element);
//    long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
//    Assert.assertTrue(elapsedSeconds >= 5 && elapsedSeconds < 20, "Elapsed: " + elapsedSeconds);
//  }
}
