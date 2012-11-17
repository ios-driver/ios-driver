package org.uiautomation.ios.ide.pages.begin;

import static org.openqa.selenium.TestWaiter.waitFor;
import static org.openqa.selenium.WaitingConditions.pageTitleToBe;

import org.openqa.selenium.By;
import org.openqa.selenium.Pages;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.environment.webserver.AppServer;
import org.openqa.selenium.environment.webserver.WebbitAppServer;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.UIADriver;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.client.uiamodels.impl.RemoteMobileSafariDriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

public class ConfigurationTest {

  private static String safari = "/Applications/Xcode4.5.app/Contents/Developer/Platforms/iPhoneSimulator.platform/Developer/SDKs/iPhoneSimulator6.0.sdk/Applications/MobileSafari.app";
  private IOSServer server;
  private static String[] args = { "-port", "4444", "-host", "localhost", "-aut", safari };
  private static IOSServerConfiguration config = IOSServerConfiguration.create(args);
  private RemoteMobileSafariDriver driver = null;
  private String url = "http://" + config.getHost() + ":" + config.getPort() + "/wd/hub";
  private Pages pages;
  private AppServer appServer;

  @BeforeClass
  public void setup() throws Exception {

    server = new IOSServer(config);
    server.start();

    IOSCapabilities safari = IOSCapabilities.ipad("Safari");
    safari.setCapability(IOSCapabilities.TIME_HACK, false);

    driver = new RemoteMobileSafariDriver(url, safari);
    appServer = new WebbitAppServer();
    appServer.start();
    pages = new Pages(appServer);
  }

  @Test
  public void testShouldReturnTitleOfPageIfSet() {
    driver.get(pages.xhtmlTestPage);
    Assert.assertEquals(driver.getTitle(), "XHTML Test Page");

    driver.get(pages.simpleTestPage);
    Assert.assertEquals(driver.getTitle(), "Hello WebDriver");
  }

  @AfterClass
  public void tearDown() throws Exception {

    try {
      driver.quit();
    } catch (Exception e) {
      System.err.println("cannot quit properly :" + e.getMessage());
    }
    server.stop();
  }

  @Test
  public void configuration() {
    try {
      driver.configure(WebDriverLikeCommand.URL).set("nativeEvents", true);
      driver.get(pages.xhtmlTestPage);
      Assert.assertEquals(driver.getTitle(), "XHTML Test Page");
      driver.configure(WebDriverLikeCommand.URL).set("nativeEvents", false);
      driver.get(pages.simpleTestPage);
      Assert.assertEquals(driver.getTitle(), "Hello WebDriver");
      driver.configure(WebDriverLikeCommand.URL).set("nativeEvents", true);
      driver.get(pages.xhtmlTestPage);
      Assert.assertEquals(driver.getTitle(), "XHTML Test Page");
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
  
  @Test
  public void testDriverShouldBeAbleToClickNativeAndAtom() {
    driver.get(pages.xhtmlTestPage);
    WebElement el = driver.findElement(By.cssSelector("div[class='content']>a[href='resultPage.html']"));
    driver.configure(WebDriverLikeCommand.CLICK).set("nativeEvents", true);
    el.click();
    waitFor(pageTitleToBe(driver, "We Arrive Here"));

    
    driver.get(pages.xhtmlTestPage);
    el = driver.findElement(By.cssSelector("div[class='content']>a[href='resultPage.html']"));
    driver.configure(WebDriverLikeCommand.CLICK).set("nativeEvents", false);
    el.click();
    waitFor(pageTitleToBe(driver, "We Arrive Here"));
   
  }
}
