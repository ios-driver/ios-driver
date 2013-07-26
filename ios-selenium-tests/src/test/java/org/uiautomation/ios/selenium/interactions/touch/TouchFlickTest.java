/*
Copyright 2012 Software Freedom Conservancy
Copyright 2007-2012 Selenium committers

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

import org.openqa.selenium.By;
import org.openqa.selenium.NeedsFreshDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.touch.FlickAction;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.testng.annotations.Test;
import org.uiautomation.ios.selenium.BaseSeleniumTest;

import static org.junit.Assert.assertTrue;

/**
 * Tests the basic flick operations on touch enabled devices.
 */
public class TouchFlickTest extends BaseSeleniumTest {

  private TouchActions getBuilder(WebDriver driver) {
    return new TouchActions(driver);
  }

  @NeedsFreshDriver
  @Test
  public void testCanFlickHorizontallyFromWebElement() {
    driver.get(pages.longContentPage);

    WebElement toFlick = driver.findElement(By.id("imagestart"));
    WebElement link = driver.findElement(By.id("link1"));
    int originalX = link.getLocation().x;
    // The element is located at the right of the page,
    // so it is not initially visible on the screen.
    Action flick = getBuilder(driver).flick(toFlick, -1000, 0, FlickAction.SPEED_NORMAL)
        .build();
    flick.perform();

    int newX = link.getLocation().x;
    // After flicking, the element should now be visible on the screen.
    assertTrue("Expected x < " + originalX + ", but got x = " + newX, newX < originalX);
  }

  @NeedsFreshDriver
  @Test
  public void testCanFlickHorizontallyFastFromWebElement() {
    driver.get(pages.longContentPage);

    WebElement toFlick = driver.findElement(By.id("imagestart"));
    WebElement link = driver.findElement(By.id("link2"));
    int originalX = link.getLocation().x;
    // The element is located at the right of the page,
    // so it is not initially visible on the screen.
    Action flick = getBuilder(driver).flick(toFlick, -1000, 0, FlickAction.SPEED_FAST)
        .build();
    flick.perform();
    int newX = link.getLocation().x;
    // After flicking, the element should now be visible on the screen.
    assertTrue("Expected x < " + originalX + ", but got x = " + newX, newX < originalX);
  }

  @NeedsFreshDriver
  @Test
  public void testCanFlickHorizontally() {
    driver.get(pages.clicksPage);
    driver.get(pages.longContentPage);

    WebElement link = driver.findElement(By.id("link1"));
    int originalX = link.getLocation().x;
    // The element is located at the right of the page,
    // so it is not initially visible on the screen.
    Action flick = getBuilder(driver).flick(-1000, 0).build();
    flick.perform();
    int newX = link.getLocation().x;
    // After flicking, the element should now be visible on the screen.
    assertTrue("Expected x < " + originalX + ", but got x = " + newX, newX < originalX);
  }

  @NeedsFreshDriver
  @Test
  public void testCanFlickHorizontallyFast() {
    driver.get(pages.longContentPage);

    WebElement link = driver.findElement(By.id("link2"));
    int originalX = link.getLocation().x;
    // The element is located at the right of the page,
    // so it is not initially visible on the screen.
    Action flick = getBuilder(driver).flick(-1500, 0).build();
    flick.perform();
    int newX = link.getLocation().x;
    // After flicking, the element should now be visible on the screen.
    assertTrue("Expected x < " + originalX + ", but got x = " + newX, newX < originalX);
  }

  @NeedsFreshDriver
  @Test
  public void testCanFlickVerticallyFromWebElement() {
    driver.get(pages.longContentPage);

    WebElement link = driver.findElement(By.id("link3"));
    int originalY = link.getLocation().y;
    // The element is located at the bottom of the page,
    // so it is not initially visible on the screen.
    WebElement toFlick = driver.findElement(By.id("imagestart"));
    Action flick = getBuilder(driver).flick(toFlick, 0, -600, FlickAction.SPEED_NORMAL)
        .build();
    flick.perform();
    int newY = link.getLocation().y;
    // After flicking, the element should now be visible on the screen.
    assertTrue("Expected y < " + originalY + ", but got y = " + newY, newY < originalY);
  }

  @NeedsFreshDriver
  @Test
  public void testCanFlickVerticallyFastFromWebElement() {
    driver.get(pages.longContentPage);

    WebElement link = driver.findElement(By.id("link4"));
    int originalY = link.getLocation().y;
    // The element is located at the bottom of the page,
    // so it is not initially visible on the screen.
    WebElement toFlick = driver.findElement(By.id("imagestart"));
    Action flick = getBuilder(driver).flick(toFlick, 0, -600, FlickAction.SPEED_FAST)
        .build();
    flick.perform();
    int newY = link.getLocation().y;
    // After flicking, the element should now be visible on the screen.
    assertTrue("Expected y < " + originalY + ", but got y = " + newY, newY < originalY);
  }

  @NeedsFreshDriver
  @Test
  public void testCanFlickVertically() {
    driver.get(pages.longContentPage);

    WebElement link = driver.findElement(By.id("link3"));
    int originalY = link.getLocation().y;
    // The element is located at the bottom of the page,
    // so it is not initially visible on the screen.
    Action flick = getBuilder(driver).flick(0, -1500).build();
    flick.perform();
    int newY = link.getLocation().y;

    // After flicking, the element should now be visible on the screen.
    assertTrue("Expected y < " + originalY + ", but got y = " + newY, newY < originalY);
  }

  @NeedsFreshDriver
  @Test
  public void testCanFlickVerticallyFast() {
    driver.get(pages.longContentPage);

    WebElement link = driver.findElement(By.id("link4"));
    int originalY = link.getLocation().y;
    // The element is located at the bottom of the page,
    // so it is not initially visible on the screen.
    Action flick = getBuilder(driver).flick(0, -1500).build();
    flick.perform();
    int newY = link.getLocation().y;
    // After flicking, the element should now be visible on the screen.
    assertTrue("Expected y < " + originalY + ", but got y = " + newY, newY < originalY);
  }

}