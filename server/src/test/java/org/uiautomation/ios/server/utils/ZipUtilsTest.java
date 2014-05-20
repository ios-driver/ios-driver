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

package org.uiautomation.ios.server.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.Assert;

import org.testng.annotations.Test;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.utils.ZipUtils;

public final class ZipUtilsTest {

  @Test
  public void canExtractAppFromURL() throws IOException {
    URL url = SampleApps.getUICatalogZipURL();
    File appFile = ZipUtils.extractAppFromURL(url);
    Assert.assertTrue(appFile.getAbsolutePath(), appFile.getName().endsWith("UICatalog.app"));
    Assert.assertTrue(new File(appFile, "Info.plist").exists());
  }
}
