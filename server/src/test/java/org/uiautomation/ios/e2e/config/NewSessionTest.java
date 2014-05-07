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

package org.uiautomation.ios.e2e.config;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.utils.ClassicCommands;
import org.uiautomation.ios.utils.IOSVersion;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import static org.uiautomation.ios.IOSCapabilities.BUNDLE_NAME;
import static org.uiautomation.ios.IOSCapabilities.DEVICE;
import static org.uiautomation.ios.IOSCapabilities.LANGUAGE;
import static org.uiautomation.ios.IOSCapabilities.LOCALE;

public class NewSessionTest extends BaseIOSDriverTest {

  @AfterMethod(alwaysRun = true)
  public void afterMethod() throws Exception {
    stopDriver();
  }

  @Test
  public void base() {
    driver = new RemoteIOSDriver(getRemoteURL(), SampleApps.uiCatalogCap());
    IOSCapabilities cap = IOSCapabilities.iphone("UICatalog", "2.10");
    String sdk = cap.getSDKVersion();
    if (sdk == null) {
      sdk = ClassicCommands.getDefaultSDK();
    }
    IOSCapabilities actual = driver.getCapabilities();
    Assert.assertEquals(actual.getBundleId(), "com.yourcompany.UICatalog");
    Assert.assertEquals(actual.getBundleVersion(), "2.10");
    Assert.assertEquals(actual.getSDKVersion(), sdk);
  }

  @Test
  public void noVersion() {
    driver = new RemoteIOSDriver(getRemoteURL(), SampleApps.uiCatalogCap());

    IOSCapabilities actual = driver.getCapabilities();
    Assert.assertEquals(actual.getBundleId(), "com.yourcompany.UICatalog");
    Assert.assertEquals(actual.getBundleVersion(), "2.10");
  }

  @Test
  public void appWithNoContentCanStart() throws Exception {
    driver = new RemoteIOSDriver(getRemoteURL(), SampleApps.noContentCap());
    IOSCapabilities actual = driver.getCapabilities();
    Assert.assertEquals(actual.getBundleId(), "freynaud.testNoContent");
    Assert.assertEquals(actual.getBundleVersion(), "1.0");

    try {
      driver.findElement(By.xpath("//*[@name=l10n('test')]"));
      Assert.fail("cannot use l10n features on an app with no content.");
    } catch (WebDriverException expected) {
      // expected
    }
  }

  @Test
  public void startDefaultLanguageLocale() {
    IOSCapabilities capabilitiesNoLanguageNoLocale;
    capabilitiesNoLanguageNoLocale = SampleConfig.uiCatalogCapNoLangNoLocale();
    driver = new RemoteIOSDriver(getRemoteURL(), capabilitiesNoLanguageNoLocale);
    IOSCapabilities actual = driver.getCapabilities();

    Assert.assertEquals(actual.getBundleId(), "com.yourcompany.UICatalog");
    Assert.assertEquals(actual.getBundleVersion(), "2.10");
    Assert.assertEquals(actual.getLanguage(), "en");
    Assert.assertEquals(actual.getLocale(), "en_GB");
  }

  @Test
  public void startSpecifiedLanguageLocale() {
    IOSCapabilities cap = IOSCapabilities.iphone("InternationalMountains");
    cap.setLanguage("fr");
    cap.setLocale("es");
    driver = new RemoteIOSDriver(getRemoteURL(), cap);

    IOSCapabilities actual = driver.getCapabilities();
    Assert.assertEquals(actual.getBundleId(), "com.yourcompany.InternationalMountains");
    Assert.assertEquals(actual.getBundleVersion(), "1.1");
    // default to UK Assert.assertEquals(target.getLanguage(), "fr");
    Assert.assertEquals(actual.getLocale(), "es");
  }

  // TODO freynaud should load english instead ?
  @Test(expectedExceptions = SessionNotCreatedException.class)
  public void recognizeUnsupportedLanguageLocale() {
    IOSCapabilities cap = IOSCapabilities.iphone("InternationalMountains");
    cap.setLanguage("es");
    cap.setLocale("es");
    driver = new RemoteIOSDriver(getRemoteURL(), cap);
  }

  @Test(expectedExceptions = SessionNotCreatedException.class)
  public void doesntExist() {
    driver = new RemoteIOSDriver(getRemoteURL(), IOSCapabilities.iphone("ferret", "2.10"));
  }

  @Test(expectedExceptions = SessionNotCreatedException.class)
  public void sdkTooOld() {
    IOSCapabilities cap = SampleApps.uiCatalogCap();
    cap.setSDKVersion("4.3");
    driver = new RemoteIOSDriver(getRemoteURL(), cap);
  }

  @Test(expectedExceptions = SessionNotCreatedException.class)
  public void wrongVersion() {
    driver =
        new RemoteIOSDriver(getRemoteURL(), IOSCapabilities.iphone("UICatalog", "not a number."));
  }

  @Test(expectedExceptions = SessionNotCreatedException.class)
  public void wrongSDK() {
    IOSCapabilities cap = IOSCapabilities.iphone("InternationalMountains");
    cap.setSDKVersion("17");
    driver = new RemoteIOSDriver(getRemoteURL(), cap);
  }

