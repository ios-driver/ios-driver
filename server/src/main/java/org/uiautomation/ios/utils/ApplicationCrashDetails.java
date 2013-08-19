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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;

import java.io.File;
import java.util.Collection;
import java.util.Date;

public class ApplicationCrashDetails {

  private final String log;
  private String crashReport;

  public ApplicationCrashDetails(String log) {
    this.log = log;
    crashReport = mostRecentCrashReport();
  }

  private String mostRecentCrashReport() {
    File crashFolder = new File(System.getProperty("user.home") + "/Library/Logs/DiagnosticReports/");
    Date now = new Date();
    Date cutoffDate = new Date(now.getTime() - 10000);
    Collection<File> files = FileUtils.listFiles(crashFolder, new AgeFileFilter(cutoffDate, false), null);
    StringBuilder sb = new StringBuilder();
    if (files.size() > 0) {
      sb.append("The crash report can be found:");
      for (File f : files) {
        sb.append("\n" + f.getAbsoluteFile());
      }
    }
    if (sb.toString().isEmpty()) {
      if (log.contains("Script was stopped by the user")) {
        sb.append("It appears like the Instruments process has crashed.");
      } else {
        sb.append("It appears like the Simulator process has crashed.");
      }
    }
    return sb.toString();
  }

  public String toString() {
    return log + "\n" + crashReport;
  }

}
