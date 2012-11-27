package org.uiautomation.ios.e2e.uicatalogapp;

import org.openqa.selenium.WebDriver;
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

public class WebDriverKeyboardTest extends BaseIOSDriverTest {

  private WebDriver driver;

  @BeforeClass
  public void startDriver() {
    driver = new RemoteUIADriver(getRemoteURL(), SampleApps.uiCatalogCap());
  }

  @AfterClass
  public void stopDriver() {
    if (driver != null) {
      driver.quit();
    }
  }

  private UIATextField getTextField() {
    String name = "TextFields, Uses of UITextField";
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria(name);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = ((RemoteUIADriver) driver).findElement(c);
    element.tap();
    Criteria fieldC = new AndCriteria(new TypeCriteria(UIATextField.class), new NameCriteria("Normal"));
    UIATextField textfield = (UIATextField) ((RemoteUIADriver) driver).findElement(fieldC);
    return textfield;
  }

  @Test
  public void typeBasic() {
    String message = "François 1234";
    UIATextField textfield = getTextField();
    textfield.sendKeys(message);
    Assert.assertEquals(textfield.getValue(), message);

  }
}