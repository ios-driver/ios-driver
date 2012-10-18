package org.uiautomation.ios.e2e;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAApplication;
import org.uiautomation.ios.UIAModels.UIATarget;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.exceptions.SessionNotCreatedException;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;
import org.uiautomation.ios.server.utils.ClassicCommands;

public class NewSessionTest {
  /*private IOSServer server;
  private static String[] args = {"-port", "4444", "-host", "localhost", "-aut",
      SampleApps.getUICatalogFile(), "-aut", SampleApps.getIntlMountainsFile(), "-aut"};

  private static IOSServerConfiguration config = IOSServerConfiguration.create(args);

  @BeforeClass
  public void startServer() throws Exception {
    server = new IOSServer(config);
    server.start();
  }

  @Test
  public void base() {
    RemoteUIADriver driver = null;
    IOSCapabilities cap = IOSCapabilities.iphone("UICatalog", "2.10");
    String sdk = cap.getSDKVersion();
    if (sdk == null) {
      sdk = ClassicCommands.getDefaultSDK();
    }
    try {
      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              SampleApps.uiCatalogCap());

      UIATarget target = driver.getLocalTarget();

      UIAApplication app = target.getFrontMostApp();
      Assert.assertEquals(app.getBundleId(), "com.yourcompany.UICatalog");
      Assert.assertEquals(app.getBundleVersion(), "2.10");
      Assert.assertEquals(target.getSystemVersion(), sdk);
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
      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              IOSCapabilities.iphone("UICatalog"));
      UIATarget target = driver.getLocalTarget();
      UIAApplication app = target.getFrontMostApp();
      Assert.assertEquals(app.getBundleId(), "com.yourcompany.UICatalog");
      Assert.assertEquals(app.getBundleVersion(), "2.10");

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
      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              IOSCapabilities.iphone("UICatalog"));
      UIATarget target = driver.getLocalTarget();
      UIAApplication app = target.getFrontMostApp();
      Assert.assertEquals(app.getBundleId(), "com.yourcompany.UICatalog");
      Assert.assertEquals(app.getBundleVersion(), "2.10");
      // default to UK
      Assert.assertEquals(target.getLanguage(), "en");
      Assert.assertEquals(target.getLocale(), "en_GB");
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
      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              cap);
      UIATarget target = driver.getLocalTarget();
      UIAApplication app = target.getFrontMostApp();
      Assert.assertEquals(app.getBundleId(), "com.yourcompany.InternationalMountains");
      Assert.assertEquals(app.getBundleVersion(), "1.1");
      // default to UK
      Assert.assertEquals(target.getLanguage(), "fr");
      Assert.assertEquals(target.getLocale(), "es");
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
      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              cap);
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
      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              IOSCapabilities.iphone("ferret", "2.10"));

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test(expectedExceptions = IOSAutomationException.class)
  public void wrongVersion() {
    RemoteUIADriver driver = null;
    try {
      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              IOSCapabilities.iphone("UICatalog", "not a number."));
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test(expectedExceptions = SessionNotCreatedException.class)
  public void wrongSDK() {
    IOSCapabilities cap = IOSCapabilities.iphone("InternationalMountains");
    cap.setSDKVersion("17");
    RemoteUIADriver driver = null;
    try {
      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              cap);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void correctSDK() {
    IOSCapabilities cap = IOSCapabilities.iphone("InternationalMountains");
    String sdk = ClassicCommands.getDefaultSDK();
    cap.setSDKVersion(sdk);
    RemoteUIADriver driver = null;
    try {
      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              cap);
      UIATarget target = driver.getLocalTarget();
      Assert.assertEquals(target.getSystemVersion(), sdk);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }


  @Test
  public void correctDevice() {
    IOSCapabilities cap = IOSCapabilities.iphone("UICatalog");
    RemoteUIADriver driver = null;
    try {
      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              cap);
      UIATarget target = driver.getLocalTarget();
      Assert.assertEquals(target.getModel(), "iPhone Simulator");
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }

    cap = IOSCapabilities.ipad("UICatalog");
    try {
      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              cap);
      UIATarget target = driver.getLocalTarget();
      Assert.assertEquals(target.getModel(), "iPad Simulator");
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
      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              cap);
      UIATarget target = driver.getLocalTarget();
      UIAApplication app = target.getFrontMostApp();
      Assert.assertEquals(app.getBundleId(), "com.yourcompany.UICatalog");
      Assert.assertEquals(app.getBundleVersion(), "2.10");
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test(expectedExceptions = SessionNotCreatedException.class)
  public void canUseAnyFlagFromInfoPlistNoMatch() {
    IOSCapabilities cap = IOSCapabilities.iphone("UICatalog");
    cap.setCapability(IOSCapabilities.MAGIC_PREFIX + "CFBundleDevelopmentRegion", "de");
    RemoteUIADriver driver = null;
    try {
      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              cap);
      UIATarget target = driver.getLocalTarget();
      UIAApplication app = target.getFrontMostApp();
      Assert.assertEquals(app.getBundleId(), "com.yourcompany.UICatalog");
      Assert.assertEquals(app.getBundleVersion(), "2.10");
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void ignoreCapabilitiesWithoutMagicPrefix() {
    IOSCapabilities cap = IOSCapabilities.iphone("UICatalog");
    cap.setCapability("CFBundleDevelopmentRegion", "de");
    RemoteUIADriver driver = null;
    try {
      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              cap);
      UIATarget target = driver.getLocalTarget();
      UIAApplication app = target.getFrontMostApp();
      Assert.assertEquals(app.getBundleId(), "com.yourcompany.UICatalog");
      Assert.assertEquals(app.getBundleVersion(), "2.10");
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @AfterClass
  public void stopServer() throws Exception {
    server.stop();
  }*/
}
