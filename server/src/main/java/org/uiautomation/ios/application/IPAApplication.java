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

package org.uiautomation.ios.application;

import org.libimobiledevice.ios.driver.binding.model.ProvisioningProfileInfo;
import org.libimobiledevice.ios.driver.binding.services.ProvisioningService;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.IOSCapabilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class IPAApplication extends APPIOSApplication {

  private final File ipa;
  private static final Logger log = Logger.getLogger(IPAApplication.class.getName());

  protected IPAApplication(File ipa, String pathToApp) {
    super(pathToApp);
    this.ipa = ipa;
  }

  public static IPAApplication createFrom(File ipa) {
    File extracted = getExtractedFolder(ipa);
    if (!extracted.exists()) {
      long deadline = System.currentTimeMillis() + 20 * 1000;
      while (System.currentTimeMillis() < deadline) {
        try {
          extractFolder(ipa);
          break;
        } catch (IOException e) {
          try {
            Thread.sleep(2000);
          } catch (InterruptedException e1) {
            // ignore.
          }
          log.warning("Cannot unzip" + ipa + ". Could be ok, if the app is being copied.");
        }
      }
    }
    if (!extracted.exists()) {
      throw new WebDriverException("couldn't unzip the app :" + ipa);
    }
    File app = getAssociatedApp(ipa);
    IPAApplication res = new IPAApplication(ipa, app.getAbsolutePath());
    log.warning(ipa + "app added.");
    return res;
  }

  private static File getExtractedFolder(File ipa) {
    return new File(ipa.getAbsoluteFile() + ".unzipped");
  }

  public String toString() {
    return ".IPA:" + ipa.getAbsolutePath();
  }

  private static File getAssociatedApp(File ipa) {
    File payload = new File(getExtractedFolder(ipa), "payload");
    File[] apps = payload.listFiles();

    File res = null;
    for (File f : apps) {
      if (f.getAbsolutePath().endsWith(".app")) {
        res = f;
      }
    }
    if (res == null) {
      throw new WebDriverException("Cannot find the .app in the unzipped app");
    }
    return res;
  }

  private static String extractFolder(File zipFile) throws ZipException, IOException {
    int BUFFER = 2048;

    ZipFile zip = new ZipFile(zipFile);
    String newPath = getExtractedFolder(zipFile).getAbsolutePath();

    new File(newPath).mkdir();
    Enumeration zipFileEntries = zip.entries();

    // Process each entry
    while (zipFileEntries.hasMoreElements()) {
      // grab a zip file entry
      ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
      String currentEntry = entry.getName();
      File destFile = new File(newPath, currentEntry);
      //destFile = new File(newPath, destFile.getName());
      File destinationParent = destFile.getParentFile();

      // create the parent directory structure if needed
      destinationParent.mkdirs();

      if (!entry.isDirectory()) {
        BufferedInputStream is = new BufferedInputStream(zip
                                                             .getInputStream(entry));
        int currentByte;
        // establish buffer for writing file
        byte data[] = new byte[BUFFER];

        // write the current file to disk
        FileOutputStream fos = new FileOutputStream(destFile);
        BufferedOutputStream dest = new BufferedOutputStream(fos,
                                                             BUFFER);

        // read and write until last byte is encountered
        while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
          dest.write(data, 0, currentByte);
        }
        dest.flush();
        dest.close();
        is.close();
      }

      if (currentEntry.endsWith(".zip")) {
        // found a zip file, try to open
        extractFolder(destFile);
      }
    }
    return newPath;
  }


  public File getIPAFile() {
    return ipa;
  }

  public File getEmbeddedProfile() {
    return new File(getApplicationPath(),"embedded.mobileprovision");
  }

  @Override
  public IOSCapabilities getCapabilities(){
    IOSCapabilities res = super.getCapabilities();
    try {
      ProvisioningProfileInfo info = ProvisioningService.getProfile(getEmbeddedProfile());
      res.setCapability(IOSCapabilities.PROVISIONNED,info.getDevices());
    } catch (Exception e) {
      log.warning("Cannot extract profile. corrupted ipa ?");
    } finally {
      return res;
    }
  }
}
