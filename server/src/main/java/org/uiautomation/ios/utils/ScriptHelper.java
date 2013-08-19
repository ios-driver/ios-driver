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

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.server.instruments.communication.CommunicationMode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Concatenate all the small JS files into 1 file passed to instruments, and replace variables by
 * their value.
 *
 * @author freynaud
 */
public class ScriptHelper {

  public static final String ENCODING = "UTF-8";
  private final String main = "instruments-js/main.js";
  private final String json = "instruments-js/json2.js";
  private final String common = "instruments-js/common.js";
  private final String lib0 = "instruments-js/UIAutomation.js";
  private final String lib1 = "instruments-js/UIAKeyboard.js";
  private final String lib2 = "instruments-js/UIAElement.js";
  private final String lib3 = "instruments-js/UIAApplication.js";
  private final String lib4 = "instruments-js/UIATarget.js";
  private final String lib5 = "instruments-js/UIAAlert.js";
  private final String lib6 = "instruments-js/Cache.js";
  private final String lib7 = "instruments-js/SafariPageNavigator.js";
  private final String lib8 = "instruments-js/UIAActionSheet.js";


  private static final String FILE_NAME = "uiamasterscript";

  private String load(String resource) {
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
    StringWriter writer = new StringWriter();
    try {
      IOUtils.copy(is, writer, "UTF-8");
    } catch (IOException e) {
      throw new WebDriverException("Cannot load script :" + resource, e);
    }
    String content = writer.toString();
    return content;
  }


  // TODO freynaud AUT is only used for the capabilies. It should be a response decorator of getCaps
  public String generateScriptContent(int port, String aut, String opaqueKey,
                                      CommunicationMode mode) {
    StringBuilder scriptContent = new StringBuilder();

    String c = load(lib0);
    c = c.replace("$PORT", String.format("%d", port));
    c = c.replace("$AUT", String.format("%s", aut));
    c = c.replace("$MODE", String.format("%s", mode));
    c = c.replace("$SESSION", String.format("%s", opaqueKey));

    scriptContent.append(load(json));
    scriptContent.append(load(common));
    scriptContent.append(load(lib1));
    scriptContent.append(load(lib4));
    scriptContent.append(load(lib3));
    scriptContent.append(load(lib2));

    scriptContent.append(load(lib5));
    scriptContent.append(load(lib6));
    scriptContent.append(load(lib7));
    scriptContent.append(load(lib8));
    scriptContent.append(c);
    scriptContent.append(load(main));
    return scriptContent.toString();
  }

  public File createTmpScript(String content) {
    try {
      File res = File.createTempFile(FILE_NAME, ".js");
      res.deleteOnExit();
      Writer
          writer =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(res), "UTF-8"));
      writer.write(content);
      writer.close();
      return res;
    } catch (Exception e) {
      throw new WebDriverException("Cannot generate script.");
    }

  }

  public File getScript(int port, String aut, String opaqueKey, CommunicationMode mode) {
    try {
      String content = generateScriptContent(port, aut, opaqueKey, mode);
      return createTmpScript(content);
    } catch (Exception e) {
      throw new WebDriverException("cannot generate the script for instrument.", e);
    }
  }

}
