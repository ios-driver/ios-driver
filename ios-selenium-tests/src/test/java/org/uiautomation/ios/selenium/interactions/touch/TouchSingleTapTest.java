/*
Copyright 2007-2011 Selenium committers

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package org.uiautomation.ios.selenium.interactions.touch;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.testng.annotations.Test;
import org.uiautomation.ios.selenium.BaseSeleniumTest;

import static org.testng.Assert.assertTrue;
import static org.openqa.selenium.TestWaiter.waitFor;

/**
 * Tests single tap actions on touch enabled devices.
 */
public class TouchSingleTapTest extends BaseSeleniumTest {

  private TouchActions getBuilder(WebDriver driver) {
    return new TouchActions(driver);
  }

  private void singleTapOnElement(String elementId) {
    WebElement toSingleTap = driver.findElement(By.id(elementId));
    Action singleTap = getBuilder(driver).singleTap(toSingleTap).build();
    singleTap.perform();
  }

  @Test
  public void testCanSingleTapOnALinkAndFollowIt() throws InterruptedException {
    driver.get(pages.clicksPage);
    singleTapOnElement("normal");
    Thread.sleep(2000);
    waitFor(WaitingConditions.pageTitleToBe(driver, "XHTML Test Page"));
  }

  @Test
  public void testCanSingleTapOnAnAnchorAndNotReloadThePage() {
    driver.get(pages.clicksPage);
    ((JavascriptExecutor) driver).executeScript("document.latch = true");
    singleTapOnElement("anchor");
    Boolean samePage = (Boolean) ((JavascriptExecutor) driver)
        .executeScript("return document.latch");

    assertTrue(samePage);
  }
}
