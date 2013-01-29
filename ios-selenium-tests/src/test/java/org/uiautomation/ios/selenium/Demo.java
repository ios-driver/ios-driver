/*
 * Copyright 2012 ios-driver committers.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the Licence at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */

package org.uiautomation.ios.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.Rotatable;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.uiautomation.ios.IOSCapabilities;

import java.net.URL;
import java.util.concurrent.Callable;

import static org.openqa.selenium.TestWaiter.waitFor;
import static org.openqa.selenium.WaitingConditions.pageTitleToBe;

public class Demo extends BaseSeleniumTest {


  public static void main3(String[] args) throws Exception {

    DesiredCapabilities safari = IOSCapabilities.ipad("Safari");
    RemoteWebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), safari);

    driver.get("http://ebay.co.uk");

    ((Rotatable) new Augmenter().augment(driver)).rotate(ScreenOrientation.LANDSCAPE);

    WebElement search = driver.findElement(By.id("srchDv"));
    search.sendKeys("ipod");
    search.submit();

    waitFor(pageTitleToBe(driver, "ipod | eBay Mobile Web"));

    driver.quit();
  }


  public static void main(String[] args) throws Exception {
    IOSCapabilities catalog = IOSCapabilities.iphone("UICatalog");

    RemoteWebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), catalog);
    // by default, the app starts in native mode.
    WebElement web = driver.findElement(By.xpath("//UIATableCell[contains(@name,'Web')]"));
    web.click();

    // now that a webview is displayed, switch to web mode.
    driver.switchTo().window("Web");

    // and select items using natural web selectors.
    final By cssSelector = By.cssSelector("a[href='http://store.apple.com/']");

    // reuse whatever contrust your webdriver tests are using.
    WebElement el = waitFor(elementToExist(driver, cssSelector));
    Assert.assertEquals(el.getAttribute("href"), "http://store.apple.com/");

    driver.quit();
  }


  public static Callable<WebElement> elementToExist(
      final WebDriver driver, final By by) {
    return new Callable<WebElement>() {

      public WebElement call() throws Exception {
        return driver.findElement(by);
      }

      @Override
      public String toString() {
        return String.format("element with ID %s to exist", by.toString());
      }
    };
  }


  public static void main2(String[] args) throws Exception {

    DesiredCapabilities safari = IOSCapabilities.iphone("Safari");
    RemoteWebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), safari);

    driver.get("http://hp.mobileweb.ebay.co.uk/home");
    WebElement search = driver.findElement(By.id("srchDv"));
    search.sendKeys("ipod");
    search.submit();

    waitFor(pageTitleToBe(driver, "ipod | eBay Mobile Web"));

    driver.quit();
  }


}
