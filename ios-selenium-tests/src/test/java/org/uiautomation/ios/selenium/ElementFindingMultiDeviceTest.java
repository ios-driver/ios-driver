package org.uiautomation.ios.selenium;

import static org.testng.Assert.assertEquals;
import static org.uiautomation.ios.IOSCapabilities.BUNDLE_NAME;
import static org.uiautomation.ios.IOSCapabilities.DEVICE;
import static org.uiautomation.ios.IOSCapabilities.LANGUAGE;
import static org.uiautomation.ios.IOSCapabilities.LOCALE;

import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.Pages;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.environment.webserver.AppServer;
import org.openqa.selenium.environment.webserver.WebbitAppServer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.client.uiamodels.impl.RemoteMobileSafariDriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.communication.device.Device;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

public class ElementFindingMultiDeviceTest {

  private IOSServer server;
  private static String[] args = { "-port", "4444", "-host", "localhost" };
  private static IOSServerConfiguration config = IOSServerConfiguration.create(args);
  protected RemoteMobileSafariDriver driver = null;
  private String url = "http://" + config.getHost() + ":" + config.getPort() + "/wd/hub";
  protected Pages pages;
  protected AppServer appServer;

  @BeforeClass
  public void setup() throws Throwable {
    server = new IOSServer(config);
    server.start();
    appServer = new WebbitAppServer();
    appServer.start();
    pages = new Pages(appServer);
  }

  @AfterClass
  public void tearDown() throws Exception {
    server.stop();
    appServer.stop();
  }

  @DataProvider(name = "capabilities")
  public Object[][] createData1() {
    return new Object[][] {

    { Device.iphone, DeviceVariation.Regular, Orientation.UIA_DEVICE_ORIENTATION_PORTRAIT },
    { Device.iphone, DeviceVariation.Regular, Orientation.UIA_DEVICE_ORIENTATION_LANDSCAPELEFT },
    { Device.iphone, DeviceVariation.Regular, Orientation.UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT },
    { Device.iphone, DeviceVariation.Retina35, Orientation.UIA_DEVICE_ORIENTATION_PORTRAIT },
    { Device.iphone, DeviceVariation.Retina35, Orientation.UIA_DEVICE_ORIENTATION_LANDSCAPELEFT },
    { Device.iphone, DeviceVariation.Retina35, Orientation.UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT },
    { Device.iphone, DeviceVariation.Retina4, Orientation.UIA_DEVICE_ORIENTATION_PORTRAIT },
    { Device.iphone, DeviceVariation.Retina4, Orientation.UIA_DEVICE_ORIENTATION_LANDSCAPELEFT },
    { Device.iphone, DeviceVariation.Retina4, Orientation.UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT },
    
    // { Device.iphone, DeviceVariation.Retina35 },
    // { Device.iphone, DeviceVariation.Retina4 },
    // { Device.ipad, DeviceVariation.Regular },
    // { Device.ipad, DeviceVariation.Retina },

    };
  }

  @Test(dataProvider = "capabilities")
  public void testSendingKeyboardEventsShouldAppendTextInInputsMultipleDeviceFamilyAndOrientation(Device device,
      DeviceVariation variation, Orientation o) throws Exception {

    IOSCapabilities cap = new IOSCapabilities();

    cap.setCapability(DEVICE, device);
    cap.setDeviceVariation(variation);

    cap.setCapability(LANGUAGE, "en");
    cap.setCapability(LOCALE, "en_GB");
    cap.setCapability(BUNDLE_NAME, "Safari");
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(new URL(url), cap);
      driver.switchTo().window("Web");

      driver.setDeviceOrientation(o);
      driver.get(pages.formPage);
     
      WebElement element = driver.findElement(By.id("working"));
      //WebElement element = driver.findElement(By.id("inputWithText"));
      
      element.sendKeys("some");
      String value = element.getAttribute("value");
      assertEquals(value, ("some"));

      element.sendKeys(" text");
      value = element.getAttribute("value");
      assertEquals(value, ("some text"));
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }

  }

}
