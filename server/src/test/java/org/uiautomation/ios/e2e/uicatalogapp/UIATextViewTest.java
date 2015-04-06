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
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.UIATextView;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.communication.WebDriverLikeCommand;

public class UIATextViewTest extends BaseIOSDriverTest {

  private UIATextView textview;

  @BeforeClass
  public void startDriver() {
    driver = getDriver(SampleApps.uiCatalogCap());
    driver.findElement(By.xpath("//UIAButton[@name='UICatalog']")).click();
    textview = getTextView();
  }

  private UIATextView getTextView() {
    String name = "Text View";
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria(name);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = driver.findElement(c);
    element.tap();
    Criteria fieldC = new TypeCriteria(UIATextView.class);
    UIATextView res = (UIATextView) driver.findElement(fieldC);
    return res;
  }

  @Test
  public void capital() throws InterruptedException {
    Thread.sleep(200);
    driver.setConfiguration(WebDriverLikeCommand.SET_VALUE, "nativeEvents", true);
    String v = "ABC";
    textview.clear();
    textview.setValue(v);
    Assert.assertEquals(textview.getValue(), v);
  }

  @Test
  public void newLinesAndTabs() {
    driver.setConfiguration(WebDriverLikeCommand.SET_VALUE, "nativeEvents", false);
    String v = "ABC\nLine 2\t col3\nthanks,\nFrançois";
    textview.clear();
    textview.setValue(v);
    Assert.assertEquals(textview.getValue(), v);
  }

  @Test
  public void slash() {
    driver.setConfiguration(WebDriverLikeCommand.SET_VALUE, "nativeEvents", true);
    String v = "A\\B ";
    textview.clear();
    textview.setValue(v);
    Assert.assertEquals(textview.getValue(), v);
  }

  @Test
  public void shalom() {
    driver.setConfiguration(WebDriverLikeCommand.SET_VALUE, "nativeEvents", false);
    String v = "שָׁלוֹם";
    textview.clear();
    textview.setValue(v);
    Assert.assertEquals(textview.getValue(), v);
  }
}
