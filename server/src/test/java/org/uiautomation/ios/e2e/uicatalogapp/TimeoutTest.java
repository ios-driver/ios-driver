package org.uiautomation.ios.e2e.uicatalogapp;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;

public class TimeoutTest extends UICatalogTestsBase {


  @Test
  public void getSetTimeout() throws InterruptedException {
    RemoteUIADriver driver = null;
    try {

      driver = getDriver();
      Assert.assertEquals(driver.getTimeout(), 0);
      driver.setTimeout(17);
      Assert.assertEquals(driver.getTimeout(), 17);


    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  //@Test
  public void getElement() throws InterruptedException {
    RemoteUIADriver driver = null;
    try {

      String name = "Buttons, Various uses of UIButton2";
      driver = getDriver();
      driver.setTimeout(10);
      RemoteUIAWindow win = getMainWindow(driver);
      Criteria c1 = new TypeCriteria(UIATableCell.class);
      Criteria c2 = new NameCriteria(name);
      Criteria c = new AndCriteria(c1, c2);
      UIAElement element = win.findElement(c);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }


}
