package org.uiautomation.ios.selenium;

import java.net.URL;

import org.openqa.selenium.Pages;
import org.openqa.selenium.environment.webserver.AppServer;
import org.openqa.selenium.environment.webserver.WebbitAppServer;
import org.testng.annotations.*;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

public abstract class BaseSeleniumTest {

  private static final String[] args = {"-port", "4444", "-host", "localhost", /*"-beta",*/ "-simulators"};
  private static final IOSServerConfiguration config = IOSServerConfiguration.create(args);
  private static final String url = "http://" + config.getHost() + ":" + config.getPort() + "/wd/hub";
 
  private IOSServer server;
  protected RemoteIOSDriver driver;
  protected Pages pages;
  protected AppServer appServer;

  @BeforeClass
  public void setup() throws Exception {
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

  @AfterClass
  public void tearDown() throws Exception {
    try {
      driver.quit();
    } catch (Exception e) {
      System.err.println("cannot quit properly: " + e);
    }
    try {
      stopIOSServer();
    } catch (Exception e) {
      System.err.println("cannot stop IOServer propery: " + e);
    }
    appServer.stop();
  }
  
  private void startIOSServer() throws Exception {
    server = new IOSServer(config);
    server.start();
  }

  private void startTestServer() {
    appServer = new WebbitAppServer();
    appServer.start();

    pages = new Pages(appServer);
  }

  private void stopIOSServer() throws Exception {
    server.stop();
  }
}
