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


import org.testng.IClass;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ISuiteResult;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * At the end of a test suite, saves the time it took to run each class.
 *
 * To enable : -DStatCollector
 * will generate : server/target/surefire-reports/metrics/duration_per_class.metrics
 * To change the file name from the default :  -DStatCollector.fileName=myCoolName.metrics
 * will generate : server/target/surefire-reports/metrics/myCoolName.metrics
 */
public class StatCollector implements IReporter {


  private final Map<String, Long> timePerClass = new HashMap<>();
  private String suffix;
  private File resultsFile;
  private boolean on = false;

  public StatCollector() {
    String on = System.getProperty("StatCollector");

    if (on != null) {
      this.on = true;
    }
    log("duration per class is " + (this.on ? "ON " : "OFF") + ".");
  }

  private void log(String msg){
    System.out.println("BUILD STUFF::StatCollector "+msg);
  }


  private void add(IClass clazz, long time) {
    String name = clazz.getRealClass().getCanonicalName();
    Long current = timePerClass.get(name);
    if (current == null) {
      current = 0L;
    }
    current += time;
    timePerClass.put(name, current);
  }

  public void addResults(ISuite from) {
    Map<String, ISuiteResult> results = from.getResults();

    for (String key : results.keySet()) {
      ISuiteResult result = results.get(key);

      process(result.getTestContext().getPassedTests());
      process(result.getTestContext().getPassedConfigurations());

      process(result.getTestContext().getFailedTests());
      process(result.getTestContext().getFailedConfigurations());

      process(result.getTestContext().getSkippedTests());
      process(result.getTestContext().getSkippedConfigurations());
    }


  }

  private void process(IResultMap map) {
    for (ITestResult res : map.getAllResults()) {
      IClass clazz = res.getTestClass();
      long duration = res.getEndMillis() - res.getStartMillis();
      add(clazz, duration);
    }
  }

  private void save() throws IOException {
    FileWriter writer = new FileWriter(resultsFile);

    for (String clazz : timePerClass.keySet()) {
      writer.append(clazz + "=" + timePerClass.get(clazz) + "\n");
    }

    writer.flush();
    writer.close();
  }


  @Override
  public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,
                             String outputDirectory) {
    if (on) {
      String fileName = System.getProperty("StatCollector.fileName");
      if (fileName == null) {
        fileName = "duration_per_class.metrics";
      }

      resultsFile = new File(outputDirectory, "metrics");
      resultsFile.mkdirs();

      resultsFile = new File(resultsFile, fileName);
      log(" metrics=" + resultsFile.getAbsolutePath());

      for (ISuite suite : suites) {
        addResults(suite);
      }

      try {
        save();
      } catch (IOException e) {
        e.printStackTrace();
      }

    }

  }
}

