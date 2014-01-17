/*
Copyright 2012 Software Freedom Conservatory.

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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;

import org.testng.annotations.Test;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.selenium.BaseSeleniumTest;

public class GetLogsTest extends BaseSeleniumTest {

  @Test
  public void testLogBufferShouldBeResetAfterEachGetLogCall() {
    Set<String> logTypes = driver.manage().logs().getAvailableLogTypes();
    for (String logType : logTypes) {
      driver.get(pages.simpleTestPage);
      LogEntries firstEntries = driver.manage().logs().get(logType);
      if (!firstEntries.getAll().isEmpty()) {
        LogEntries secondEntries = driver.manage().logs().get(logType);
        assertFalse(LogEntriesChecks.hasOverlappingLogEntries(
                firstEntries, secondEntries),
            String.format("There should be no overlapping log entries in " +
                "consecutive get log calls for %s logs", logType));
      }
    }
  }
  
  @Test
  public void testDifferentLogsShouldNotContainTheSameLogEntries() {
    driver.get(pages.simpleTestPage);
    Map<String, LogEntries> logTypeToEntriesMap = new HashMap<String, LogEntries>();
    Set<String> logTypes = driver.manage().logs().getAvailableLogTypes();
    for (String logType : logTypes) {
      logTypeToEntriesMap.put(logType, driver.manage().logs().get(logType));
    }
    for (String firstLogType : logTypeToEntriesMap.keySet()) {
      for (String secondLogType : logTypeToEntriesMap.keySet()) {
        if (!firstLogType.equals(secondLogType)) {
          assertFalse(LogEntriesChecks.hasOverlappingLogEntries(
                  logTypeToEntriesMap.get(firstLogType),
                  logTypeToEntriesMap.get(secondLogType)),
              String.format("Two different log types (%s, %s) should not " +
                  "contain the same log entries", firstLogType, secondLogType));
        }
      }
    }
  }

  @Test
  public void testTurningOffLogShouldMeanNoLogMessages() throws Exception {
    Set<String> logTypes = driver.manage().logs().getAvailableLogTypes();
    for (String logType : logTypes) {
      IOSCapabilities caps = IOSCapabilities.iphone("Safari");
      LoggingPreferences logPrefs = new LoggingPreferences();
      logPrefs.enable(logType, Level.OFF);
      caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
      startDriver(caps);
      driver.get(pages.simpleTestPage);
      LogEntries entries = driver.manage().logs().get(logType);
      assertTrue(entries.getAll().isEmpty(),
          String.format("There should be no log entries for " +
              "log type %s when logging is turned off.", logType));
    }
    stopDriver();
  }
}
