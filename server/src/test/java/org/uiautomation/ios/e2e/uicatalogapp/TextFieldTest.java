package org.uiautomation.ios.e2e.uicatalogapp;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.UIATextField;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAApplication;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIATarget;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;

public class TextFieldTest extends UICatalogTestsBase {


  private UIATextField getTextField(RemoteUIAWindow win) {
    String name = "TextFields, Uses of UITextField";
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria(name);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = win.findElement(c);
    element.tap();
    Criteria fieldC =
        new AndCriteria(new TypeCriteria(UIATextField.class), new NameCriteria("Normal"));
    UIATextField textfield = (UIATextField) win.findElement(fieldC);
    return textfield;
  }


  @Test
  public void valueIsSet() {
    RemoteUIADriver driver = null;
    try {

      driver = getDriver();
      RemoteUIATarget target = driver.getLocalTarget();
      RemoteUIAApplication app = target.getFrontMostApp();
      RemoteUIAWindow win = app.getMainWindow();

      UIATextField textfield = getTextField(win);
      
      String v = "ABC";
      textfield.setValue(v);
      Assert.assertEquals(textfield.getValue(), v);
      
      v = "aBc";
      textfield.setValue(v);
      Assert.assertEquals(textfield.getValue(), v);
      
      v = "François";
      textfield.setValue(v);
      Assert.assertEquals(textfield.getValue(), v);

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

}
