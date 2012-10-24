package org.uiautomation.ios.e2e.uicatalogapp;

import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;

public class FindElementTest extends UICatalogTestsBase {

  @Test
  public void findElementOnRoot() throws InterruptedException {
    RemoteUIADriver driver = null;
    try {
      driver = getDriver();
      
      String name = "Buttons, Various uses of UIButton";
      Criteria c1 = new TypeCriteria(UIATableCell.class);
      Criteria c2 = new NameCriteria(name);
      Criteria c = new AndCriteria(c1, c2);
     
      UIAElement element = driver.findElement(c);
      System.out.println(element);

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

}
