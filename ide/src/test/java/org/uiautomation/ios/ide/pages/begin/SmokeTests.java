package org.uiautomation.ios.ide.pages.begin;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.ide.IDEServer;
import org.uiautomation.ios.server.IOSServer;

public class SmokeTests {

  @BeforeClass
  public void setup() throws Exception {
    IDEServer.main(new String[] {"-port", "5555", "-aut", "/Users/freynaud/build/eBayDevice.app"});
  }

  @Test
  public void smokeTest() {
    RemoteUIADriver driver =
        new RemoteUIADriver("http://localhost:5556/wd/hub", IOSCapabilities.iphone("eBay"));
    System.out.println(driver.getSession());
    driver.quit();
  }
}
