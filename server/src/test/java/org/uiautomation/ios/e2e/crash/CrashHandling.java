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

package org.uiautomation.ios.e2e.crash;

import junit.framework.Assert;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.client.uiamodels.impl.augmenter.ElementTree;
import org.uiautomation.ios.client.uiamodels.impl.augmenter.IOSDriverAugmenter;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;
import org.uiautomation.ios.utils.ClassicCommands;

import java.net.MalformedURLException;
import java.net.URL;


public class CrashHandling {

  private IOSServer server;
  private IOSServerConfiguration config;
  private RemoteWebDriver driver;
  private IOSCapabilities cap;

  @BeforeMethod
  public void startServer() throws Exception {
    String[] args = {"-port", "4444", "-host", "localhost",
                     "-app", SampleApps.getPPNQASampleApp()};
    config = IOSServerConfiguration.create(args);

    server = new IOSServer(config);
    server.start();

    Thread.sleep(1000);

    cap = SampleApps.ppNQASampleAppCap();
    cap.setCapability(IOSCapabilities.SIMULATOR, true);

    driver = new RemoteWebDriver(getRemoteURL(), cap);
  }

  @AfterMethod(alwaysRun = true)
  public void closeDriver() throws Exception {
    if (driver != null) {
      try {
        driver.quit();
      } catch (Exception e) {
        e.printStackTrace();
      }
      driver = null;
    }

    if (server != null) {
      try {
        server.stop();
      } catch (Exception e) {
        e.printStackTrace();
      }
      server = null;
    }
  }

  private URL getRemoteURL() {
    try {
      return new URL("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void isAppCrashDetected() throws InterruptedException {
    WebElement crashButton = driver.findElement(By.name("Crash me!"));

    boolean crashExceptionThrown = false;
    try {
      crashButton.click();
      // give instruments some time to realise there has been a crash and report it
      Thread.sleep(1000);
      // the application has crashed - we should be notified on the next client request
      crashButton.click();
    } catch (Exception e) {
      crashExceptionThrown = true;
      Assert.assertTrue("Crash error contains the crash trace file details. " + e.getMessage(),
                        e.getMessage().contains("The crash report can be found"));
    }
    Assert.assertTrue("App crash detected.", crashExceptionThrown);
  }

  @Test
  public void isExceptionThrownOnEveryActionAfterCrash() throws InterruptedException {
    WebElement crashButton = driver.findElement(By.name("Crash me!"));

    try {
      crashButton.click();
    } catch (Exception e) {
      // ignore.
    }
    Thread.sleep(1000);
    catchCrashException(crashButton);
    catchCrashException(crashButton);
    catchCrashException(crashButton);
  }

  private boolean catchCrashException(WebElement button) {
    boolean crashExceptionThrown = false;
    try {
      // the application has crashed - we should be notified on the next client request
      button.click();
    } catch (Exception e) {
      crashExceptionThrown = true;
      Assert.assertTrue("Crash error contains the crash trace file details. " + e.getMessage(),
                        e.getMessage().contains("The crash report can be found"));
    }
    return crashExceptionThrown;
  }

  @Test
  public void canStartANewSessionAfterAppCrash() throws InterruptedException {
    WebElement crashButton = driver.findElement(By.name("Crash me!"));
    try {
      crashButton.click();
      // give instruments some time to realise there has been a crash and report it
      Thread.sleep(1000);
      // the application has crashed - we should be notified on the next client request
      crashButton.click();
    } catch (Exception e) {
      // this is expected since we crashed the app
    }

    driver = new RemoteWebDriver(getRemoteURL(), cap);
  }

  @Test
  public void isSimulatorCrashDetected() throws InterruptedException {
    WebElement crashButton = driver.findElement(By.name("Crash me!"));

    boolean crashExceptionThrown = false;
    // kill simulator
    ClassicCommands.killall("iPhone Simulator");
    Thread.sleep(1000);
    try {
      // should throw an exception
      crashButton.click();
    } catch (Exception e) {
      crashExceptionThrown = true;
//      Assert.assertTrue(
//          "Crash error contains Simulator as likely cause of problem. " + e.getMessage(),
//          e.getMessage().contains("It appears like the Simulator process has crashed"));
      // ios 7 : Fail: The target application appears to have died
    }
    Assert.assertTrue("Simulator crash detected.", crashExceptionThrown);
  }

  @Test
  public void canStartANewSessionAfterSimulatorCrash() throws InterruptedException {
    WebElement crashButton = driver.findElement(By.name("Crash me!"));
    // kill simulator
    ClassicCommands.killall("iPhone Simulator");
    Thread.sleep(1000);
    try {
      // should throw an exception
      crashButton.click();
    } catch (Exception ignored) {
      // this is expected since we crashed the simulator
    }
    driver = new RemoteWebDriver(getRemoteURL(), cap);
  }

  @Test
  public void isInstrumentsCrashDetected() throws InterruptedException {
    WebElement crashButton = driver.findElement(By.name("Crash me!"));

    boolean crashExceptionThrown = false;
    try {
      // kill instruments
      ClassicCommands.killall("instruments");
      Thread.sleep(1000);
      // should throw an exception
      crashButton.click();
    } catch (Exception e) {
      crashExceptionThrown = true;
//      Assert.assertTrue("Crash error contains Instruments as likely cause of problem. " + e.getMessage(), e.getMessage().contains("Instruments"));
      // IOS7  :Stopped: Script was stopped by the user
    }
    Assert.assertTrue("Instruments crash detected.", crashExceptionThrown);
  }

  @Test
  public void canStartANewSessionAfterInstrumentsCrash() throws InterruptedException {
    WebElement crashButton = driver.findElement(By.name("Crash me!"));
    // kill instruments
    ClassicCommands.killall("instruments");
    Thread.sleep(1000);
    try {
      // should throw an exception
      crashButton.click();
    } catch (Exception e) {
      // this is expected since we crashed instruments
    }

    driver = new RemoteWebDriver(getRemoteURL(), cap);
  }

  @Test(enabled = false)
  public void canHandleVeryLargeAmountsOfDataInTableViewWithoutCrashing()
      throws InterruptedException {
    WebElement largeTableViewButton = driver.findElement(By.name("TableView 10000"));
    try {
      largeTableViewButton.click();

      ElementTree tree = IOSDriverAugmenter.augment(driver);

      JSONObject json = tree.logElementTree(null, false);

      Assert.assertNotNull("We can get the page source for a large tableview", json);
    } catch (Exception e) {
      Assert.fail(
          "Exception caught while performing logElementTree on page with large TreeView. App crashed");
    }
  }
}
