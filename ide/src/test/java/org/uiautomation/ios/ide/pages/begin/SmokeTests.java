package org.uiautomation.ios.ide.pages.begin;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.ide.IDEServer;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.application.Localizable;
import org.uiautomation.ios.server.tests.SampleApps;

public class SmokeTests {

  @BeforeClass
  public void setup() throws Exception {
    IDEServer.main(new String[] {"-port", "5555", "-aut", SampleApps.getUICatalogApp()});
    IOSServer.main(new String[] {"-port", "5556", "-aut", SampleApps.getIntlMountainsApp()});
  }

  @Test
  public void smokeTest() {
    RemoteUIADriver driver =
        new RemoteUIADriver("http://localhost:5556/wd/hub", SampleApps.intlMountainsCap(Localizable.fr));
    System.out.println(driver.getSession());
    driver.quit();
  }
}
