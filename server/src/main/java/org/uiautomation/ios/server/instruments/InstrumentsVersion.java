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

package org.uiautomation.ios.server.instruments;

import org.uiautomation.ios.utils.CommandOutputListener;

/**
 * parses the output of a "instruments" call to  extract the version. There doesn't seem to be a
 * --version flag.
 */
public class InstrumentsVersion implements CommandOutputListener {

  private final String start = "instruments, version ";
  private String version;
  private String build;

  @Override
  public void stdout(String log) {
    // ignore the regular logs.
  }

  @Override
  public void stderr(String log) {
    // no --version flag, so call a wrong command and extract the info from the error message.
    if (log != null && log.contains(start)) {
      processVersionLine(log);
    }
  }

  // string parsing to get the version out.
  private void processVersionLine(String log) {
    log = log.replace(start, "");
    String[] pieces = log.split(" \\(");
    if (pieces.length == 2) {
      version = pieces[0];
      build = pieces[1].replace(")", "");
      // old builds do not include build info. Xcode 4.3.2 return 1.0 without build info
    } else {
      version = pieces[0];
      build = null;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    InstrumentsVersion that = (InstrumentsVersion) o;

    if (build != null ? !build.equals(that.build) : that.build != null) {
      return false;
    }
    if (version != null ? !version.equals(that.version) : that.version != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = version != null ? version.hashCode() : 0;
    result = 31 * result + (build != null ? build.hashCode() : 0);
    return result;
  }

  /**
   * 1.0 for Xcode 4.3.2 4.5 for Xcode 4.5 5.0 for Xcode 5
   *
   * @return the version of instruments.
   */
  public String getVersion() {
    return version;
  }

  /**
   * the build of instruments command line tool. No available ( returns null ) for older version of
   * instruments. null for XCode 4.3.2 46000 for Xcode 4.5 52076 for Xcode5 DP6
   *
   * @return the build of instruments. Null is not available.
   */
  public String getBuild() {
    return build;
  }

  @Override
  public String toString() {
    return "version:" + version + ", build: " + build;
  }
}


