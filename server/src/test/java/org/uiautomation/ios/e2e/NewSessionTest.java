package org.uiautomation.ios.e2e;

import java.util.List;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.communication.IOSDevice;
import org.uiautomation.ios.server.utils.ClassicCommands;

public class NewSessionTest extends BaseIOSDriverTest {

  @Test
  public void base() {
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(getRemoteURL(), SampleApps.uiCatalogCap());
      IOSCapabilities cap = IOSCapabilities.iphone("UICatalog", "2.10");
      String sdk = cap.getSDKVersion();
      if (sdk == null) {
        sdk = ClassicCommands.getDefaultSDK();
      }
      IOSCapabilities actual = driver.getCapabilities();
      Assert.assertEquals(actual.getBundleId(), "com.yourcompany.UICatalog");
      Assert.assertEquals(actual.getBundleVersion(), "2.10");
      Assert.assertEquals(actual.getSDKVersion(), sdk);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void noVersion() {
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(getRemoteURL(), SampleApps.uiCatalogCap());

      IOSCapabilities actual = driver.getCapabilities();
      Assert.assertEquals(actual.getBundleId(), "com.yourcompany.UICatalog");
      Assert.assertEquals(actual.getBundleVersion(), "2.10");
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void startDefaultLanguageLocale() {
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(getRemoteURL(), SampleApps.uiCatalogCap());

      IOSCapabilities actual = driver.getCapabilities();
      Assert.assertEquals(actual.getBundleId(), "com.yourcompany.UICatalog");
      Assert.assertEquals(actual.getBundleVersion(), "2.10"); // default to UK
      Assert.assertEquals(actual.getLanguage(), "en");
      Assert.assertEquals(actual.getLocale(), "en_GB");
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }

  }

  @Test
  public void startSpecifiedLanguageLocale() {
    RemoteUIADriver driver = null;
    try {
      IOSCapabilities cap = IOSCapabilities.iphone("InternationalMountains");
      cap.setLanguage("fr");
      cap.setLocale("es");
      driver = new RemoteUIADriver(getRemoteURL(), cap);

      IOSCapabilities actual = driver.getCapabilities();
      Assert.assertEquals(actual.getBundleId(), "com.yourcompany.InternationalMountains");
      Assert.assertEquals(actual.getBundleVersion(), "1.1");
      // default to UK Assert.assertEquals(target.getLanguage(), "fr");
      Assert.assertEquals(actual.getLocale(), "es");
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test(expectedExceptions = SessionNotCreatedException.class)
  public void recognizeUnsupportedLanguageLocale() {
    RemoteUIADriver driver = null;
    try {
      IOSCapabilities cap = IOSCapabilities.iphone("InternationalMountains");
      cap.setLanguage("es");
      cap.setLocale("es");
      driver = new RemoteUIADriver(getRemoteURL(), cap);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }

  }

  @Test(expectedExceptions = SessionNotCreatedException.class)
  public void doesntExist() {
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(getRemoteURL(), IOSCapabilities.iphone("ferret", "2.10"));
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
  
  
  @Test(expectedExceptions = SessionNotCreatedException.class)
  public void sdkTooOld() {
    RemoteUIADriver driver = null;
    try {
      IOSCapabilities cap = SampleApps.uiCatalogCap();
      cap.setSDKVersion("4.3");
      driver = new RemoteUIADriver(getRemoteURL(), cap);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test(expectedExceptions = SessionNotCreatedException.class)
  public void wrongVersion() {
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(getRemoteURL(), IOSCapabilities.iphone("UICatalog", "not a number."));
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test(expectedExceptions = SessionNotCreatedException.class)
  public void wrongSDK() {
    RemoteUIADriver driver = null;
    try {
      IOSCapabilities cap = IOSCapabilities.iphone("InternationalMountains");
      cap.setSDKVersion("17");
      driver = new RemoteUIADriver(getRemoteURL(), cap);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void correctSDK() {
    RemoteUIADriver driver = null;
    try {
      IOSCapabilities cap = IOSCapabilities.iphone("InternationalMountains");
      String sdk = ClassicCommands.getDefaultSDK();
      cap.setSDKVersion(sdk);
      driver = new RemoteUIADriver(getRemoteURL(), cap);
      IOSCapabilities actual = driver.getCapabilities();

      Assert.assertEquals(actual.getSDKVersion(), sdk);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void supportAllInstalledSDKs() {
    RemoteUIADriver driver = null;
    List<String> sdks = ClassicCommands.getInstalledSDKs();
    System.out.println(sdks);
    for (String sdk : sdks) {
      Float version = Float.parseFloat(sdk);
      if (version >= 5L) {

        try {
          IOSCapabilities cap = IOSCapabilities.iphone("InternationalMountains");
          cap.setSDKVersion(sdk);

          driver = new RemoteUIADriver(getRemoteURL(), cap);
          IOSCapabilities actual = driver.getCapabilities();

          Assert.assertEquals(actual.getSDKVersion(), sdk);
        } finally {
          if (driver != null) {
            driver.quit();
          }
        }
      }
    }
  }

  @Test
  public void correctDevice() {
    RemoteUIADriver driver = null;
    try {
      IOSCapabilities cap = IOSCapabilities.iphone("UICatalog");
      driver = new RemoteUIADriver(getRemoteURL(), cap);
      IOSCapabilities actual = driver.getCapabilities();
      Assert.assertEquals(actual.getDevice(), IOSDevice.iPhoneSimulator);

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }

    try {
      IOSCapabilities cap = IOSCapabilities.ipad("UICatalog");
      driver = new RemoteUIADriver(getRemoteURL(), cap);
      IOSCapabilities actual = driver.getCapabilities();
      Assert.assertEquals(actual.getDevice(), IOSDevice.iPadSimulator);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void canUseAnyFlagFromInfoPlistMatches() {
    IOSCapabilities cap = IOSCapabilities.iphone("UICatalog");
    cap.setCapability(IOSCapabilities.MAGIC_PREFIX + "CFBundleDevelopmentRegion", "en");
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(getRemoteURL(), cap);
      IOSCapabilities actual = driver.getCapabilities();
      Assert.assertEquals(actual.getBundleId(), "com.yourcompany.UICatalog");
      Assert.assertEquals(actual.getBundleVersion(), "2.10");
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  /*
   * @Test(expectedExceptions = SessionNotCreatedException.class) public void
   * canUseAnyFlagFromInfoPlistNoMatch() { IOSCapabilities cap =
   * IOSCapabilities.iphone("UICatalog");
   * cap.setCapability(IOSCapabilities.MAGIC_PREFIX +
   * "CFBundleDevelopmentRegion", "de"); RemoteUIADriver driver = null; try {
   * driver = new RemoteUIADriver("http://" + config.getHost() + ":" +
   * config.getPort() + "/wd/hub", cap); UIATarget target =
   * driver.getLocalTarget(); UIAApplication app = target.getFrontMostApp();
   * Assert.assertEquals(app.getBundleId(), "com.yourcompany.UICatalog");
   * Assert.assertEquals(app.getBundleVersion(), "2.10"); } finally { if (driver
   * != null) { driver.quit(); } } }
   * 
   * @Test public void ignoreCapabilitiesWithoutMagicPrefix() { IOSCapabilities
   * cap = IOSCapabilities.iphone("UICatalog");
   * cap.setCapability("CFBundleDevelopmentRegion", "de"); RemoteUIADriver
   * driver = null; try { driver = new RemoteUIADriver("http://" +
   * config.getHost() + ":" + config.getPort() + "/wd/hub", cap); UIATarget
   * target = driver.getLocalTarget(); UIAApplication app =
   * target.getFrontMostApp(); Assert.assertEquals(app.getBundleId(),
   * "com.yourcompany.UICatalog"); Assert.assertEquals(app.getBundleVersion(),
   * "2.10"); } finally { if (driver != null) { driver.quit(); } } }
   */

  @Test
  public void supportApplicationWithMultipleDeviceFamily() {
    IOSCapabilities cap = IOSCapabilities.iphone("Safari");
    RemoteWebDriver driver = null;
    try {
      driver = new RemoteWebDriver(getRemoteURL(), cap);
      Capabilities actual = driver.getCapabilities();
      Assert.assertEquals(actual.getCapability(IOSCapabilities.DEVICE), "iPhone Simulator");

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
    cap = IOSCapabilities.ipad("Safari");
    driver = null;
    try {
      driver = new RemoteWebDriver(getRemoteURL(), cap);
      Capabilities actual = driver.getCapabilities();
      Assert.assertEquals(actual.getCapability(IOSCapabilities.DEVICE), "iPad Simulator");

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

}
