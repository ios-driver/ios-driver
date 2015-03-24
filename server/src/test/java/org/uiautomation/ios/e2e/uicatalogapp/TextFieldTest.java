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
import org.uiautomation.ios.UIAModels.UIATextField;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.communication.WebDriverLikeCommand;

public class TextFieldTest extends BaseIOSDriverTest {

  private UIATextField textfield;

  private UIATextField getTextField() {
    String name = "Text Fields";
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria(name);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = driver.findElement(c);
    element.tap();
    Criteria fieldC = new TypeCriteria(UIATextField.class);
      // add more tests to other text fields
    UIATextField textfield = (UIATextField) driver.findElement(fieldC);
    return textfield;
  }

  @BeforeClass
  public void startDriver() {
    driver = getDriver(SampleApps.uiCatalogCap());
    driver.findElement(By.xpath("//UIAButton[@name='UICatalog']")).click();
    textfield = getTextField();
  }

  @Test
  public void capital() {
    driver.setConfiguration(WebDriverLikeCommand.SET_VALUE, "nativeEvents", true);
    String v = "ABC";
    textfield.clear();
    textfield.setValue(v);
    Assert.assertEquals(textfield.getValue(), v);
  }

  @Test
  public void lowerCase() {
    driver.setConfiguration(WebDriverLikeCommand.SET_VALUE, "nativeEvents", true);
    String v = "abc";
    textfield.clear();
    textfield.setValue(v);
    Assert.assertEquals(textfield.getValue(), v);
  }

  @Test
  public void both() {
    driver.setConfiguration(WebDriverLikeCommand.SET_VALUE, "nativeEvents", true);
    String v = "aBc";
    textfield.clear();
    textfield.setValue(v);
    Assert.assertEquals(textfield.getValue(), v);
  }

  @Test
  public void intl() {
    driver.setConfiguration(WebDriverLikeCommand.SET_VALUE, "nativeEvents", true);
    String v = "François";
    textfield.clear();
    textfield.setValue(v);
    Assert.assertEquals(textfield.getValue(), v);
  }

  @Test
  public void shalom() {
    driver.setConfiguration(WebDriverLikeCommand.SET_VALUE, "nativeEvents", false);
    String v = "שָׁלוֹם";
    textfield.clear();
    textfield.setValue(v);
    Assert.assertEquals(textfield.getValue(), v);
  }

  @Test // TODO appears the other way around on the screen. Correct ?
  public void shalom2() {
    driver.setConfiguration(WebDriverLikeCommand.SET_VALUE, "nativeEvents", false);
    String v = "שָׁלוֹם François";
    textfield.clear();
    textfield.setValue(v);
    Assert.assertEquals(textfield.getValue(), v);
  }

}
