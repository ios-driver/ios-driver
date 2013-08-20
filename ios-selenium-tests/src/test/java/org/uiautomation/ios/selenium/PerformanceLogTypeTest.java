/*
Copyright 2013 Software Freedom Conservancy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.uiautomation.ios.selenium;

import org.openqa.selenium.Pages;
import org.openqa.selenium.environment.webserver.AppServer;
import org.openqa.selenium.environment.webserver.WebbitAppServer;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;

import com.google.common.collect.Iterables;

import java.net.URL;
import java.util.Set;
import java.util.logging.Level;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class PerformanceLogTypeTest {

  private WebDriver perfDriver;
  
  private IOSServer server;
  private static String[] args = {"-port", "4444", "-host", "localhost", "-beta", "-simulators"};
  private static IOSServerConfiguration config = IOSServerConfiguration.create(args);
  protected RemoteIOSDriver driver = null;
  protected String url = "http://" + config.getHost() + ":" + config.getPort() + "/wd/hub";
  protected Pages pages;
  protected AppServer appServer;

  @BeforeClass
  public void setup() throws Throwable {
    server = new IOSServer(config);
    server.start();
    appServer = new WebbitAppServer();
    appServer.start();
    pages = new Pages(appServer);
  }

  @AfterClass
  public void tearDown() throws Exception {
    server.stop();
    appServer.stop();
  }

  @Test
  public void testPerformanceLogShouldBeDisabledByDefault() throws Exception {
    IOSCapabilities safari = IOSCapabilities.iphone("Safari");
    driver = new RemoteIOSDriver(new URL(url), safari);
    Set<String> logTypes = driver.manage().logs().getAvailableLogTypes();
    assertFalse(logTypes.contains(LogType.PERFORMANCE),"Performance log should not be enabled by default");
    driver.quit();
  }

  void createLocalDriverWithPerformanceLogType() throws Exception {
    IOSCapabilities safari = IOSCapabilities.iphone("Safari");
    LoggingPreferences logPrefs = new LoggingPreferences();
    logPrefs.enable(LogType.PERFORMANCE, Level.INFO);
    safari.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
    perfDriver = new RemoteIOSDriver(new URL(url), safari);
  }

  void tearDownLocal() {
      perfDriver.quit();
  }

  @Test
  public void testShouldBeAbleToEnablePerformanceLog() throws Exception {
    createLocalDriverWithPerformanceLogType();
    Set<String> logTypes = perfDriver.manage().logs().getAvailableLogTypes();
    assertTrue(logTypes.contains(LogType.PERFORMANCE),"Profiler log should be enabled");
    tearDownLocal();
  }

  @Test
  public void testPageLoadShouldProducePerformanceLogEntries() throws Exception {
    createLocalDriverWithPerformanceLogType();
    perfDriver.get(pages.simpleTestPage);
    LogEntries entries = perfDriver.manage().logs().get(LogType.PERFORMANCE);
    assertFalse(Iterables.size(entries)==0);
    tearDownLocal();
  }
}
