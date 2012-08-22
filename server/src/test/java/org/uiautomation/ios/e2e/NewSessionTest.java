package org.uiautomation.ios.e2e;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAApplication;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.UIAModels.UIATarget;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAApplication;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIATarget;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

public class NewSessionTest {
  private IOSServer server;
  private static String[] args = {"-port", "4444", "-host", "localhost", "-aut",
      SampleApps.getUICatalogFile(), "-aut", SampleApps.getIntlMountainsFile()};

  private static IOSServerConfiguration config = IOSServerConfiguration.create(args);

  @BeforeClass
  public void startServer() throws Exception {
    server = new IOSServer(config);
    server.start();
  }

  @Test
  public void base() {
    RemoteUIADriver driver = null;
    try {
      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              IOSCapabilities.iphone("UICatalog", "2.10"));

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

  @Test(expectedExceptions = IOSAutomationException.class)
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

  @Test(expectedExceptions = IOSAutomationException.class)
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



  @AfterClass
  public void stopServer() throws Exception {
    server.stop();
  }
}
