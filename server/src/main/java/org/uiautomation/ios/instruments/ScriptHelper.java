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

package org.uiautomation.ios.instruments;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.instruments.commandExecutor.CommunicationMode;

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

  private final ImmutableList<String> libs = ImmutableList.of(
      "instruments-js/json2.js",
      "instruments-js/common.js",
      "instruments-js/UIAKeyboard.js",
      "instruments-js/UIATarget.js",
      "instruments-js/UIAApplication.js",
      "instruments-js/UIAElement.js",
      "instruments-js/UIATextView.js",
      "instruments-js/UIAAlert.js",
      "instruments-js/Cache.js",
      "instruments-js/SafariPageNavigator.js",
      "instruments-js/UIAActionSheet.js",
      "instruments-js/UIAutomation.js",
      "instruments-js/main.js"
  );

  private static final String FILE_NAME = "uiamasterscript";

  private String load(String resource) {
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
    StringWriter writer = new StringWriter();
    try {
      IOUtils.copy(is, writer, ENCODING);
    } catch (IOException e) {
      throw new WebDriverException("Cannot load script :" + resource, e);
    }
    String content = writer.toString();
    return content;
  }

  // TODO freynaud AUT is only used for the capabilities. It should be a response decorator of getCaps
  public String generateScriptContent(int port, String aut, String opaqueKey,
                                      CommunicationMode mode,boolean acceptSslCerts) {
    StringBuilder scriptContent = new StringBuilder();

    scriptContent.append(Joiner.on("\n").join(
        "/* Script configuration parameters. */",
        String.format("var CONFIG_PORT = %d;", port),
        String.format("var CONFIG_AUT = \"%s\";", aut),
        String.format("var CONFIG_MODE = \"%s\";", mode),
        String.format("var CONFIG_SESSION = \"%s\";", opaqueKey),
        String.format("var ACCEPT_SSL_CERTS = %s;", acceptSslCerts),
        ""
    ));

    for (String lib : libs) {
      scriptContent.append(load(lib));
    }

    return scriptContent.toString();
  }

  public File createTmpScript(String content) {
    try {
      File res = File.createTempFile(FILE_NAME, ".js");
      res.deleteOnExit();
      Writer
          writer =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(res), ENCODING));
      writer.write(content);
      writer.close();
      return res;
    } catch (Exception e) {
      throw new WebDriverException("Cannot generate script.");
    }
  }

  public File getScript(int port, String aut, String opaqueKey, CommunicationMode mode,boolean acceptSslCerts) {
    try {
      String content = generateScriptContent(port, aut, opaqueKey, mode,acceptSslCerts);
      return createTmpScript(content);
    } catch (Exception e) {
      throw new WebDriverException("cannot generate the script for instrument.", e);
    }
  }
}
