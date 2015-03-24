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
import org.openqa.selenium.interactions.Keyboard;
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

public class UIAKeyboardSmokeTest extends BaseIOSDriverTest {

  private UIATextField textfield;

  @BeforeClass
  public void startDriver() {
    driver = getDriver(SampleApps.uiCatalogCap());
    driver.findElement(By.xpath("//UIAButton[@name='UICatalog']")).click();
  }

  @Test
  public void doesntThrowIfKeyboardNotPresent() {
    driver.getKeyboard();
  }

  private UIATextField getTextField() {
    String name = "Text Fields";
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria(name);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = driver.findElement(c);
    element.tap();
    Criteria fieldC = new TypeCriteria(UIATextField.class);
    textfield = (UIATextField) driver.findElement(fieldC);
    return textfield;
  }

  @Test
  public void typeBasic() {
    String message = "Francois 1234";

    UIATextField textfield = getTextField();
    textfield.tap();

    Keyboard keyboard = driver.getKeyboard();
    keyboard.sendKeys(message);

    Assert.assertEquals(textfield.getValue(), message);
  }

  // TODO : the keyboard is now inherited from the host. Need to overwrite it.
  @Test(dependsOnMethods = {"typeBasic"},enabled = false)
  public void sendKeys() {
    textfield.clear();
    textfield.sendKeys("ç");
    textfield.sendKeys("é");
    textfield.sendKeys("Ē");
    textfield.clear();
    textfield.sendKeys("François !");

    Assert.assertEquals("François !", textfield.getAttribute("value"));
  }

}
