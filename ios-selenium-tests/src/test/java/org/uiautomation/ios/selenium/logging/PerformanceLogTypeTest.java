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

package org.uiautomation.ios.selenium.logging;

import com.google.common.collect.Iterables;

import java.net.URL;
import java.util.Set;
import java.util.logging.Level;

import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.selenium.BaseSeleniumTest;

public class PerformanceLogTypeTest extends BaseSeleniumTest {

  @Override
  protected void startDriver(IOSCapabilities caps) {
    // Disable default driver; instead, let each test call "createDriver"
  }

  protected void createDriver(Level performanceLogLevel) throws Exception {
    IOSCapabilities caps = IOSCapabilities.iphone("Safari");
    if (performanceLogLevel != null) {
      LoggingPreferences logPrefs = new LoggingPreferences();
      logPrefs.enable(LogType.PERFORMANCE, performanceLogLevel);
      caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
    }
    super.startDriver(caps);
  }

  @Test
  public void testPerformanceLogShouldBeDisabledByDefault() throws Exception {
    createDriver(null);
    Set<String> logTypes = driver.manage().logs().getAvailableLogTypes();
    assertFalse(logTypes.contains(LogType.PERFORMANCE),
        "Performance log should not be enabled default");
  }

  @Test
  public void testShouldBeAbleToEnablePerformanceLog() throws Exception {
    createDriver(Level.INFO);
    Set<String> logTypes = driver.manage().logs().getAvailableLogTypes();
    assertTrue(logTypes.contains(LogType.PERFORMANCE),
        "Profiler log should be enabled.");
  }

  @Test
  public void testPageLoadShouldProducePerformanceLogEntries() throws Exception {
    createDriver(Level.INFO);
    driver.get(pages.simpleTestPage);
    LogEntries entries = driver.manage().logs().get(LogType.PERFORMANCE);
    assertTrue(Iterables.size(entries) > 0, "Performance log contains entries.");
  }
}
