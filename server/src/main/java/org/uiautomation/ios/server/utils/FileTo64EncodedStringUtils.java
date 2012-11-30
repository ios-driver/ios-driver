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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebDriverException;

public class FileTo64EncodedStringUtils {

  private final File source;

  public FileTo64EncodedStringUtils(File source) {
    this.source = source;
    try {
      waitForFileToAppearOnDisk();
    } catch (InterruptedException e) {
      throw new WebDriverException(
          "Interrupted waiting for the screenshot to be written on disk.", e);
    }
  }

  private void waitForFileToAppearOnDisk() throws InterruptedException {

    int cpt = 0;
    while (!source.exists()) {
      Thread.sleep(250);
      cpt++;
      if (cpt > 5 * 4) {
        throw new WebDriverException("timeout waiting for screenshot file to be written.");
      }
    }
    return;
  }

  public String encode() throws IOException {
    FileInputStream is;
    try {
      is = new FileInputStream(source);
    } catch (FileNotFoundException e) {
      // ignore. File presence has been checked by waitForFileToAppearOnDisk.
      throw new RuntimeException("something else deleting tmp screenshot ?", e);
    }
    byte[] img = IOUtils.toByteArray(is);
    String s = Base64.encodeBase64String(img);
    return s;
  }
}
