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
package org.uiautomation.ios;

import java.io.File;
import java.net.URL;

import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.server.application.Localizable;

// TODO freynaud move to tests
public class SampleApps {


  private static final String uiCatalog = "/sampleApps/UICatalog.app";
  private static final String intlMountains = "/sampleApps/InternationalMountains.app";



  private static File getFromClassPath(String resource) {
    URL url = SampleApps.class.getResource(resource);
    File res = null;
    if (url.toExternalForm().startsWith("file:")) {
      res = new File(url.toExternalForm().replace("file:", ""));
    }

    if (res == null || !res.exists()) {
      throw new RuntimeException("Cannot load test app from " + url);
    }
    return res;
  }

  public static String getUICatalogApp() {
    return getFromClassPath(uiCatalog).getAbsolutePath();
  }

  public static String getIntlMountainsApp() {
    return getFromClassPath(intlMountains).getAbsolutePath();
  }

  public static IOSCapabilities uiCatalogCap() {
    IOSCapabilities c = IOSCapabilities.iphone("UICatalog","2.10");
    c.setCapability(IOSCapabilities.TIME_HACK, true);
    return c;
  }

  public static IOSCapabilities intlMountainsCap(Localizable l) {
    IOSCapabilities c = IOSCapabilities.iphone(SampleApps.getIntlMountainsApp());
    c.setLanguage(l.getName());
    c.setCapability(IOSCapabilities.TIME_HACK, false);
    return c;
  }
}
