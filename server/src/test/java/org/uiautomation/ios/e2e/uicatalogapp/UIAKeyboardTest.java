package org.uiautomation.ios.e2e.uicatalogapp;

import org.openqa.selenium.Keyboard;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
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
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;


public class UIAKeyboardTest extends BaseIOSDriverTest {

  private RemoteUIADriver driver;
  private UIATextView textview;
  private Keyboard keyboard;

  @BeforeClass
  public void startDriver() {
    driver = new RemoteUIADriver(getRemoteURL(), SampleApps.uiCatalogCap());
    textview = getTextView();
    textview.tap();
    keyboard = driver.getKeyboard();
  }

  @AfterClass
  public void stopDriver() {
    if (driver != null) {
      driver.quit();
    }
  }

  private UIATextView getTextView() {
    String name = "TextView, Use of UITextField";
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
  public void capital() {
    String v = "aBC";
    textview.clear();
    keyboard.sendKeys(v);
    Assert.assertEquals(textview.getValue(), v);
  }

  @Test(enabled = false)
  public void characterRequiresALongTapOnKey() {
    String v = "François";
    textview.clear();
    keyboard.sendKeys(v);
    Assert.assertEquals(textview.getValue(), v);
  }

  @Test
  public void newLines() {
    String v = "ABC\nLine 2\nthanks,\nFrancois";
    textview.clear();
    keyboard.sendKeys(v);
    Assert.assertEquals(textview.getValue(), v);
  }

  @Test(expectedExceptions = WebDriverException.class)
  public void tabs() {
    String v = "AB\tCD";
    keyboard.sendKeys(v);
  }

  @Test
  public void slash() {
    String v = "A\\B ";
    textview.clear();
    keyboard.sendKeys(v);
    Assert.assertEquals(textview.getValue(), v);
  }

  @Test(enabled = false)
  public void shalom() {
    String v = "שָׁלוֹם";
    textview.clear();
    keyboard.sendKeys(v);
    Assert.assertEquals(textview.getValue(), v);
  }
}
