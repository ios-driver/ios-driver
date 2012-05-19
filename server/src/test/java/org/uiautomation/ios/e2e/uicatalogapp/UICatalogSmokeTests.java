package org.uiautomation.ios.e2e.uicatalogapp;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.UIAElementArray;
import org.uiautomation.ios.UIAModels.UIAWindow;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAApplication;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIATarget;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;
import org.uiautomation.ios.communication.IOSDevice;
import org.uiautomation.ios.exceptions.IOSAutomationSetupException;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;
import org.uiautomation.ios.server.instruments.ClassicCommands;
import org.uiautomation.ios.server.instruments.InstrumentsManager;
import org.uiautomation.ios.server.simulator.IOSSimulatorManager;
import org.uiautomation.ios.server.tmp.SampleApps;

public class UICatalogSmokeTests {


  private IOSServer server;
  private static String[] args = {"-port", "4444","-host", "localhost"};
  private static IOSServerConfiguration config = IOSServerConfiguration.create(args);
  private String url = "http://" + config.getHost() + ":" + config.getPort() + "/wd/hub";

  @BeforeClass
  public void startServer() throws Exception {
    server = new IOSServer(config);
    server.start();
  }


  @Test
  public void releaseResources() throws IOSAutomationSetupException {
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(url, SampleApps.cap());
      Assert.assertTrue(ClassicCommands.isRunning(InstrumentsManager.INSTRUMENTS_PROCESS_NAME));
      Assert.assertTrue(ClassicCommands.isRunning(IOSSimulatorManager.SIMULATOR_PROCESS_NAME));
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
    Assert.assertFalse(ClassicCommands.isRunning(InstrumentsManager.INSTRUMENTS_PROCESS_NAME));
    Assert.assertFalse(ClassicCommands.isRunning(IOSSimulatorManager.SIMULATOR_PROCESS_NAME));
  }

  @Test
  public void canChangeProperties() throws Exception {
    RemoteUIADriver driver = null;


    driver = null;
    try {
      IOSCapabilities cap = new IOSCapabilities();
      cap.setDevice(IOSDevice.iPhone);
      cap.setLanguage("de");
      cap.setLocale("fr_CA");
      cap.setCapability(IOSCapabilities.AUT, new SampleApps().getUICatalogApp());
      driver = new RemoteUIADriver(url, cap);
      IOSCapabilities realCap = driver.getCapabilities();
      Assert.assertEquals(realCap.getLanguage(), "de");
      Assert.assertEquals(realCap.getLocale(), "fr_CA");


    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test(groups = {"NI", "broken"}, enabled = false)
  public void canChangePropertiesBroken() throws Exception {
    RemoteUIADriver driver = null;

    driver = null;
    try {
      IOSCapabilities cap = new IOSCapabilities();
      // broken
      cap.setDevice(IOSDevice.iPad);
      cap.setLanguage("de");
      cap.setLocale("fr_CA");
      cap.setCapability(IOSCapabilities.AUT, new SampleApps().getUICatalogApp());
      driver = new RemoteUIADriver(url, cap);
      IOSCapabilities realCap = driver.getCapabilities();
      // not working. Seems defined by compile options in xocdebuild more than simulator settings ?
      Assert.assertEquals(realCap.getDevice(), IOSDevice.iPad);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }



  @Test
  public void smokeTestCanStart() {
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(url, SampleApps.cap());
      RemoteUIATarget target = driver.getLocalTarget();
      Assert.assertEquals(target.getReference(), "1");
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void idIsUniquePerElement() throws Exception {
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(url, SampleApps.cap());
      RemoteUIATarget target = driver.getLocalTarget();
      RemoteUIAApplication app = target.getFrontMostApp();
      RemoteUIAWindow win1 = app.getMainWindow();


      UIAElementArray<UIAWindow> windows = app.getWindows();
      RemoteUIAWindow win2 = (RemoteUIAWindow) windows.get(0);
      Assert.assertEquals(win1.getReference(), win2.getReference());

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void logTreeFromRoot() throws Exception {
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(url, SampleApps.cap());
      JSONObject tree = driver.logElementTree();
      // TODO freynaud some validation would be good.
      Assert.assertEquals(tree.length(), 2);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void logTreeFromElement() throws Exception {
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(url, SampleApps.cap());
      RemoteUIATarget target = driver.getLocalTarget();
      RemoteUIAApplication app = target.getFrontMostApp();
      RemoteUIAWindow win1 = app.getMainWindow();
      // TODO freynaud some validation would be good.
      JSONObject tree = win1.logElementTree();
      System.out.println(tree.toString(2));
      Assert.assertEquals(tree.length(), 2);
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
