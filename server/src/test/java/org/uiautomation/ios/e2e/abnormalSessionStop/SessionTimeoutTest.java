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

package org.uiautomation.ios.e2e.abnormalSessionStop;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.IOSServer;
import org.uiautomation.ios.IOSServerConfiguration;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
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
                     "-aut", SampleApps.getUICatalogFile(),
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
  public void canSetTimeoutBetween2CommandsWebMode() throws InterruptedException {
    driver = new RemoteIOSDriver(getRemoteURL(), IOSCapabilities.iphone("Safari"));
    Assert.assertNotNull(driver.getCurrentUrl());
    Thread.sleep(idleBetweenCommands * 1000 - 500);
    Assert.assertNotNull(driver.getCurrentUrl());
    Thread.sleep(idleBetweenCommands * 1000 + 100);
    try {
      driver.getCurrentUrl();
    } catch (WebDriverException e) {
      Assert.assertTrue(e.getMessage().startsWith(ServerSideSession.StopCause.timeOutBetweenCommand.name()));
      return;
    }
    fail("should have timed out");
  }

  @Test
  public void canSetTimeoutBetween2CommandsNativeMode() throws InterruptedException {
    driver = new RemoteIOSDriver(getRemoteURL(), SampleApps.uiCatalogCap());

    Assert.assertNotNull(driver.findElement(By.xpath("//UIAWindow")));
    Thread.sleep(idleBetweenCommands * 1000 + 100);
    try {
      driver.findElement(By.xpath("//UIAWindow"));
    } catch (WebDriverException e) {
      Assert.assertTrue(e.getMessage().startsWith(ServerSideSession.StopCause.timeOutBetweenCommand.name()));
      return;
    }
    fail("should have timed out");
  }


  @Test
  public void canSetTimeoutBetween2CommandsWhenProcessingANativeCommand() throws InterruptedException {
    driver = new RemoteIOSDriver(getRemoteURL(), SampleApps.uiCatalogCap());
    Assert.assertEquals(driver.executeScript("return 1;"), 1L);
    try {
      driver.executeScript("UIATarget.localTarget().delay(10);return 1;");
      fail("should have timed out");
    } catch (Exception e) {
      Assert.assertTrue(e instanceof WebDriverException);
      String expected = ServerSideSession.StopCause.timeOutBetweenCommand.name();
      String current = e.getMessage();
      Assert.assertTrue(current.startsWith(expected), "the message is : " + current);
      return;
    }
    fail("should have timed out");
  }


  @Test
  public void sessionTimeOut() throws InterruptedException {
    driver = new RemoteIOSDriver(getRemoteURL(), SampleApps.uiCatalogCap());
    long started = System.currentTimeMillis();
    long lastCommandStarted = 0;
    try {
      while (true) {
        // check that we can't execute new command after the session has timed out.
        lastCommandStarted = System.currentTimeMillis() - started;
        driver.findElement(new TypeCriteria(UIAElement.class));
        Thread.sleep(250);
      }

    } catch (WebDriverException e) {
      Assert.assertTrue(e.getMessage().contains(ServerSideSession.StopCause.sessionTimeout.name()));
      Assert.assertTrue(lastCommandStarted < ((sessionTimeoutInSec * 1000)+250),
                        String.format("last command was started %d ms after the session was started", lastCommandStarted));
    }
  }
}
