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

import org.json.JSONException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAApplication;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.UIAWindow;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;

import java.util.List;

public class FindElementTest extends BaseIOSDriverTest {

  @BeforeClass
  public void startDriver() {
    driver = getDriver(SampleApps.uiCatalogCap());
    driver.findElement(By.xpath("//UIAButton[@name='UICatalog']")).click();
  }

  private static final String buttonsName = "Buttons";

  @Test
  public void findElementByCriteria() throws InterruptedException {
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria(buttonsName);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = driver.findElement(c);
    Assert.assertEquals(element.getName(), buttonsName);
  }

  @Test
  public void findElementByTagName() throws InterruptedException, JSONException {
    WebElement element = driver.findElement(By.tagName("UIATableCell"));
    Assert.assertEquals(element.getAttribute("name"), "Activity Indicators");
  }

  @Test
  public void findElementsCriteria() throws InterruptedException {
    Criteria cell = new TypeCriteria(UIATableCell.class);
    List<UIAElement> elements = driver.findElements(cell);
    Assert.assertEquals(elements.size(), 17);
  }

  @Test
  public void findElementsByTagName() throws InterruptedException {
    List<WebElement> elements = driver.findElements(By.tagName("UIATableCell"));
    Assert.assertEquals(elements.size(), 17);
  }

  @Test(expectedExceptions = NoSuchElementException.class)
  public void findElementNoResult() throws InterruptedException {
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria("I don't exist.");
    Criteria c = new AndCriteria(c1, c2);
    driver.findElement(c);
    Assert.fail("should have thrown");
  }

  @Test
  public void findElementsNoResult() throws InterruptedException {
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria("I don't exist.");
    Criteria c = new AndCriteria(c1, c2);
    List<UIAElement> elements = driver.findElements(c);
    Assert.assertEquals(elements.size(), 0);
  }

  @Test
  public void findElementOnElementCriteria() throws InterruptedException {
    UIAApplication app = (UIAApplication) driver.findElement(By.tagName("UIAApplication"));
    UIAElement element = app.findElement(new NameCriteria(buttonsName));
    Assert.assertEquals(element.getName(), buttonsName);
    Assert.assertTrue(element instanceof UIATableCell);
  }

  @Test
  public void findElementOnElementTagName() throws InterruptedException {
    UIAApplication app = (UIAApplication) driver.findElement(By.tagName("UIAApplication"));
    WebElement element = app.findElement(By.tagName("UIATableCell"));
    Assert.assertEquals(element.getAttribute("name"), "Activity Indicators");
    Assert.assertTrue(element instanceof UIATableCell);
  }

  @Test
  public void findElementsOnElementTagName() throws InterruptedException {
    UIAApplication app = (UIAApplication) driver.findElement(By.tagName("UIAApplication"));
    List<WebElement> elements = app.findElements(By.tagName("UIATableCell"));
    Assert.assertEquals(elements.size(), 17);
  }

  @Test
  public void findElementsOnElementCriteria() throws InterruptedException {
    UIAApplication app = (UIAApplication) driver.findElement(By.tagName("UIAApplication"));
    List<UIAElement> elements = app.findElements(new TypeCriteria(UIATableCell.class));
    Assert.assertEquals(elements.size(), 17);
  }

  @Test
  public void findElementsOnElementNoResult() throws InterruptedException {
    UIAWindow window = (UIAWindow) driver.findElement(By.tagName("UIAWindow"));
    List<WebElement> elements = window.findElements(By.tagName("UIAButton"));
    Assert.assertEquals(elements.size(), 1);
  }

  @Test
  public void findElementXpath() throws InterruptedException {
    WebElement element = driver.findElement(By.xpath("//UIATableCell"));
    Assert.assertEquals(element.getAttribute("name"), "Activity Indicators");
  }

  @Test
  public void findElementsXpath() throws InterruptedException {
    List<WebElement> elements = driver.findElements(By.xpath("//UIATableCell"));
    Assert.assertEquals(elements.size(), 17);
  }

}
