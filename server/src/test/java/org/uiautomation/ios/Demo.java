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

package org.uiautomation.ios;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * example for the documentation
 */
public class Demo {


  private final static boolean useQA = true;

  public static void main(String[] args) throws Exception {
    String[] a = {"-port", "4445", "-host", "localhost",
                  "-aut", SampleApps.getUICatalogFile(),
                  "-aut", SampleApps.getUICatalogIpad(),
                  "-simulators",
                  "-aut", "/Users/freynaud/eBay3.1_19.app"};

    IOSServerConfiguration config = IOSServerConfiguration.create(a);

    IOSServer server = new IOSServer(config);
    server.start();

    IOSCapabilities cap = IOSCapabilities.iphone("eBay");
    cap.setCapability(IOSCapabilities.SIMULATOR, true);

    if (useQA) {
      cap.setCapability(IOSCapabilities.IOS_SWITCHES,
                        Arrays.asList(new String[]{"-e", "useQA", "YES"}));
    }

    RemoteWebDriver driver = new RemoteWebDriver(new URL("http://localhost:4445/wd/hub"), cap);
    WebElement agree = driver.findElement(By.name("l10n('Agree')"));
    agree.click();
    WebElement searchField = driver.findElement(By.linkText("value=Search eBay"));
    searchField.sendKeys("ihpone5");
    WebElement search = driver.findElement(By.xpath("//UIAButton[@label='search']"));
    search.click();

    driver.quit();
    server.stop();


  }


  public static void main2(String[] args) throws Exception {


    String[] a = {"-port", "5555", "-host", "127.0.0.1",
                  //"-aut", SampleApps.getUICatalogFile(),
                  //"-aut", SampleApps.getUICatalogIpad(),
                  //"-beta",
                  "-folder", "applications", "-simulators",
                  "-hub", "http://127.0.0.1:4444/grid/register"};

    IOSServerConfiguration config = IOSServerConfiguration.create(a);

    IOSServer server = new IOSServer(config);
    server.start();

    IOSCapabilities cap = IOSCapabilities.iphone("eBay");
    cap.setCapability(IOSCapabilities.IOS_SWITCHES,
                      Arrays.asList(new String[]{"-e", "useQA", "YES"}));

    RemoteWebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), cap);
    try {

      driver.manage().timeouts().implicitlyWait(45, TimeUnit.SECONDS);

      WebElement agree = driver.findElement(By.name("Agree"));
      agree.click();

      //driver.switchTo().alert().dismiss();
      WebElement signInButton = driver.findElement(By.xpath("//UIAButton[@name='Sign In']"));
      signInButton.click();
      WebElement user = driver.findElement(By.xpath("//UIATextField[@value='User ID or email']"));
      user.sendKeys(userId);
      WebElement pass = driver.findElement(By.xpath("//UIASecureTextField[@value='Password']"));
      pass.sendKeys(password);
      WebElement element4 = driver.findElement(By.xpath("//UIAButton[@name='Done']"));
      element4.click();

      Thread.sleep(2000);
      boolean cartDown = false;
      // navigate
      String itemId = "220003725262";
      if (cartDown) {
        WebElement search = driver.findElement(By.xpath("//UIAButton[@name='Search']"));
        search.click();

        //validate
        WebElement
            element1 =
            driver.findElement(By.xpath("//UIASearchBar[@value='Search Watching']"));
        element1.sendKeys(itemId);

        // check it's there.
        WebElement result = driver.findElement(By.className("UIATableCell"));
        System.out.println(result.getAttribute("name"));

      } else {
        WebElement basket = driver.findElement(By.xpath("//UIAButton[@name='Basket, 1 item']"));
        //By.xpath("//UIAButton[matches(@name,l10n('ItemCountFormatStringPlural'))]"));
        //ItemCountFormatStringSingular
        basket.click();

        //WebElement firstItem = driver.findElement(By.xpath("//UIATableCell[2]"));
        //firstItem.click();

        WebElement
            search =
            driver.findElement(By.name("Checkout"));
        search.click();

      }

      // watch
      WebElement
          paypal =
          driver.findElement(By.name("Please login to your account"));
      paypal.click();

      while (driver.getWindowHandles().size() != 3) {
        Thread.sleep(1000);
      }

      WebElement email = null;
      while (email == null) {
        try {
          driver.switchTo().window("Web_3");
          email = driver.findElement(By.id("email"));
        } catch (NoSuchElementException e) {
          Thread.sleep(1000);
          System.out.println("not found");
        }
      }

      driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

      driver.findElement(By.cssSelector(".resetter")).click();
      email.sendKeys("ppbuild-1920977828325914@paypal.com");

      WebElement password = driver.findElement(By.id("password"));
      password.sendKeys("11111111");

      WebElement login = driver.findElement(By.id("login"));
      login.click();

      Thread.sleep(30000);

    } finally {
      driver.quit();
      server.stop();
    }



  }

  public static void main3(String[] args) throws Exception {
    String[] a = {"-port", "4444", "-host", "localhost",
                  "-aut", "/Users/freynaud/PayPal Here.app"};

    IOSServerConfiguration config = IOSServerConfiguration.create(a);

    IOSServer server = new IOSServer(config);
    server.start();

    IOSCapabilities cap = IOSCapabilities.iphone("PayPal Here");
    cap.setCapability(IOSCapabilities.SIMULATOR, true);

    RemoteWebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), cap);

    WebElement agree = driver.findElement(By.name("Sign Up for PayPal Here"));
    agree.click();

    driver.quit();
    server.stop();
  }

  private static String userId = "francois_uk1";
  private static String password = "password";


}
