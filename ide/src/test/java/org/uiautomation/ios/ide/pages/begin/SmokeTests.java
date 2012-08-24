package org.uiautomation.ios.ide.pages.begin;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.ide.IDEServer;
import org.uiautomation.ios.server.IOSServer;

public class SmokeTests {

  @BeforeClass
  public void setup() throws Exception {
    IOSServer.main(new String[] {"-port","4444","-aut","/Users/freynaud/build/eBay2.2.2rc1.app"});
    IDEServer.main(new String[] {"5555"});
  }

  @Test
  public void smokeTest() {
    RemoteUIADriver driver =
        new RemoteUIADriver("http://localhost:4444/wd/hub", IOSCapabilities.iphone("eBay"));
    System.out.println("http://localhost:5555/ide/session/"+driver.getSession()+"/home");
    driver.quit();
  }
}
