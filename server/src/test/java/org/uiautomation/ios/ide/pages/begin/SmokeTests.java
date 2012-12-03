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

  public static void main(String[] args) throws Exception {
    IDEServer ide = new IDEServer(driver_port);
    MockedCache cache = new MockedCache();
    ide.setCache(cache);
    ide.start();
    System.out.println("started");
  }

  /**
   * session:iphone_Regular_UIA_DEVICE_ORIENTATION_PORTRAIT
   * session:iphone_Retina35_UIA_DEVICE_ORIENTATION_PORTRAIT
   * session:iphone_Retina4_UIA_DEVICE_ORIENTATION_PORTRAIT
   * session:ipad_Regular_UIA_DEVICE_ORIENTATION_PORTRAIT
   * session:ipad_Retina_UIA_DEVICE_ORIENTATION_PORTRAIT
   * session:ipad_Regular_UIA_DEVICE_ORIENTATION_PORTRAIT_UPSIDEDOWN
   * session:ipad_Retina_UIA_DEVICE_ORIENTATION_PORTRAIT_UPSIDEDOWN
   * session:iphone_Regular_UIA_DEVICE_ORIENTATION_LANDSCAPELEFT
   * session:iphone_Retina35_UIA_DEVICE_ORIENTATION_LANDSCAPELEFT
   * session:iphone_Retina4_UIA_DEVICE_ORIENTATION_LANDSCAPELEFT
   * session:ipad_Regular_UIA_DEVICE_ORIENTATION_LANDSCAPELEFT
   * session:ipad_Retina_UIA_DEVICE_ORIENTATION_LANDSCAPELEFT
   * session:iphone_Regular_UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT
   * session:iphone_Retina35_UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT
   * session:iphone_Retina4_UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT
   * session:ipad_Regular_UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT
   * session:ipad_Retina_UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT
   */

}
