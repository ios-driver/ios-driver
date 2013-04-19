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
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.selenium.BaseSeleniumTest;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.TestWaiter.waitFor;
import static org.openqa.selenium.WaitingConditions.alertToBePresent;
import static org.openqa.selenium.WaitingConditions.elementTextToEqual;
import static org.openqa.selenium.WaitingConditions.windowHandleCountToBe;
import static org.testng.Assert.*;

/**
 * Tests the basic double tap operations.
 */
public class TouchDoubleTapTest extends BaseSeleniumTest {

  private TouchActions getBuilder(WebDriver driver) {
    return new TouchActions(driver);
  }

  private void doubleTapOnElement(String elementId) {
    WebElement toDoubleTap = driver.findElement(By.id(elementId));
    Action doubleTap = getBuilder(driver).doubleTap(toDoubleTap).build();
    doubleTap.perform();
  }

  @Test
  public void testCanDoubleTapOnAnImageAndAlterLocationOfElementsInScreen() {
    driver.get(pages.longContentPage);

    WebElement image = driver.findElement(By.id("imagestart"));
    int y = image.getLocation().getY();
    // The element is located at a certain point, after double tapping,
    // the y coordinate must change.
    assertTrue(y > 100);

    doubleTapOnElement("imagestart");

    y = image.getLocation().y;
    assertTrue(y < 50);
  }

}
