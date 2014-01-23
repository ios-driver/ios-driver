/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

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

import static org.junit.Assert.assertEquals;


public class LocationContextTest extends BaseIOSDriverTest {

  private RemoteWebDriver driver;
  private UIAElement element;

  @BeforeClass
  public void startDriver() {
    driver = getDriver(SampleApps.geoCoderCap());
  }

  @AfterClass(alwaysRun = true)
  public void stopDriver() {
    if (driver != null) {
      driver.quit();
    }
  }


  @Test(enabled = false)
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
