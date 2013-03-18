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
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.LocationCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;

public class MultiWindowTest extends BaseSeleniumTest {

  @Test
  public void canSwitchBetweenWindows() throws Exception {
    driver.get(appServer.whereIs("click_frames.html"));
    //http://localhost:7694/common/click_frames.html
    driver.switchTo().frame("source");

    driver.findElement(By.id("new-window")).click();

    // native + 2 web windows.
    Thread.sleep(2000); // wait for the new window to load
    Assert.assertEquals(driver.getWindowHandles().size(), 3);

    // mobile safari auto switches to a newly opened window.
    driver.switchTo().window("Web_1");
    driver.switchTo().defaultContent();
    Assert.assertEquals(driver.getTitle(), "click frames");
    Assert.assertTrue(driver.getCurrentUrl().endsWith("click_frames.html"));

    driver.switchTo().window("Web_2");
    Assert.assertEquals(driver.getTitle(), "XHTML Test Page");
    Assert.assertTrue(driver.getCurrentUrl().endsWith("xhtmlTest.html"));

    driver.switchTo().window("Web_1");
    Assert.assertEquals(driver.getTitle(), "click frames");
    Assert.assertTrue(driver.getCurrentUrl().endsWith("click_frames.html"));

    driver.switchTo().window("Web_2");
    Assert.assertEquals(driver.getTitle(), "XHTML Test Page");
    Assert.assertTrue(driver.getCurrentUrl().endsWith("xhtmlTest.html"));

    driver.switchTo().window("Web_1");
    Assert.assertEquals(driver.getTitle(), "click frames");
    Assert.assertTrue(driver.getCurrentUrl().endsWith("click_frames.html"));

    driver.switchTo().window("Web_1");
    Assert.assertEquals(driver.getTitle(), "click frames");
    Assert.assertTrue(driver.getCurrentUrl().endsWith("click_frames.html"));


  }

  @Test(enabled = false)
  public void canNavigateBetweenWindows() throws InterruptedException {
    driver.get(appServer.whereIs("click_frames.html"));
    //http://localhost:7694/common/click_frames.html
    driver.switchTo().frame("source");

    // click to create a new window.
    driver.findElement(By.id("new-window")).click();
    waitForWindow(driver, "Web_2");

    // native + 2 web windows.
    Assert.assertEquals(driver.getWindowHandles().size(), 3);

    // mobile safari auto switches to a newly opened window.
    switchTo("Web_1");
    driver.switchTo().defaultContent();
    Assert.assertEquals(driver.getTitle(), "click frames");
    Assert.assertTrue(driver.getCurrentUrl().endsWith("click_frames.html"));

    // click to create window n°3.
    driver.switchTo().frame("source");
    driver.findElement(By.id("new-window")).click();
    waitForWindow(driver, "Web_3");

    switchTo("Web_1");
    switchTo("Web_3");
    switchTo("Web_1");
    switchTo("Web_2");


  }

  private void switchTo(String handle) {
    driver.switchTo().window(handle);
    Assert.assertEquals(driver.getWindowHandle(), handle);
  }

  private void waitForWindow(WebDriver driver, String handle) {
    while (!driver.getWindowHandle().equals(handle)) {
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        // ignore
      }
    }
  }

  public static void main(String[] args) {

    Criteria c = new AndCriteria(new TypeCriteria(UIAElement.class), new LocationCriteria(0, 10));

    System.out.println(c.stringify());

  }


}
