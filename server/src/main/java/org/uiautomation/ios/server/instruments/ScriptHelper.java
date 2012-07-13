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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.uiautomation.ios.exceptions.IOSAutomationSetupException;

public class ScriptHelper {

  private final String main = "wip.js";
  private final String json = "json2.js";
  private final String lib1 = "UIAutomation.js";
  private final String lib2 = "UIAElement.js";

  public ScriptHelper() {}

  private String load(String resource) throws IOException {
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
    if (is ==null){
      throw new IOSAutomationSetupException("cannot load : "+resource);
    }
    StringWriter writer = new StringWriter();
    IOUtils.copy(is, writer, "UTF-8");
    String content = writer.toString();
    return content;
  }

  private String generateScriptContent(int port,String aut) throws IOException {
    StringBuilder scriptContent = new StringBuilder();

    String c = load(lib1);
    c = c.replace("$PORT", String.format("%d", port));
    c = c.replace("$AUT", String.format("%s", aut));

    scriptContent.append(load(json));
    scriptContent.append(c);
    scriptContent.append(load(lib2));

    scriptContent.append(load(main));
    return scriptContent.toString();
  }



  private File createTmpScript(String content) throws IOException {
    // File res = File.createTempFile("uiamasterscript", ".js");
    File res = new File("uiamasterscript.js");
    BufferedWriter out = new BufferedWriter(new FileWriter(res));
    out.write(content);
    out.close();
    return res;

  }

  public File getScript(int port,String aut) throws IOSAutomationSetupException {

    try {
      String content = generateScriptContent(port,aut);
      return createTmpScript(content);
    } catch (Exception e) {
      throw new IOSAutomationSetupException("cannot generate the script for instrument.", e);
    }

  }

}
