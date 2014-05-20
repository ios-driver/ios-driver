/*
 * Copyright 2013 ios-driver committers.
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
package org.uiautomation.ios.logging;

import org.openqa.selenium.logging.LogEntry;

import java.util.logging.Level;
import java.util.ArrayList;
import java.util.List;

/*
 * Stores an Array of LogEntry from Webdriver Logging API.  LogEntries are of a specific
 * type and have a minimum level.
 */
public class WebDriverLog implements Log {

  private final List<LogEntry> logEntries = new ArrayList<LogEntry>();

  private final Level minLogLevel;
  private final String type;

  public WebDriverLog(String type, Level minLogLevel) {
    this.type = type;
    this.minLogLevel = minLogLevel;
  }

  public String getType() {
    return type;
  }

  @Override
  public void addEntry(Level level, String message) {
    addEntryTimestamped(System.currentTimeMillis(), level, message);
  }

  @Override
  public void addEntryTimestamped(long timestamp, Level level, String message) {
    if (level.intValue() >= minLogLevel.intValue()) {
      logEntries.add(new LogEntry(level, timestamp, message));
    }
  }

  public List<LogEntry> getAndClearEntries() {
    List<LogEntry> ret = new ArrayList<LogEntry>(logEntries);
    logEntries.clear();
    return ret;
  }

}
