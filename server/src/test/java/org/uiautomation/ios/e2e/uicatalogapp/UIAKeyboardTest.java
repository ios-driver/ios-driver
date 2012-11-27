package org.uiautomation.ios.e2e.uicatalogapp;

import org.openqa.selenium.Keyboard;
import org.openqa.selenium.NoSuchElementException;
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

public class UIAKeyboardTest extends BaseIOSDriverTest {

  private RemoteUIADriver driver;

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

  @Test(expectedExceptions = NoSuchElementException.class)
  public void throwsIfKeyboardNotPresent() {
    driver.getKeyboard();
  }

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

  /**
   * 28 on IOS 5 31 on IOS 6
   */
  /*
   * @Test(groups = "broken") public void canFindKeyboard() { RemoteUIADriver
   * driver = null; try {
   * 
   * driver = getDriver(); RemoteUIATarget target = driver.getLocalTarget();
   * RemoteUIAApplication app = target.getFrontMostApp(); RemoteUIAWindow win =
   * app.getMainWindow();
   * 
   * UIATextField textfield = getTextField(win); textfield.tap();
   * 
   * UIAKeyboard keyboard = app.getKeyboard();
   * 
   * UIAElementArray<UIAKey> keys = keyboard.getKeys();
   * Assert.assertTrue(keys.size() == 28 || keys.size() == 31); // GB, should be
   * a qwerty Assert.assertEquals(keys.get(0).getName(), "q");
   * 
   * } finally { if (driver != null) { driver.quit(); } } }
   */

  @Test(dependsOnMethods = { "throwsIfKeyboardNotPresent" })
  public void typeBasic() {

    String message = "Francois 1234";

    UIATextField textfield = getTextField();
    textfield.tap();

    Keyboard keyboard = driver.getKeyboard();
    keyboard.sendKeys(message);

    Assert.assertEquals(textfield.getValue(), message);

  }
}
