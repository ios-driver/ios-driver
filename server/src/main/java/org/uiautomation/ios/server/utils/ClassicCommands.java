/*
 * Copyright 2012 ios-driver committers.
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
package org.uiautomation.ios.server.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriverException;

public class ClassicCommands {

  public static List<String> psgrep(String processName) {
    List<String> s = new ArrayList<String>();
    s.add("ps");
    s.add("aux");

    Command com = new Command(s, false);
    Grep grep = new Grep(processName);
    com.registerListener(grep);
    com.executeAndWait();
    return grep.getMatching();
  }

  public static boolean isRunning(String processName) {
    return psgrep(processName).size() > 0;
  }

  public static void killall(String processName) {

    if (isRunning(processName)) {
      List<String> s = new ArrayList<String>();
      s.add("killall");
      s.add(processName);

      Command com = new Command(s, false);
      com.executeAndWait();
    }

  }

  public static File getXCodeInstall() {
    List<String> cmd = new ArrayList<String>();
    cmd.add("/usr/bin/xcrun");
    cmd.add("-find");
    cmd.add("xcodebuild");

    Command c = new Command(cmd, false);
    c.executeAndWait();

    if (c.getStdOut().size() != 1) {
      throw new WebDriverException("cannot find XCode location." + c.getStdOut());
    }

    String path = c.getStdOut().get(0);

    String pattern = ".app/";
    int index = path.indexOf(pattern);
    String res = path.substring(0, index + pattern.length());
    return new File(res);

  }

  public static File getAutomationTemplate() {
    List<String> cmd = new ArrayList<String>();
    cmd.add("instruments");
    cmd.add("-s");
    Command c = new Command(cmd, false);

    Grep grep = new Grep("Automation.tracetemplate");
    c.registerListener(grep);
    c.executeAndWait();
    List<String> res = grep.getMatching();
    if (res.size() == 0) {
      throw new WebDriverException("expected 1 result for automation on instruments -s , got " + res);
    }
    String path = res.get(0);
    path = path.replaceFirst(",", "");
    path = path.replaceAll("\"", "");
    path = path.trim();
    File f = new File(path);
    if (!f.exists()) {
      throw new WebDriverException(f + "isn't a valid template.");
    }
    return f;
  }

  public static List<String> getInstalledSDKs() {
    List<String> c = new ArrayList<String>();
    c.add("xcodebuild");
    c.add("-showsdks");
    Command com = new Command(c, false);
    ShowSDKsPasrer l = new ShowSDKsPasrer();
    com.registerListener(l);
    com.executeAndWait();
    return l.getSDKs();
  }

  // TODO freynaud find the correct command
  public static String getDefaultSDK() {
    List<String> sdks = getInstalledSDKs();
    return sdks.get(sdks.size() - 1);
  }

}

class ShowSDKsPasrer implements CommandOutputListener {

  private List<String> sdks = new ArrayList<String>();
  private boolean ok = true;
  private final String pattern = "iphonesimulator";

  public void stdout(String log) {
    String sdk = extractSDK(log);
    if (sdk != null) {
      sdks.add(sdk);
    }
  }

  private String extractSDK(String log) {
    if (log.contains(pattern)) {
      int index = log.indexOf(pattern) + pattern.length();
      return log.substring(index);
    } else {
      return null;
    }
  }

  public void stderr(String log) {
    ok = false;
  }

  public List<String> getSDKs() {
    if (!ok) {
      throw new WebDriverException("there was an error.stderr is not empty");
    }
    return sdks;
  }

}

class Grep implements CommandOutputListener {

  private final String pattern;
  private final List<String> matching = new ArrayList<String>();

  public Grep(String pattern) {
    this.pattern = pattern;
  }

  public List<String> getMatching() {
    return matching;
  }

  public void stdout(String log) {
    if (log.contains(pattern)) {
      matching.add(log);
    }
  }

  public void stderr(String log) {
    if (log.contains(pattern)) {
      matching.add(log);
    }
  }

}