  @Test
  public void correctSDK() {
    IOSCapabilities cap = IOSCapabilities.iphone("InternationalMountains");
    String sdk = ClassicCommands.getDefaultSDK();
    cap.setSDKVersion(sdk);
    driver = new RemoteIOSDriver(getRemoteURL(), cap);
    IOSCapabilities actual = driver.getCapabilities();
    Assert.assertEquals(actual.getSDKVersion(), sdk);
  }

  @Test
  public void supportAllInstalledSDKs() {
    List<String> sdks = ClassicCommands.getInstalledSDKs();
    for (String sdk : sdks) {

      if (new IOSVersion(sdk).isGreaterOrEqualTo("5.0")) {

        try {
          IOSCapabilities cap = IOSCapabilities.iphone("InternationalMountains");
          cap.setSDKVersion(sdk);

          driver = new RemoteIOSDriver(getRemoteURL(), cap);
          IOSCapabilities actual = driver.getCapabilities();

          Assert.assertEquals(actual.getSDKVersion(), sdk);
        } finally {
          if (driver != null) {
            driver.quit();
            driver = null;
          }
        }
      }
    }
  }

  @Test
  public void correctDevice() {
    IOSCapabilities cap = IOSCapabilities.iphone("UICatalog");
    driver = new RemoteIOSDriver(getRemoteURL(), cap);
    IOSCapabilities actual = driver.getCapabilities();
    Assert.assertEquals(actual.getDevice(), DeviceType.iphone);

    driver.quit();

    cap = IOSCapabilities.ipad("UICatalog");
    driver = new RemoteIOSDriver(getRemoteURL(), cap);
    actual = driver.getCapabilities();
    Assert.assertEquals(actual.getDevice(), DeviceType.ipad);
  }

  @Test
  public void canUseAnyFlagFromInfoPlistMatches() {
    IOSCapabilities cap = IOSCapabilities.iphone("UICatalog");
    cap.setCapability(IOSCapabilities.MAGIC_PREFIX + "CFBundleDevelopmentRegion", "en");
    driver = new RemoteIOSDriver(getRemoteURL(), cap);
    IOSCapabilities actual = driver.getCapabilities();
    Assert.assertEquals(actual.getBundleId(), "com.yourcompany.UICatalog");
    Assert.assertEquals(actual.getBundleVersion(), "2.10");
  }

  @DataProvider(name = "capabilities")
  public Object[][] createData1() {
    return new Object[][]{

        {DeviceType.iphone, DeviceVariation.iPhone, 320, 480},
        {DeviceType.iphone, DeviceVariation.iPhoneRetina, 640, 960},
        {DeviceType.iphone, DeviceVariation.iPhoneRetina_4inch, 640, 1136},
        {DeviceType.iphone, DeviceVariation.iPhoneRetina_4inch_64bit, 640, 1136},
        {DeviceType.ipad, DeviceVariation.iPad, 768, 1024},
        {DeviceType.ipad, DeviceVariation.iPadRetina, 1536, 2048},
        {DeviceType.ipad, DeviceVariation.iPadRetina_64bit, 1536, 2048},

    };
  }

  @Test(dataProvider = "capabilities")
  public void supportApplicationWithMultipleDeviceFamily(DeviceType device,
                                                         DeviceVariation variation,
                                                         int expectedW,
                                                         int expectedH) throws Exception {

    String sdk = ClassicCommands.getDefaultSDK();
    if (!DeviceVariation.compatibleWithSDKVersion(device, variation, sdk)) {
      return;
    }
    IOSCapabilities cap = new IOSCapabilities();

    cap.setCapability(DEVICE, device);
    cap.setDeviceVariation(variation);

    cap.setCapability(LANGUAGE, "es");
    cap.setCapability(LOCALE, "en_GB");
    cap.setCapability(BUNDLE_NAME, "Safari");

    // normal iphone
    driver = new RemoteIOSDriver(getRemoteURL(), cap);
    Capabilities actual = driver.getCapabilities();

    driver.switchTo().window("Web");
    driver.get(getRemoteURL() + "/status");

    for (Orientation o : Orientation.values()) {
      if (o == Orientation.UIA_DEVICE_ORIENTATION_FACEUP
          || o == Orientation.UIA_DEVICE_ORIENTATION_FACEDOWN
          || (o == Orientation.UIA_DEVICE_ORIENTATION_PORTRAIT_UPSIDEDOWN
              && device == DeviceType.iphone)) {
        continue;
      }
      driver.rotate(o);
    }

    Assert.assertEquals(actual.getCapability(DEVICE), device.toString());

    File f = driver.getScreenshotAs(OutputType.FILE);

    BufferedImage bimg = ImageIO.read(f);
    Assert.assertTrue(bimg.getWidth() == expectedW || bimg.getHeight() == expectedW);
    Assert.assertTrue(bimg.getWidth() == expectedH || bimg.getHeight() == expectedH);
  }
}
