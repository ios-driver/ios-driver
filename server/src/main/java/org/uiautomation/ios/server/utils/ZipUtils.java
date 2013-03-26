package org.uiautomation.ios.server.utils;

import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebDriverException;

public final class ZipUtils {

  private static final Logger log = Logger.getLogger(ZipUtils.class.getName());

  /**
   * Downloads zip from from url, extracts it into tmp dir and returns path to
   * extracted directory
   * 
   * @param url
   *          url to zipped .app or .ipa
   * @return .app or .ipa file
   */
  public static File extractAppFromURL(String url) throws IOException {
    String fileName = url.substring(url.lastIndexOf('/') + 1);
    URL theUrl = new URL(url);

    File tmpDir = createTmpDir("iosd");
    log.fine("tmpDir: " + tmpDir.getAbsolutePath());

    File downloadedFile = new File(tmpDir, fileName);
    FileUtils.copyURLToFile(theUrl, downloadedFile);

    if (fileName.endsWith(".ipa"))
      return downloadedFile;
    
    unzip(downloadedFile, tmpDir);
    
    for (File file: tmpDir.listFiles()) {
      if (file.getName().endsWith(".app")) {
        return file;
      }
    }
    
    throw new WebDriverException("cannot extract .app/.ipa from " + url);
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
