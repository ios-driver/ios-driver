package org.uiautomation.ios.e2e.crash;

import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;
import org.uiautomation.ios.utils.ClassicCommands;

import java.net.MalformedURLException;
import java.net.URL;

public class CrashHandling {

  private IOSServer server;
  private IOSServerConfiguration config;
  private RemoteWebDriver driver;
  private IOSCapabilities cap;

  @BeforeMethod
  public void startServer() throws Exception {
    String[] args = {"-port", "4444", "-host", "localhost",
        "-app", SampleApps.getPPNQASampleApp(), "-simulators"};
    config = IOSServerConfiguration.create(args);

    server = new IOSServer(config);
    server.start();

    Thread.sleep(1000);

    cap = SampleApps.ppNQASampleAppCap();
    cap.setCapability(IOSCapabilities.SIMULATOR, true);

    driver = new RemoteWebDriver(getRemoteURL(), cap);

  }

//  @AfterClass
//  public void stopServer() throws Exception {
//
//  }

  @AfterMethod
  public void closeDriver() throws Exception {
    try {
      if (driver != null) {
        driver.quit();
        driver = null;
      }
    } catch (Exception ignored) {
    }

    try {
      if (server != null) {
        server.stop();
        server = null;
      }
    } catch (Exception ignored) {
    }
  }

  private URL getRemoteURL() {
    try {
      URL remote = new URL("http://" + config.getHost() + ":" + config.getPort() + "/wd/hub");
      return remote;
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void isAppCrashDetected() throws InterruptedException {

    WebElement crashButton = driver.findElement(By.name("Crash me!"));

    boolean crashExceptionThrown = false;
    try {
      crashButton.click();
      // give instruments some time to realise there has been a crash and report it
      Thread.sleep(1000);
      // the application has crashed - we should be notified on the next client request
      crashButton.click();
    } catch (Exception e) {
      crashExceptionThrown = true;
      Assert.assertTrue("Crash error contains the crash trace file details. " + e.getMessage(), e.getMessage().contains("The crash report can be found"));
    }
    Assert.assertTrue("App crash detected.", crashExceptionThrown);

  }

  @Test
  public void canStartANewSessionAfterAppCrash() throws InterruptedException {

    WebElement crashButton = driver.findElement(By.name("Crash me!"));
    try {
      crashButton.click();
      // give instruments some time to realise there has been a crash and report it
      Thread.sleep(1000);
      // the application has crashed - we should be notified on the next client request
      crashButton.click();
    } catch (Exception ignored) {
      // this is expected since we crashed the app
    }

    driver = new RemoteWebDriver(getRemoteURL(), cap);

    Assert.assertTrue("We can start a new test session", driver != null);
  }

  @Test
  public void isSimulatorCrashDetected() throws InterruptedException {

    WebElement crashButton = driver.findElement(By.name("Crash me!"));

    boolean crashExceptionThrown = false;
    try {
      // kill simulator
      ClassicCommands.killall("iPhone Simulator");
      Thread.sleep(1000);
      // should throw an exception
      crashButton.click();
    } catch (Exception e) {
      crashExceptionThrown = true;
      Assert.assertTrue("Crash error contains Simulator as likely cause of problem. " + e.getMessage(), e.getMessage().contains("It appears like the Simulator process has crashed"));
    }
    Assert.assertTrue("Simulator crash detected.", crashExceptionThrown);

  }

  @Test
  public void canStartANewSessionAfterSimulatorCrash() throws InterruptedException {

    WebElement crashButton = driver.findElement(By.name("Crash me!"));
    try {
      // kill simulator
      ClassicCommands.killall("iPhone Simulator");
      Thread.sleep(1000);
      // should throw an exception
      crashButton.click();
    } catch (Exception ignored) {
      // this is expected since we crashed the simulator
    }

    driver = new RemoteWebDriver(getRemoteURL(), cap);

    Assert.assertTrue("We can start a new test session", driver != null);
  }

//  @Test
//  public void isInstrumentsCrashDetected() throws InterruptedException {
//    IOSCapabilities cap = SampleApps.ppNQASampleAppCap();
//    cap.setCapability(IOSCapabilities.SIMULATOR, true);
//
//    driver = new RemoteWebDriver(getRemoteURL(), cap);
//    WebElement crashButton = driver.findElement(By.name("Crash me!"));
//
//    boolean crashExceptionThrown = false;
//    try {
//      // kill instruments
//      ClassicCommands.killall("instruments");
//      Thread.sleep(1000);
//      // should throw an exception
//      crashButton.click();
//    } catch (Exception e) {
//      crashExceptionThrown = true;
//    }
//    Assert.assertTrue("Instruments crash detected.", crashExceptionThrown);
//
//  }

}
