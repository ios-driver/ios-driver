package org.uiautomation.ios.ide.pages.begin;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
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

  @Test
  public void smokeTest() {
    RemoteUIADriver driver =
        new RemoteUIADriver("http://localhost:" + driver_port + "/wd/hub",
            IOSCapabilities.ipad("eBay"));
    // System.out.println("http://localhost:4444/ide/session/"+driver.getSession()+"/home");
    driver.quit();
  }


  public static void main(String[] args) throws Exception {
    IDEServer ide = new IDEServer(driver_port);
    MockedCache cache = new MockedCache();
    ide.setCache(cache);
    ide.start();
  }
}
