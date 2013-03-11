package org.uiautomation.ios.server.application;

import org.openqa.selenium.WebDriverException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class IPAApplication extends APPIOSApplication {

  private final File ipa;

  private IPAApplication(File ipa, String pathToApp) {
    super(pathToApp);
    this.ipa = ipa;
  }

  public static IPAApplication createFrom(File ipa) {
    File extracted = getExtractedFolder(ipa);
    if (!extracted.exists()) {
      try {
        extractFolder(ipa);
      } catch (IOException e) {
        throw new WebDriverException("Cannot unzip the ipa file.", e);
      }
    }
    File app = getAssociatedApp(ipa);
    IPAApplication res = new IPAApplication(ipa, app.getAbsolutePath());
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
}
