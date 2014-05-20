package org.uiautomation.ios.selenium;

import java.net.URL;

import org.openqa.selenium.Pages;
import org.openqa.selenium.environment.webserver.AppServer;
import org.openqa.selenium.environment.webserver.WebbitAppServer;
import org.testng.annotations.*;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.IOSServer;
import org.uiautomation.ios.IOSServerConfiguration;

public abstract class BaseSeleniumTest {

  private static final String[] args = {"-port", "4444", "-host", "localhost"};
  private static final IOSServerConfiguration config = IOSServerConfiguration.create(args);
  private static final String url = "http://" + config.getHost() + ":" + config.getPort() + "/wd/hub";
 
  private IOSServer server;
  protected RemoteIOSDriver driver;
  protected Pages pages;
  protected AppServer appServer;

  @BeforeClass
  public void beforeClass() throws Exception {
    startIOSServer();
    startTestServer();
    IOSCapabilities safari = IOSCapabilities.iphone("Safari");
    // safari.setLanguage("fr");
    driver = new RemoteIOSDriver(new URL(url), safari);
  }
  
  @BeforeMethod
  public void beforeMethod() {
    // this ensures all initial test pages are loaded in a clean state
    // (i.e. they don't stay zoomed because of a previous test method action)
    driver.get("about:blank");
  }

  @AfterClass(alwaysRun = true)
  public void afterClass() {
    stopDriver();
    stopIOSServer();
    if (appServer != null)
      appServer.stop();
  }
  
  private void startIOSServer() throws Exception {
    server = new IOSServer(config);
    server.start();
  }

  private void startTestServer() {
    appServer = new WebbitAppServer(config.getHost());
    appServer.start();

    pages = new Pages(appServer);
  }

  protected void startDriver(IOSCapabilities caps) throws Exception {
    stopDriver();
    // caps.setLanguage("fr");
    driver = new RemoteIOSDriver(new URL(url), caps);
  }

  private void stopIOSServer() {
    if (server != null) {
      try {
        server.stop();
      } catch (Exception e) {
        System.err.println("cannot stop IOServer propery: " + e);
      }
      server = null;
    }
  }

  protected final void stopDriver() {
    if (driver != null) {
      try {
        driver.quit();
      } catch (Exception e) {
        System.err.println("cannot quit properly: " + e.getMessage());
      }
      driver = null;
    }
  }
}
