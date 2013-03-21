package org.uiautomation.ios.server.utils;

import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public final class ZipUtils {

  private static final Logger log = Logger.getLogger(ZipUtils.class.getName());

  /**
   * Downloads zip from from url, extracts it into tmp dir and returns path to
   * .app directory
   * 
   * @param url
   *          url to zipped .app
   * @return path to the .app directory
   */
  public static String extractAppFromURL(String url) throws IOException {
    URL theUrl = new URL(url);

    // extract app name from URL
    if (!url.toLowerCase().endsWith(".zip"))
      throw new IllegalArgumentException("expects: .../AppName.zip format: " + url);
    String appName = url.substring(url.lastIndexOf('/'), url.length() - 4);

    File tmpDir = createTmpDir(appName);
    log.fine("tmpDir: " + tmpDir.getAbsolutePath());

    File zipFile = new File(tmpDir, appName + ".zip");
    FileUtils.copyURLToFile(theUrl, zipFile);

    unzip(zipFile, tmpDir);

    String pathToExtractedApp = new File(tmpDir, appName + ".app").getAbsolutePath();
    log.info("extractAppFromURL: " + url + " -> " + pathToExtractedApp);
    return pathToExtractedApp;
  }

  //

  public static void unzip(File zipFile, File targetDir) throws IOException {
    String targetDirPath = targetDir.getAbsolutePath() + '/';
    if (!targetDir.exists())
      targetDir.mkdirs();

    ZipFile zip = new ZipFile(zipFile);
    try {
      for (ZipEntry entry : Collections.list(zip.entries())) {
        if (entry.isDirectory())
          continue;
        InputStream input = zip.getInputStream(entry);
        try {
          File target = new File(targetDirPath + entry.getName());
          FileUtils.copyInputStreamToFile(input, target);
        } finally {
          IOUtils.closeQuietly(input);
        }
      }
    } finally {
      zip.close();
    }
  }

  private static File createTmpDir(String prefix) throws IOException {
    File tmp = File.createTempFile(prefix, null);
    tmp.delete();
    tmp.mkdir();
    tmp.deleteOnExit();
    return tmp;
  }
}
