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
package org.uiautomation.ios;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;


public class SampleApps {

  private static final Logger log = Logger.getLogger(SampleApps.class.getName());

  private static final String uiCatalog = "/sampleApps/UICatalog.app";
  private static final String uiCatalogZip = "/sampleApps/UICatalog.zip";
  private static final String uiCatalogiPad = "/sampleApps/UICatalogiPad.app";
  private static final String intlMountains = "/sampleApps/InternationalMountains.app";
  private static final String geocoder = "/sampleApps/Geocoder.app";
  private static final String noContent = "/sampleApps/testNoContent.app";
  private static final String ppNQASampleApp = "/sampleApps/PPNQASampleApp.app";

  private static final String sdkVersion = System.getProperty("SDK", null);


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

  public static String getGeocoderFile() {
    return getFromClassPath(geocoder).getAbsolutePath();
  }

  public static String gettestNoContentFile() {
    return getFromClassPath(noContent).getAbsolutePath();
  }

  public static String getUICatalogFile() {
    return getFromClassPath(uiCatalog).getAbsolutePath();
  }
  
  public static URL getUICatalogZipURL() throws MalformedURLException {
    String s =  "file://" + getFromClassPath(uiCatalogZip).getAbsolutePath();
    return new URL(s);
  }

  public static String getIntlMountainsFile() {
    return getFromClassPath(intlMountains).getAbsolutePath();
  }

  public static String getUICatalogIpad() {
    return getFromClassPath(uiCatalogiPad).getAbsolutePath();
  }

  public static String getPPNQASampleApp() {
    return getFromClassPath(ppNQASampleApp).getAbsolutePath();
  }

  public static IOSCapabilities geoCoderCap() {
    IOSCapabilities c = IOSCapabilities.iphone("Geocoder");
    c.setCapability(IOSCapabilities.LOCALE, "en");

    if (sdkVersion != null) {
      log.info("SET SDK to " + sdkVersion);
      c.setSDKVersion(sdkVersion);
    }
    return c;
  }

  public static IOSCapabilities uiCatalogCap() {
    IOSCapabilities c = IOSCapabilities.iphone("UICatalog", "11.3");
    if (sdkVersion != null) {
      log.info("SET SDK to " + sdkVersion);
      c.setSDKVersion(sdkVersion);
    }
    return c;
  }

  public static IOSCapabilities ppNQASampleAppCap() {
    IOSCapabilities c = IOSCapabilities.ipad("PPNQASampleApp");
    if (sdkVersion != null) {
      log.info("SET SDK to " + sdkVersion);
      c.setSDKVersion(sdkVersion);
    }
    return c;
  }

  public static IOSCapabilities noContentCap() {
    IOSCapabilities c = IOSCapabilities.iphone("testNoContent");
    if (sdkVersion != null) {
      log.info("SET SDK to " + sdkVersion);
      c.setSDKVersion(sdkVersion);
    }
    return c;
  }

  public static IOSCapabilities uiCatalogipadCap() {
    IOSCapabilities c = IOSCapabilities.ipad("UICatalog");
    if (sdkVersion != null) {
      log.info("SET SDK to " + sdkVersion);
      c.setSDKVersion(sdkVersion);
    }
    return c;
  }

  public static IOSCapabilities intlMountainsCap(String lang) {
    IOSCapabilities c = IOSCapabilities.iphone("InternationalMountains", "1.3");
    c.setLanguage(lang);
    if (sdkVersion != null) {
      log.info("SET SDK to " + sdkVersion);
      c.setSDKVersion(sdkVersion);
    }
    return c;
  }


}
