package org.uiautomation.ios.e2e.uicatalogapp;

import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.UIADriver;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAElementArray;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.UIATextField;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;

public class ActiveTest extends UICatalogTestsBase {


  private void getTextField(UIADriver driver) {
    String name = "TextFields, Uses of UITextField";
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    Criteria c2 = new NameCriteria(name);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = driver.findElement(c);
    element.tap();
   
  }


  @Test
  public void valueIsSet() {
    RemoteUIADriver driver = null;
    try {

      driver = getDriver();
      getTextField(driver);

    UIAElementArray<UIAElement> all = driver.findElements(new TypeCriteria(UIATextField.class));
    for (UIAElement field : all){
      //System.out.println(field.isActive());
    }

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
}
