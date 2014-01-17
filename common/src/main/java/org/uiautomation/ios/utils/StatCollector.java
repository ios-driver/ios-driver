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
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener2;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.Listeners;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Listeners(StatCollector.class)
public class StatCollector implements ISuiteListener, IMethodInterceptor {


  private final Map<String, Long> timePerClass = new HashMap<>();
  private String suite;
  private boolean generate = false;
  private int nbSlave = 0;
  private Set<String> classesToRun = new HashSet<>();

  @Override
  public void onStart(ISuite suite) {
    String s = System.getProperty("generate");
//    System.out.println("THE PUTPUT IS : "+suite.getName());
//    System.exit(0);
    if (System.getProperty("generate") != null) {
      generate = true;
      nbSlave = Integer.parseInt(s);
    }
    this.suite = suite.getName();

    for (ITestNGMethod m : suite.getAllMethods()) {
      classesToRun.add(m.getTestClass().getRealClass().getCanonicalName());
    }

    if (generate) {
      for (ITestNGMethod m : suite.getExcludedMethods()) {
        System.out.println("EXCLUDED : " + m.getConstructorOrMethod().getMethod().toString());
      }

      try {
        split(nbSlave);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }



  class OldResultTime implements Comparable<OldResultTime> {

    private long duration;
    private String clazz;

    public OldResultTime(String clazz, int duration) {
      this.clazz = clazz;
      this.duration = duration;
    }


    @Override
    public int compareTo(OldResultTime o) {
      return (int) (o.duration - this.duration);
    }

    @Override
    public String toString() {
      return clazz + " (" + duration + ")";
    }

    public long getDuration() {
      return duration;
    }

    public String getClassCanonicalName() {
      return clazz;
    }
  }

  private void split(int nbSlaves) throws IOException {
    List<OldResultTime> old = new ArrayList<>();
    for (String clazz : classesToRun) {
      Properties p = new Properties();
      try {
        FileReader reader = new FileReader("ios-driver.txt.back");
        p.load(reader);
      } catch (Exception e) {
        e.printStackTrace();
      }

      String s = p.getProperty(clazz);
      if (s == null) {
        s = "1";
      }
      int t = (int) Long.parseLong(s);
      old.add(new OldResultTime(clazz, t));

    }
    Collections.sort(old);

    Properties p = new Properties();

    List<SubSuite> suites = greedy(nbSlaves, old);
    for (SubSuite suite : suites) {
      File f = new File(suite.getName() + ".xml");
      suite.generateTestNGXMLFile(f);
      p.put(suite.getName().toUpperCase(), suite.generateCommandLineArguments());

    }

    File cla = new File("subsuites.properties");
    FileWriter write = new FileWriter(cla);
    p.store(write, "ios-driver internal build.File generated automatically.Do not edit manually. ");
  }


  class SubSuite implements Comparable<SubSuite> {

    private List<OldResultTime> classes = new ArrayList<>();
    private String name;


    public SubSuite(String name) {
      this.name = name;
    }

    public void add(OldResultTime item) {
      classes.add(item);
    }


    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Subsuite :" + name + "\n");

      builder.append("expected run time : " + getExpectedRunTime() + "\n");
      for (OldResultTime old : classes) {
        builder.append(old.clazz + "\n");
      }
      return builder.toString();
    }

    public long getExpectedRunTime() {
      Long res = 0L;
      for (OldResultTime old : classes) {
        res += old.getDuration();
      }
      return res;
    }

    @Override
    public int compareTo(SubSuite o) {
      return (int) (this.getExpectedRunTime() - o.getExpectedRunTime());
    }


    public void generateTestNGXMLFile(File f) throws IOException {

      StringBuilder builder = new StringBuilder();

      builder.append("<!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\" >\n"
                     + "\n"
                     + "<suite name=\"ios-driver\" parallel=\"false\">\n"
                     + "    <listeners>\n"
                     + "        <listener class-name=\"org.uiautomation.ios.utils.StatCollector\"></listener>\n"
                     + "    </listeners>\n"
                     + "\t<test name=\"" + name + "\" preserve-order=\"true\">\n");
      builder.append("\t\t<classes>\n");

      for (OldResultTime old : classes) {
        builder.append("\t\t\t<class name=\"" + old.getClassCanonicalName() + "\"  />\n");
      }

      builder.append("\t</classes>\n");
//                     + "\t\t<packages>\n"
//                     + "\t\t\t<package name=\"org.uiautomation.ios.*\" />\n"
//                     + "\t\t</packages>\n"
      builder.append("\t</test>\n");
      builder.append("\t</suite>");

      FileWriter writer = new FileWriter(f);
      writer.write(builder.toString());
      writer.flush();
      writer.close();


    }

    public String getName() {
      return name;
    }

    public String generateCommandLineArguments() throws IOException {
      StringBuilder builder = new StringBuilder();

      for (OldResultTime old : classes) {
        builder.append(old.getClassCanonicalName() + ",");
      }
      return builder.toString();
    }


  }

  private List<SubSuite> greedy(int nbSlaves, List<OldResultTime> old) {
    List<SubSuite> suites = new ArrayList<>();

    for (int i = 0; i < nbSlaves; i++) {
      suites.add(new SubSuite("subsuite_" + i));
    }

    while (!old.isEmpty()) {
      OldResultTime item = old.remove(0);
      suites.get(0).add(item);
      Collections.sort(suites);
    }

    for (SubSuite s : suites) {
      System.out.println(s);
    }
    return suites;
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

  @Override
  public void onFinish(ISuite suite) {
    Map<String, ISuiteResult> results = suite.getResults();

    for (String key : results.keySet()) {
      ISuiteResult result = results.get(key);

      process(result.getTestContext().getPassedTests());
      process(result.getTestContext().getPassedConfigurations());

      process(result.getTestContext().getFailedTests());
      process(result.getTestContext().getFailedConfigurations());

      process(result.getTestContext().getSkippedTests());
      process(result.getTestContext().getSkippedConfigurations());
    }

    try {
      save();
    } catch (IOException e) {
      e.printStackTrace();
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
    File f = new File(suite + ".txt");
    System.out.println("results : " + f.getAbsolutePath());

    FileWriter writer = new FileWriter(f);

    for (String clazz : timePerClass.keySet()) {
      writer.append(clazz + "=" + timePerClass.get(clazz) + "\n");
    }

    writer.flush();
    writer.close();
  }


  @Override
  public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
    if (generate) {
      return new ArrayList<>();
    } else {
      return methods;
    }
  }
}

