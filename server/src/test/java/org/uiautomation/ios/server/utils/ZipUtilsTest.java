package org.uiautomation.ios.server.utils;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.testng.annotations.Test;
import org.uiautomation.ios.SampleApps;

public final class ZipUtilsTest {

  @Test
  public void canExtractAppFromURL() throws IOException {
    String url = SampleApps.getUICatalogZipURL();
    String pathToExtractedApp = ZipUtils.extractAppFromURL(url);
    Assert.assertTrue(pathToExtractedApp, pathToExtractedApp.endsWith("UICatalog.app"));
    Assert.assertTrue(new File(pathToExtractedApp, "Info.plist").exists());
  }
}
