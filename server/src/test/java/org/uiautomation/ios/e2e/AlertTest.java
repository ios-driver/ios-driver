package org.uiautomation.ios.e2e;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.UIAAlert;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.UIAModels.UIAElementArray;
import org.uiautomation.ios.UIAModels.UIAWindow;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.server.IOSServer;

public class AlertTest {
  private static final int driver_port = 4445;

  @BeforeClass
  public void setup() throws Exception {
    IOSServer.main(new String[] {"-port", driver_port + "", "-aut",
        "/Users/freynaud/build/eBay2.2.2rc1.app", "-aut", "/Users/freynaud/build/eBayiPad.app"});
  }

  @Test
  public void smokeTest() throws Exception {
    IOSCapabilities cap = IOSCapabilities.iphone("eBay");
    cap.setCapability(IOSCapabilities.TIME_HACK, false);
    RemoteUIADriver driver =
        new RemoteUIADriver("http://localhost:" + driver_port + "/wd/hub", cap);
    UIAAlert alert = driver.getLocalTarget().getAlert();
    UIAButton b = alert.getDefaultButton();
    try {
     UIAWindow win = driver.getLocalTarget().getFrontMostApp().getMainWindow();
     System.out.println(win.logElementTree(null, false).toString(2));
        
    } catch (Exception e) {
      e.printStackTrace();
    }
    b.tap();
    // System.out.println(alert.logElementTree(null, false).toString(2));
    driver.quit();
  }
}
