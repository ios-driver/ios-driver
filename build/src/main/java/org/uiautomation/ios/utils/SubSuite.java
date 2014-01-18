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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class SubSuite implements Comparable<SubSuite> {

  private List<ClassTestTime> classes = new ArrayList<>();
  private String name;


  public SubSuite(String name) {
    this.name = name;
  }

  public void add(ClassTestTime item) {
    classes.add(item);
  }


  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Subsuite :" + name + "\n");

    builder.append("expected run time : " + getExpectedRunTime() + "\n");
    for (ClassTestTime old : classes) {
      builder.append(old.getClassCanonicalName() + "\n");
    }
    return builder.toString();
  }

  public long getExpectedRunTime() {
    Long res = 0L;
    for (ClassTestTime old : classes) {
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

    for (ClassTestTime old : classes) {
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

    for (ClassTestTime old : classes) {
      builder.append(old.getClassCanonicalName() + ",");
    }
    return builder.toString();
  }


}