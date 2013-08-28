package org.uiautomation.ios.selenium;

import org.openqa.selenium.Pages;
import org.openqa.selenium.environment.webserver.AppServer;
import org.openqa.selenium.environment.webserver.WebbitAppServer;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

import java.net.URL;

public class BaseSeleniumTest {

  private IOSServer server;
  private static String[] args = {"-port", "4444", "-host", "localhost", /*"-beta",*/ "-simulators"};
  private static IOSServerConfiguration config = IOSServerConfiguration.create(args);
  protected RemoteIOSDriver driver = null;
  private String url = "http://" + config.getHost() + ":" + config.getPort() + "/wd/hub";
  protected Pages pages;
  protected AppServer appServer;

  @BeforeClass
  public void setup() throws Throwable {
    startIOSServer();
    startTestServer();
    IOSCapabilities safari = IOSCapabilities.iphone("Safari");
    // safari.setLanguage("fr");
    driver = new RemoteIOSDriver(new URL(url), safari);

  }

  public void startIOSServer() throws Exception {
    server = new IOSServer(config);
    server.start();
  }

  public void startTestServer() {
    appServer = new WebbitAppServer();
    appServer.start();

    pages = new Pages(appServer);
  }

  public void stopIOSServer() throws Exception {
    server.stop();
  }


  @AfterClass
  public void tearDown() throws Exception {

    try {
      driver.quit();
    } catch (Exception e) {
      System.err.println("cannot quit properly :" + e.getMessage());
    }
    stopIOSServer();
    appServer.stop();
  }
}
