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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeoutTest extends BaseIOSDriverTest {
    
  @AfterMethod(alwaysRun = true)
  public void afterMethod() throws Exception {
    stopDriver();
  }
    
  @Test
  public void getElement() throws InterruptedException {
    driver = getDriver(SampleApps.uiCatalogCap());
    driver.findElement(By.xpath("//UIAButton[@name='UICatalog']")).click();
    String name = "Buttons2";

    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria(name);
    Criteria c = new AndCriteria(c1, c2);
    long start = System.currentTimeMillis();
    try {
      driver.findElement(c);
      Assert.fail("shouldn't find element" + name);
    } catch (NoSuchElementException e) {
      long total = System.currentTimeMillis() - start;
      Assert.assertTrue(total < 2000);
    }
    try {
      driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
      start = System.currentTimeMillis();
      driver.findElement(c);
      Assert.fail("shouldn't find element" + name);
    } catch (NoSuchElementException e) {
      long total = System.currentTimeMillis() - start;
      Assert.assertTrue(total > 2000);
    }
  }

  @Test
  public void getElementByXPathUsesImplicitWait() {
    driver = getDriver(SampleApps.uiCatalogCap());
    driver.findElement(By.xpath("//UIAButton[@name='UICatalog']")).click();
    // TODO freynaud fic SetImplicitWaitTimeoutNHandler.Timeout.
    driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
    String name = "Buttons2";

    // single
    driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
    By b = By.xpath("//*[@name='" + name + "']");
    long start = System.currentTimeMillis();
    try {
      driver.findElement(b);
      Assert.fail("shouldn't find element" + name);
    } catch (NoSuchElementException e) {
      long total = System.currentTimeMillis() - start;
      Assert.assertTrue(total < 2000);
    }

    // multi
    try {
      List<WebElement> els = driver.findElements(b);
      Assert.assertTrue(els.size() == 0);
    } catch (NoSuchElementException e) {
      long total = System.currentTimeMillis() - start;
      Assert.assertTrue(total < 2000);
    }

    // single
    driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
    try {
      start = System.currentTimeMillis();
      driver.findElement(b);
      Assert.fail("shouldn't find element" + name);
    } catch (NoSuchElementException e) {
      long total = System.currentTimeMillis() - start;
      Assert.assertTrue(total > 2000);
    }

    // multi
    try {
      start = System.currentTimeMillis();
      List<WebElement> els = driver.findElements(b);
      Assert.assertTrue(els.size() == 0);
    } catch (NoSuchElementException e) {
      long total = System.currentTimeMillis() - start;
      Assert.assertTrue(total > 2000);
    }
  }
}
