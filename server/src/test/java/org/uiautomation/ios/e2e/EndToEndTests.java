package org.uiautomation.ios.e2e;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.UIAApplication;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAApplication;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIATarget;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;
import org.uiautomation.ios.server.tmp.SampleApps;

public class EndToEndTests {

  private IOSServer server;
  private static String[] args = {"-port", "4444", "-host", "localhost", "-aut",
      SampleApps.getUICatalogApp()};
  private static IOSServerConfiguration config = IOSServerConfiguration.create(args);

  @BeforeClass
  public void startServer() throws Exception {
    server = new IOSServer(config);
    server.start();
  }

  @Test
  public void scriptStartsAndRegisterToServer() {
    RemoteUIADriver driver = null;
    try {
      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              SampleApps.uiCatalogCap());

      RemoteUIATarget target = driver.getLocalTarget();
      Assert.assertEquals(target.getReference(), "1");
      target = (RemoteUIATarget) driver.getLocalTarget();
      Assert.assertEquals(target.getReference(), "2");
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void targetAttributesTests() {
    RemoteUIADriver driver = null;
    try {
      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              SampleApps.uiCatalogCap());

      RemoteUIATarget target = driver.getLocalTarget();
      Assert.assertEquals(target.getReference(), "1");

      String model = target.getModel();
      Assert.assertEquals(model, "iPhone Simulator");

      String name = target.getName();
      Assert.assertEquals(name, "iPhone Simulator");

      String systemName = target.getSystemName();
      Assert.assertEquals(systemName, "iPhone OS");

      String systemVersion = target.getSystemVersion();
      Assert.assertEquals(systemVersion, "5.1");

      UIARect rect = target.getRect();
      Assert.assertEquals(rect.getX(), 0);
      Assert.assertEquals(rect.getX(), 0);
      Assert.assertEquals(rect.getHeight(), 480);
      Assert.assertEquals(rect.getWidth(), 320);

      UIAApplication app = target.getFrontMostApp();
      Assert.assertNull(app.getVersion());
      Assert.assertEquals(app.getBundleId(), "com.yourcompany.UICatalog");
      Assert.assertEquals(app.getBundleVersion(), "2.10");

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void screenshot() throws InterruptedException {
    RemoteUIADriver driver = null;
    try {

      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              SampleApps.uiCatalogCap());

      RemoteUIATarget target = driver.getLocalTarget();

      File to = new File("ss.png");
      target.takeScreenshot(to.getAbsolutePath());
      Assert.assertTrue(to.exists());
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void frontMostApp() throws InterruptedException {
    RemoteUIADriver driver = null;
    try {

      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              SampleApps.uiCatalogCap());

      RemoteUIATarget target = driver.getLocalTarget();
      RemoteUIAApplication app = target.getFrontMostApp();
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
  
  @Test
  public void logElementTreeWithScreenshot() throws InterruptedException, JSONException {
    RemoteUIADriver driver = null;
    try {

      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              SampleApps.uiCatalogCap());

      File f = new File("logElementTreeTmp");
      f.delete();
      JSONObject object = driver.logElementTree(f);
     
      Assert.assertTrue(f.exists());
      f.delete();
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  
  @Test
  public void logElementTreeNoScreenshot() throws InterruptedException, JSONException {
    RemoteUIADriver driver = null;
    try {

      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              SampleApps.uiCatalogCap());

      JSONObject object = driver.logElementTree(null);
      Assert.assertTrue(object.has("tree"));
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
  @Test
  public void mainWindow() throws InterruptedException {
    RemoteUIADriver driver = null;
    try {

      driver =
          new RemoteUIADriver("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub",
              SampleApps.uiCatalogCap());

      RemoteUIATarget target = driver.getLocalTarget();
      RemoteUIAApplication app = target.getFrontMostApp();
      RemoteUIAWindow window = app.getMainWindow();
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
