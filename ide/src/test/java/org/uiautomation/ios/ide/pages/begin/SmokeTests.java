package org.uiautomation.ios.ide.pages.begin;

import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.BeforeClass;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.ide.IDEServer;
import org.uiautomation.ios.server.IOSServer;

public class SmokeTests {

  private static final int driver_port = 4445;

  @BeforeClass
  public void setup() throws Exception {
    IOSServer.main(new String[] {"-port", driver_port + "", "-aut",
        "/Users/freynaud/build/eBay2.2.2rc1.app", "-aut", "/Users/freynaud/build/eBayiPad.app"});
    // IDEServer.main(new String[] {"5555"});
  }

  


  public static void main(String[] args) throws Exception {
    /*IDEServer ide = new IDEServer(driver_port);
    MockedCache cache = new MockedCache();
    ide.setCache(cache);
    ide.start();*/
    IOSServer.main(new String[] {"-port", driver_port + "", "-aut",
        "/Users/freynaud/build/eBay2.2.2rc1.app", "-aut", "/Users/freynaud/build/eBayiPad.app"});
    WebDriver driver = new RemoteWebDriver(new URL("http://localhost:"+driver_port+"/wd/hub"), IOSCapabilities.ipad("eBay"));
    driver.quit();
  }
}
