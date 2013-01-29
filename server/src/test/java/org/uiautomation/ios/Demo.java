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

package org.uiautomation.ios;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.lang.AssertionError;
import java.lang.System;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * example for the documentation
 */
public class Demo extends BaseIOSDriverTest {

  public static void main2(String[] args) throws MalformedURLException {
    // create a selenium desiredCapabilities object with the right values.
    DesiredCapabilities cap = IOSCapabilities.iphone("InternationalMountains", "1.1");
    cap.setCapability(IOSCapabilities.LANGUAGE, "zh");

    // start the application
    RemoteWebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), cap);

    // check that the 9 mountains of the app are there.
    List<WebElement> cells = driver.findElements(By.className("UIATableCell"));
    Assert.assertEquals(9, cells.size());

    // get the 1st mountain
    WebElement first = cells.get(0);
    first.click();

    // take a screenshot using the normal selenium api.
    TakesScreenshot screen = (TakesScreenshot) new Augmenter().augment(driver);
    File ss = new File("screenshot-zh.png");
    screen.getScreenshotAs(OutputType.FILE).renameTo(ss);
    System.out.println("screenshot take :" + ss.getAbsolutePath());

    // access the content
    By contentFree = By.xpath("//UIAStaticText[matches(@name,l10n('sentenceFormat'))]");
    WebElement text = driver.findElement(contentFree);
    System.out.println(text.getAttribute("name"));

    // end the test
    driver.quit();
  }

  public static void main3(String[] args) throws Exception {
    DesiredCapabilities safari = IOSCapabilities.iphone("Safari");
    RemoteWebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), safari);

    driver.get("http://www.ebay.co.uk/");

    System.out.println(driver.getTitle());
    driver.quit();
  }


}
