package org.uiautomation.ios.server.command.impl.decorators;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.uiautomation.ios.exceptions.IOSAutomationException;

public class FileTo64EncodedStringUtils {

  private final File source;

  public FileTo64EncodedStringUtils(File source) {
    this.source = source;
    try {
      waitForFileToAppearOnDisk();
    } catch (InterruptedException e) {
      throw new IOSAutomationException(
          "Interrupted waiting for the screenshot to be written on disk.", e);
    }
  }

  private void waitForFileToAppearOnDisk() throws InterruptedException {

    int cpt = 0;
    while (!source.exists()) {
      Thread.sleep(250);
      cpt++;
      if (cpt > 5 * 4) {
        throw new IOSAutomationException("timeout waiting for screenshot file to be written.");
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
