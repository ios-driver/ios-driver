/*
Copyright 2007-2009 Selenium committers

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
package org.uiautomation.ios.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.util.concurrent.Callable;

import static org.testng.Assert.assertEquals;

public class RenderedWebElementTest extends BaseSeleniumTest {

  @Test
  public void testShouldPickUpStyleOfAnElement() {
    driver.get(pages.javascriptPage);

    WebElement element = driver.findElement(By.id("green-parent"));
    String backgroundColour = element.getCssValue("background-color");

    assertEquals(backgroundColour,"rgba(0, 128, 0, 1)");

    element = driver.findElement(By.id("red-item"));
    backgroundColour = element.getCssValue("background-color");

    assertEquals(backgroundColour,"rgba(255, 0, 0, 1)");
  }

  @Test
  public void testGetCssValueShouldReturnStandardizedColour() {
    driver.get(pages.colorPage);

    WebElement element = driver.findElement(By.id("namedColor"));
    String backgroundColour = element.getCssValue("background-color");
    assertEquals(backgroundColour,"rgba(0, 128, 0, 1)");

    element = driver.findElement(By.id("rgb"));
    backgroundColour = element.getCssValue("background-color");
    assertEquals(backgroundColour,"rgba(0, 128, 0, 1)");

  }

  @Test
  public void testShouldAllowInheritedStylesToBeUsed() {
    driver.get(pages.javascriptPage);

    WebElement element = driver.findElement(By.id("green-item"));
    String backgroundColour = element.getCssValue("background-color");

    assertEquals(backgroundColour, "rgba(0, 0, 0, 0)");
  }

  @Test
  public void testShouldGetHeightCss() {
    driver.get(pages.javascriptPage);

    WebElement element = driver.findElement(By.id("green-item"));
    String height = element.getCssValue("height");

    assertEquals(height, "30px");
  }

  @Test
  public void testShouldGetTextDecorationCss() {
    driver.get(pages.javascriptPage);

    WebElement element = driver.findElement(By.tagName("a"));
    String height = element.getCssValue("text-decoration");

    assertEquals(height, "underline");
  }

  private boolean fuzzyPositionMatching(int expectedX, int expectedY, String locationTouple) {
    String[] splitString = locationTouple.split(",");
    int gotX = Integer.parseInt(splitString[0].trim());
    int gotY = Integer.parseInt(splitString[1].trim());

    // Everything within 5 pixels range is OK
    final int ALLOWED_DEVIATION = 5;
    return Math.abs(expectedX - gotX) < ALLOWED_DEVIATION &&
        Math.abs(expectedY - gotY) < ALLOWED_DEVIATION;

  }

  private Callable<Boolean> fuzzyMatchingOfCoordinates(
      final WebElement element, final int x, final int y) {
    return new Callable<Boolean>() {
      public Boolean call() throws Exception {
        return fuzzyPositionMatching(x, y, element.getText());
      }

      @Override
      public String toString() {
        return "Coordinates: " + element.getText() + " but expected: " +
            x + ", " + y;
      }
    };
  }
}
