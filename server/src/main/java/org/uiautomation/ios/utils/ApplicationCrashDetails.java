package org.uiautomation.ios.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;

import java.io.File;
import java.util.Collection;
import java.util.Date;

public class ApplicationCrashDetails {

  private final String log;
  private String crashReport;

  public ApplicationCrashDetails(String log){
    this.log = log;
    crashReport = mostRecentCrashReport();
  }

  public static String mostRecentCrashReport() {
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
    if(sb.toString().isEmpty()){
      sb.append("It appears like the Simulator process has crashed.");
    }
    return sb.toString();
  }

  public String toString(){
    return log + "\n" + crashReport;
  }

}
