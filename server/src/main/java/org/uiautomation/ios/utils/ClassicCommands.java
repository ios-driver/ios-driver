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

import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.instruments.InstrumentsVersion;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassicCommands {


  static final Logger log = Logger.getLogger(ClassicCommands.class.getName());

  /**
   * returns the version of the currently selected ( xcode-select -switch ) Xcode.
   */
  public static InstrumentsVersion getInstrumentsVersion() {
    List<String> s = new ArrayList<>();
    s.add("instruments");

    Command c = new Command(s, false);
    InstrumentsVersion version = new InstrumentsVersion();
    c.registerListener(version);
    c.executeAndWait(true);

    return version;
  }

  public static List<String> psgrep(String processName) {
    List<String> s = new ArrayList<>();
    s.add("ps");
    s.add("aux");

    Command com = new Command(s, false);
    Grep grep = new Grep(processName);
    com.registerListener(grep);
    try {
      com.executeAndWait();
    } catch (Exception e) {
      log.warning("Error waiting for the process and the listener threads to finish.");
    }
    return grep.getMatching();
  }

  public static boolean isRunning(String processName) {
    try {
      return psgrep(processName).size() > 0;
    } catch (Exception e) {
      log.log(Level.WARNING,"isRunning?",e);
      return false;
    }
  }

  public static void killall(String processName) {
    if (isRunning(processName)) {
      List<String> s = new ArrayList<>();
      s.add("killall");
      s.add(processName);

      Command com = new Command(s, false);
      com.executeAndWait(true);
    }
  }

  public static File getXCodeInstall() {
    List<String> cmd = new ArrayList<>();
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
    List<String> cmd = new ArrayList<>();
    cmd.add("instruments");
    cmd.add("-s");
    Command c = new Command(cmd, false);

    Grep grep = new Grep("Automation.tracetemplate");
    c.registerListener(grep);
    c.executeAndWait();
    List<String> res = grep.getMatching();
    if (res.size() == 0) {
      throw new WebDriverException(
          "expected 1 result for automation on instruments -s , got " + res);
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

  private static List<String> installedSDKs;

  public synchronized static List<String> getInstalledSDKs() {
    if (installedSDKs == null) {
      List<String> c = new ArrayList<>();
      c.add("xcodebuild");
      c.add("-showsdks");
      Command com = new Command(c, false);
      ShowSDKsParser l = new ShowSDKsParser();
      com.registerListener(l);
      try {
        // ignoring errors as showSDK can return warnings about plugins. If that's the case, return
        // code!=0 , but it still should work as look as there are SDKs installed.
        boolean ignoreError = true;
        int exit = com.executeAndWait(ignoreError);
        if (exit != 0) {
          log.warning("there was an error executing xcodebuild -showsdks Exit code=" + exit);
        }
        installedSDKs = l.getSDKs();
      } catch (Exception e) {
        installedSDKs = new ArrayList<>();
      }
    }
    return installedSDKs;
  }

  public static String getDefaultSDK() {
    List<String> sdks = getInstalledSDKs();
    return sdks.get(sdks.size() - 1);
  }

  /**
   * @return the simulator ProductVersion for the corresponding SDKVersion (i.e. "7.0.3" for "7.0")
   */
  public static String getSimulatorProductVersion(String sdkVersion) {
    List<String> c = new ArrayList<>();
    c.add("xcodebuild");
    c.add("-version");
    c.add("-sdk");
    Command com = new Command(c, false);
    SimulatorProductVersionParser l = new SimulatorProductVersionParser(sdkVersion);
    com.registerListener(l);
    com.executeAndWait();
    return l.getProductVersion();
  }

  public static long getHighestPidForName(String processName) {
    List<String> c = new ArrayList<>();
    c.add("pgrep");
    c.add(processName);
    Command com = new Command(c, false);
    final List<Integer> res = new ArrayList<>();
    com.registerListener(new CommandOutputListener() {
      @Override
      public void stdout(String log) {
        res.add(Integer.parseInt(log));
      }

      @Override
      public void stderr(String log) {

      }
    });
    com.executeAndWait(true);

    if (res.size() == 0) {
      return -1;
    }
    Collections.sort(res);
    return res.get(0);
  }


  public static void kill(long pid) {
    if (pid <= 0) {
      return;
    }
    List<String> c = new ArrayList<>();
    c.add("kill");
    c.add("-9");
    c.add(String.format("%d", pid));

    Command com = new Command(c, false);
    com.executeAndWait(true);
  }


}

class Grep implements CommandOutputListener {

  private final String pattern;
  private final List<String> matching = new ArrayList<>();

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





