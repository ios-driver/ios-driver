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

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class SuiteGenerator implements ISuiteListener, IMethodInterceptor {

  private final boolean on;
  private int nbSlave = 0;
  private String suite;
  private Set<String> classesToRun = new HashSet<>();

  public SuiteGenerator() {
    String s = System.getProperty("SuiteGenerator.generate");

    if (s != null) {
      on = true;
      nbSlave = Integer.parseInt(s);
    } else {
      on = false;
      nbSlave = 0;
    }

    log("generation mode is " + (this.on ? "ON " : "OFF") + ".");
  }

  private void log(String msg){
    System.out.println("BUILD STUFF::SuiteGenerator "+msg);
  }

  @Override
  public void onStart(ISuite suite) {
    File parent = new File(suite.getOutputDirectory());
    File folder = null;
    while (parent != null) {
      File candidate = new File(parent, "metrics");
      if (candidate.exists()) {
        folder = candidate;
        break;
      }
      parent = parent.getParentFile();
    }

    if (folder == null) {
      System.out.println("np parent");
    } else {
      log("will use metrics from :"+folder.getAbsolutePath());
    }



//    this.suite = suite.getName();
//
//    for (ITestNGMethod m : suite.getAllMethods()) {
//      classesToRun.add(m.getTestClass().getRealClass().getCanonicalName());
//    }
//
//    if (on) {
//      for (ITestNGMethod m : suite.getExcludedMethods()) {
//        System.out.println("EXCLUDED : " + m.getConstructorOrMethod().getMethod().toString());
//      }
//
//      try {
//        split(nbSlave);
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//    }
  }

  @Override
  public void onFinish(ISuite suite) {

  }

  private List<SubSuite> greedy(int nbSlaves, List<ClassTestTime> old) {
    List<SubSuite> suites = new ArrayList<>();

    for (int i = 0; i < nbSlaves; i++) {
      suites.add(new SubSuite("subsuite_" + i));
    }

    while (!old.isEmpty()) {
      ClassTestTime item = old.remove(0);
      suites.get(0).add(item);
      Collections.sort(suites);
    }

    for (SubSuite s : suites) {
      System.out.println(s);
    }
    return suites;
  }

  private void split(int nbSlaves) throws IOException {
    List<ClassTestTime> old = new ArrayList<>();
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
      old.add(new ClassTestTime(clazz, t));

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

  @Override
  public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
    if (on) {
      return new ArrayList<>();
    } else {
      return methods;
    }
  }
}
