package org.uiautomation.ios.e2e.uicatalogapp;

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
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.communication.WebDriverLikeCommand;

public class UIATextViewTest extends BaseIOSDriverTest {

  private RemoteIOSDriver driver;
  private UIATextView textview;

  @BeforeClass
  public void startDriver() {
    driver = new RemoteIOSDriver(getRemoteURL(), SampleApps.uiCatalogCap());
    textview = getTextView();
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
