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
package org.uiautomation.ios.server.instruments;

import java.util.ArrayList;
import java.util.List;

import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.exceptions.IOSAutomationSetupException;

public class ClassicCommands {

  public static List<String> psgrep(String processName) throws IOSAutomationSetupException {
    List<String> s = new ArrayList<String>();
    s.add("ps");
    s.add("aux");

    Command com = new Command(s, false);
    Grep grep = new Grep(processName);
    com.registerListener(grep);
    com.executeAndWait();
    return grep.getMatching();
  }


  public static boolean isRunning(String processName) throws IOSAutomationSetupException {
    return psgrep(processName).size() > 0;
  }



  public static void killall(String processName) throws IOSAutomationSetupException {

    if (isRunning(processName)) {
      List<String> s = new ArrayList<String>();
      s.add("killall");
      s.add(processName);

      Command com = new Command(s, false);
      com.executeAndWait();
    }


  }

  public static List<String> getInstalledSDKs() throws IOSAutomationSetupException {
    List<String> c = new ArrayList<String>();
    c.add("xcodebuild");
    c.add("-showsdks");
    Command com = new Command(c, false);
    ShowSDKsPasrer l = new ShowSDKsPasrer();
    com.registerListener(l);
    com.executeAndWait();
    return l.getSDKs();
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
      throw new IOSAutomationException("there was an error.stderr is not empty");
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

  public void stderr(String log) {}

}
