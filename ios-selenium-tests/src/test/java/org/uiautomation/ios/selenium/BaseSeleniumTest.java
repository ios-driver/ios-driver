package org.uiautomation.ios.selenium;

import org.openqa.selenium.Pages;
import org.openqa.selenium.environment.webserver.AppServer;
import org.openqa.selenium.environment.webserver.WebbitAppServer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.RemoteMobileSafariDriver;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

import java.net.URL;

public class BaseSeleniumTest {

  private IOSServer server;
  private static String[] args = {"-port", "4444", "-host", "localhost"};
  private static IOSServerConfiguration config = IOSServerConfiguration.create(args);
  protected RemoteMobileSafariDriver driver = null;
  private String url = "http://" + config.getHost() + ":" + config.getPort() + "/wd/hub";
  protected Pages pages;
  protected AppServer appServer;

  @BeforeClass
  public void setup() throws Throwable {
    server = new IOSServer(config);
    server.start();

    IOSCapabilities safari = IOSCapabilities.iphone("Safari");
    safari.setCapability(IOSCapabilities.TIME_HACK, false);
    // safari.setLanguage("fr");
    driver = new RemoteMobileSafariDriver(new URL(url), safari);
    appServer = new WebbitAppServer();
    appServer.start();

    pages = new Pages(appServer);
  }

  @AfterClass
  public void tearDown() throws Exception {

    try {
      driver.quit();
    } catch (Exception e) {
      System.err.println("cannot quit properly :" + e.getMessage());
    }
    server.stop();
    appServer.stop();
  }
}
