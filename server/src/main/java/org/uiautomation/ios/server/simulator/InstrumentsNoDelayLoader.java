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

package org.uiautomation.ios.server.simulator;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.server.instruments.InstrumentsVersion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class InstrumentsNoDelayLoader {

  private static InstrumentsNoDelayLoader instance;
  private InstrumentsVersion version;
  private File workingDirectory;

  private InstrumentsNoDelayLoader(InstrumentsVersion version) {
    this.version = version;
    this.workingDirectory =
        new File(System.getProperty("java.io.tmpdir") + pathForVersion(version));

    this.workingDirectory.mkdirs();
    if (!this.workingDirectory.exists()) {
      throw new WebDriverException(
          "Cannot create dir to extract instruments stuff : " + workingDirectory);
    }

    extract(version);
  }

  public static synchronized InstrumentsNoDelayLoader getInstance(InstrumentsVersion version) {
    if (instance != null && !version.equals(instance.version)) {
      throw new WebDriverException(
          "Cannot switch instruments from vesrion " + instance.version + " to version " + version
          + " without restarting the server.");
    }
    if (instance == null) {
      instance = new InstrumentsNoDelayLoader(version);
    }
    return instance;
  }

  /**
   * the files are stored in folder + versiob +  build For instance instruments version 4.6 build
   * 46000 will be in /instruments_no_delay/4.6/46000/
   */
  private String pathForVersion(InstrumentsVersion version) {
    String path = version.getVersion() + File.separatorChar + version.getBuild();
    return path;
  }

  /**
   * extract the binaries for the specified version of instruments.
   */
  private void extract(InstrumentsVersion version) {
    if (version.getBuild() == null) {
      throw new WebDriverException("you are running a version of XCode that is too old " + version);
    }

    extractFromJar("instruments");
    extractFromJar("InstrumentsShim.dylib");
    extractFromJar("ScriptAgentShim.dylib");
    extractFromJar("SimShim.dylib");
    extractFromJar("README");

  }

  private File extractFromJar(String resource) {
    String base = "/instruments_no_delay/" + pathForVersion(version) + "/";
    InputStream is = InstrumentsNoDelayLoader.class.getResourceAsStream(base + resource);

    if (is == null) {
      String msg = "Cannot find " + base + resource + ".";
      throw new WebDriverException(msg);
    }
    File f = null;
    try {
      f = new File(workingDirectory, resource);
      FileOutputStream os = new FileOutputStream(f);
      IOUtils.copy(is, os);
      IOUtils.closeQuietly(is);
      IOUtils.closeQuietly(os);
    } catch (IOException e) {
      throw new RuntimeException("Cannot extract" + e.getMessage(), e);
    }
    return f;
  }


  public File getInstruments(String sessionId) {
    return getScript(sessionId);
  }

  public long getPid(String sessionId) {
    File tmp = new File(System.getProperty("user.home") + "/.ios-driver/");
    File pidFile = new File(tmp.getAbsolutePath(), sessionId + ".pid");

    try {
      FileReader reader = new FileReader(pidFile);
      BufferedReader br = new BufferedReader(reader);
      String s = br.readLine();
      return Long.parseLong(s);
    } catch (Exception e) {
      return -1;
    } finally {
      if (pidFile.exists()) {
        pidFile.delete();
      }
    }
  }

  private File getScript(String sessionId) {
    File tmp = new File(System.getProperty("user.home") + "/.ios-driver/");
    if (!tmp.exists()) {
      tmp.mkdirs();
    }
    File instruments = new File(workingDirectory, "instruments_" + sessionId);
    File pidFile = new File(tmp.getAbsolutePath(), sessionId + ".pid");
    try {
      FileWriter writer = new FileWriter(instruments);
      writer.write("LIB_PATH=$(cd `dirname $0`; pwd)\n"
                   + "XCODE_PATH=$(xcode-select --print-path)\n"
                   + "\n"
                   + "DYLD_INSERT_LIBRARIES=\"$LIB_PATH/InstrumentsShim.dylib\" \\\n"
                   + "LIB_PATH=$LIB_PATH \\\n"
                   + "\"$XCODE_PATH\"/usr/bin/instruments \"$@\" &\n"
                   + "echo $! > " + pidFile.getAbsolutePath());
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    instruments.setExecutable(true);
    return instruments;
  }
}
