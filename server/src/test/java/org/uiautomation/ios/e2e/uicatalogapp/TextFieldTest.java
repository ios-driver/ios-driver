package org.uiautomation.ios.e2e.uicatalogapp;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
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
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;

public class TextFieldTest extends BaseIOSDriverTest {

  private RemoteUIADriver driver = null;
  private UIATextField textfield;

  private UIATextField getTextField() {
    String name = "TextFields, Uses of UITextField";
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria(name);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = driver.findElement(c);
    element.tap();
    Criteria fieldC = new AndCriteria(new TypeCriteria(UIATextField.class), new NameCriteria("Normal"));
    UIATextField textfield = (UIATextField) driver.findElement(fieldC);
    return textfield;
  }

  @BeforeClass
  public void startDriver() {
    driver = new RemoteUIADriver(getRemoteURL(), SampleApps.uiCatalogCap());
    textfield = getTextField();
  }

  @AfterClass
  public void stopDriver() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Test
  public void capital() {
    String v = "ABC";
    textfield.setValue(v);
    Assert.assertEquals(textfield.getValue(), v);
  }

  @Test
  public void lowerCase() {
    String v = "abc";
    textfield.setValue(v);
    Assert.assertEquals(textfield.getValue(), v);
  }

  @Test
  public void both() {
    String v = "aBc";
    textfield.setValue(v);
    Assert.assertEquals(textfield.getValue(), v);
  }

  @Test
  public void intl() {
    String v = "François";
    textfield.setValue(v);
    Assert.assertEquals(textfield.getValue(), v);
  }

  @Test
  public void shalom() {
    String v = "שָׁלוֹם";
    textfield.setValue(v);
    Assert.assertEquals(textfield.getValue(), v);
  }
  
  @Test // TODO appears the other way around on the screen. Correct ?
  public void shalom2() {
    String v = "שָׁלוֹם François";
    textfield.setValue(v);
    Assert.assertEquals(textfield.getValue(), v);
  }

}
