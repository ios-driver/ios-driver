package org.uiautomation.ios.e2e.uicatalogapp;

import org.openqa.selenium.html5.Location;
import org.openqa.selenium.html5.LocationContext;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;


public class LocationContextTest extends BaseIOSDriverTest {

  private RemoteWebDriver driver;
  private UIAElement element;

  @BeforeClass
  public void startDriver() {

    driver = new RemoteWebDriver(getRemoteURL(), SampleApps.geoCoderCap());
  }

  @AfterClass
  public void stopDriver() {
    if (driver != null) {
      driver.quit();
    }
  }


  public static void main(String[] args) {
    System.out.println(IOSCapabilities.iphone("Safari").getRawCapabilities());
  }

  @Test
  public void testShouldSetAndGetLocation() {
    //driver.get(pages.html5Page);

    LocationContext d = (LocationContext) new Augmenter().augment(driver);
    Location loc = new Location(40.714353, -74.005973, 0.056747);

    d.setLocation(loc);

    //driver.manage().timeouts().implicitlyWait(2000, MILLISECONDS);

    //Location location = ((LocationContext) driver).location();
    //assertEquals(40.714353, location.getLatitude(), 4);
    //assertEquals(-74.005973, location.getLongitude(), 4);
    //assertEquals(1.056747, location.getAltitude(), 4);
  }
}
