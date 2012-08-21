package org.uiautomation.ios.e2e.uicatalogapp;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.UIATextField;
import org.uiautomation.ios.UIAModels.UIATextView;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAApplication;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIATarget;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;

public class UIATextViewTest extends UICatalogTestsBase {


  private UIATextView getTextView(RemoteUIAWindow win) {
    String name = "TextView, Use of UITextField";
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria(name);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = win.findElement(c);
    element.tap();
    Criteria fieldC = new TypeCriteria(UIATextView.class);
    UIATextView res = (UIATextView) win.findElement(fieldC);
    return res;
  }


  @Test
  public void valueIsSet() {
    RemoteUIADriver driver = null;
    try {

      driver = getDriver();
      RemoteUIATarget target = driver.getLocalTarget();
      RemoteUIAApplication app = target.getFrontMostApp();
      RemoteUIAWindow win = app.getMainWindow();

      UIATextView textview = getTextView(win);

      String v = "ABC";
      textview.setValue(v);
      Assert.assertEquals(textview.getValue(), v);
      
      v = "ABC\nLine 2\nthanks,\nFrançois";
      textview.setValue(v);
      Assert.assertEquals(textview.getValue(), v);
      
      v = "ABC\tLine 2\tthanks,\tFrançois";
      textview.setValue(v);
      Assert.assertEquals(textview.getValue(), v);

      v = "A\\B ";
      textview.setValue(v);
      Assert.assertEquals(textview.getValue(), v);


    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }


}
