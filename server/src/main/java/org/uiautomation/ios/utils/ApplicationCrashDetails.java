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

package org.uiautomation.ios.utils;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

public class ApplicationCrashDetails {

  private final String log;
  private String crashReport;

  public ApplicationCrashDetails(String log) {
    this.log = log;
    crashReport = mostRecentCrashReport();
  }

  private String mostRecentCrashReport() {
    File crashFolder = new File(System.getProperty("user.home") + "/Library/Logs/DiagnosticReports/");
    File[] files = crashFolder.listFiles(new FileFilter() {
      @Override
      public boolean accept(File file) {
        // Each crash is reported both as .Foo[...].crash.plist and as Foo[...].crash. Filter out the plists.
        return file.getName().endsWith(".crash");
      }
    });
    if (files != null && files.length > 0) {
      Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
      File newestFile = files[0];
      return String.format("The crash report can be found at: %s", newestFile.getAbsoluteFile());
    } else if (log.contains("Script was stopped by the user")) {
      return "Instruments appears to have crashed.";
    } else {
      return "The Simulator appears to have crashed.";
    }
  }

  public String toString() {
    return log + "\n" + crashReport;
  }
}
