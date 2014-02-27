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


@Test(enabled = false,groups = "broken on xcode5.0")
//http://stackoverflow.com/questions/18792965/uiautomations-draginsidewithoptions-has-no-effect-on-ios7-simulator
public class TouchFlickTest extends BaseSeleniumTest {

  // Flick speed is measured in pixels per second.  What constitutes "normal" versus "fast" is entirely up to the
  // application, but Selenium's FlickAction erroneously encodes SPEED_NORMAL and SPEED_FAST as 0 and 1, respectively.
  // Do not use FlickAction's SPEED_NORMAL or SPEED_FAST.  The normal vs. fast tests are intended to test both paths
  // of the JavaScript code that uses flickFromTo() (if (duration == distance/speed) < 0.5s) or dragFromToForDuration().
  private static int speedNormal(int distance) {
    return Math.abs(distance);
  }

  private static int speedFast(int distance) {
    return Math.abs(distance * 3);
  }

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
    int dx = -100;
    Action flick = getBuilder(driver).flick(toFlick, dx, 0, speedNormal(dx)).build();
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
    int dx = -100;
    Action flick = getBuilder(driver).flick(toFlick, dx, 0, speedFast(dx)).build();
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
    int dy = -100;
    Action flick = getBuilder(driver).flick(toFlick, 0, dy, speedNormal(dy)).build();
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
    int dy = -100;
    Action flick = getBuilder(driver).flick(toFlick, 0, dy, speedFast(dy)).build();
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