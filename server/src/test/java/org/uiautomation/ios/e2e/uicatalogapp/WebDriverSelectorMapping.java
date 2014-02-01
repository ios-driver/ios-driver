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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;

public class WebDriverSelectorMapping extends BaseIOSDriverTest {

  @BeforeClass
  public void startDriver() {
    driver = getDriver(SampleApps.intlMountainsCap("en"));
    WebElement element = driver.findElement(By.className("UIATableCell"));
    element.click();
  }

  private
  String
      english =
      "Mountain 1 was first climbed on 29 May 1953 and has a height of 8,848 meters";
  private String partial = "first climbed on 29 May 1953";


  @Test
  public void testCanFindByName() {
    By b = By.name(english);
    WebElement element = driver.findElement(b);
    Assert.assertEquals(element.getAttribute("name"), english);
  }

  @Test
  public void testCanFindByLabel() {
    By b = By.linkText("label=" + english);
    WebElement element = driver.findElement(b);
    Assert.assertEquals(element.getAttribute("label"), english);
  }

  @Test
  public void testCanFindByValue() {
    By b = By.linkText("value=" + english);
    WebElement element = driver.findElement(b);
    Assert.assertEquals(element.getAttribute("value"), english);
  }

  @Test
  public void testCanFindByPartialName() {
    By b = By.partialLinkText("name=" + partial);
    WebElement element = driver.findElement(b);
    Assert.assertEquals(element.getAttribute("name"), english);
  }

  @Test
  public void testCanFindByRegexName() {
    By b = By.partialLinkText("name=first climbed on (.*) and has a height");
    WebElement element = driver.findElement(b);
    Assert.assertEquals(element.getAttribute("name"), english);
  }


  @Test
  public void testCanFindByPartialLabel() {
    By b = By.partialLinkText("label=" + partial);
    WebElement element = driver.findElement(b);
    Assert.assertEquals(element.getAttribute("label"), english);
  }

  @Test
  public void testCanFindByPartialValue() {
    By b = By.partialLinkText("value=" + partial);
    WebElement element = driver.findElement(b);
    Assert.assertEquals(element.getAttribute("value"), english);
  }

  @Test
  public void testCannotFindById() {
    By b = By.id(english);
    driver.findElement(b);
  }

  @Test
  public void testCanFindByNamel10n() {
    By b = By.partialLinkText("name=l10n('sentenceFormat')");
    WebElement element = driver.findElement(b);
    Assert.assertEquals(element.getAttribute("name"), english);
  }

  @Test
  public void testCanFindByLabell10n() {
    By b = By.partialLinkText("label=l10n('sentenceFormat')");
    WebElement element = driver.findElement(b);
    Assert.assertEquals(element.getAttribute("label"), english);
  }

}
