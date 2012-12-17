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
import java.util.logging.Logger;

import org.uiautomation.ios.server.application.AppleLocale;


public class SampleApps {

  private static final Logger log = Logger.getLogger(SampleApps.class.getName());

  private static final String uiCatalog = "/sampleApps/UICatalog.app";
  private static final String uiCatalogiPad = "/sampleApps/UICatalogiPad.app";
  private static final String intlMountains = "/sampleApps/InternationalMountains.app";
  private static final String geocoder = "/sampleApps/Geocoder.app";

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

  public static String getUICatalogFile() {
    return getFromClassPath(uiCatalog).getAbsolutePath();
  }

  public static String getIntlMountainsFile() {
    return getFromClassPath(intlMountains).getAbsolutePath();
  }

  public static String getUICatalogIpad() {
    return getFromClassPath(uiCatalogiPad).getAbsolutePath();
  }

  public static IOSCapabilities geoCoderCap() {
    IOSCapabilities c = IOSCapabilities.iphone("Geocoder");
    c.setCapability(IOSCapabilities.LOCALE, "en");

    c.setCapability(IOSCapabilities.TIME_HACK, false);
    if (sdkVersion != null) {
      log.info("SET SDK to " + sdkVersion);
      c.setSDKVersion(sdkVersion);
    }
    return c;
  }

  public static IOSCapabilities uiCatalogCap() {
    IOSCapabilities c = IOSCapabilities.iphone("UICatalog", "2.10");
    c.setCapability(IOSCapabilities.TIME_HACK, false);
    if (sdkVersion != null) {
      log.info("SET SDK to " + sdkVersion);
      c.setSDKVersion(sdkVersion);
    }
    return c;
  }

  public static IOSCapabilities uiCatalogipadCap() {
    IOSCapabilities c = IOSCapabilities.ipad("UICatalog");
    c.setCapability(IOSCapabilities.TIME_HACK, false);
    if (sdkVersion != null) {
      log.info("SET SDK to " + sdkVersion);
      c.setSDKVersion(sdkVersion);
    }
    return c;
  }

  public static IOSCapabilities intlMountainsCap(String lang) {
    IOSCapabilities c = IOSCapabilities.iphone("InternationalMountains", "1.1");
    c.setLanguage(lang);
    c.setCapability(IOSCapabilities.TIME_HACK, false);
    if (sdkVersion != null) {
      log.info("SET SDK to " + sdkVersion);
      c.setSDKVersion(sdkVersion);
    }
    return c;
  }
}
