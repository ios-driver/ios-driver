package org.uiautomation.ios.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.Pages;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.environment.webserver.AppServer;
import org.openqa.selenium.environment.webserver.WebbitAppServer;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.communication.device.DeviceType;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.uiautomation.ios.IOSCapabilities.BUNDLE_NAME;
import static org.uiautomation.ios.IOSCapabilities.DEVICE;
import static org.uiautomation.ios.IOSCapabilities.LANGUAGE;
import static org.uiautomation.ios.IOSCapabilities.LOCALE;

public class ElementFindingMultiDeviceTest {

  private IOSServer server;
  private static String[] args = {"-port", "4444", "-host", "localhost"};
  private static IOSServerConfiguration config = IOSServerConfiguration.create(args);
  protected RemoteWebDriver driver = null;
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

  @AfterClass(alwaysRun = true)
  public void tearDown() throws Exception {
    if (server != null)
      server.stop();
    if (appServer != null)
      appServer.stop();
  }

  @DataProvider(name = "capabilities")
  public Object[][] createData1() {
    return new Object[][]{
        // not available in iOS7: {DeviceType.iphone, DeviceVariation.iPhone},
        {DeviceType.iphone, DeviceVariation.iPhoneRetina},
        {DeviceType.iphone, DeviceVariation.iPhoneRetina_4inch},
        {DeviceType.iphone, DeviceVariation.iPhoneRetina_4inch_64bit},

        {DeviceType.ipad, DeviceVariation.iPad},
        {DeviceType.ipad, DeviceVariation.iPadRetina},
        {DeviceType.ipad, DeviceVariation.iPadRetina_64bit},
    };
  }

  @Test(dataProvider = "capabilities")
  public void testSendingKeyboardEventsShouldAppendTextInInputsMultipleDeviceFamilyAndOrientation(
      DeviceType device,
      DeviceVariation variation) throws Exception {

    IOSCapabilities cap = new IOSCapabilities();

    cap.setCapability(DEVICE, device);
    cap.setDeviceVariation(variation);

    cap.setCapability(LANGUAGE, "en");
    cap.setCapability(LOCALE, "en_GB");
    cap.setCapability(BUNDLE_NAME, "Safari");
    RemoteIOSDriver driver = null;
    try {
      driver = new RemoteIOSDriver(new URL(url), cap);

      for (Orientation o : getOrientationForDevice(device)) {
        driver.rotate(o);
        driver.get(pages.formPage);
        WebElement element = driver.findElement(By.id("working"));

        element.sendKeys("some");
        String value = element.getAttribute("value");
        assertEquals(value, ("some"));

        element.sendKeys(" text");
        value = element.getAttribute("value");
        assertEquals(value, ("some text"));
      }

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }

  }

  private List<Orientation> getOrientationForDevice(DeviceType device) {
    List<Orientation> res = new ArrayList<Orientation>();
    res.add(Orientation.PORTRAIT);
    res.add(Orientation.LANDSCAPE);
    res.add(Orientation.UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT);
    if (device == DeviceType.ipad) {
      res.add(Orientation.UIA_DEVICE_ORIENTATION_PORTRAIT_UPSIDEDOWN);
    }
    return res;
  }

}
