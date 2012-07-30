package org.uiautomation.ios.e2e.intl;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.OrCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;
import org.uiautomation.ios.server.application.Localizable;

public class CriteriaKeepsOrderTests extends IntlMountainTestsBase {

  @Test(groups = "broken")
  public void orCriteriaReturnsFirstMatch() {
    RemoteUIADriver driver = null;
    try {

      driver = getDriver(Localizable.en);
      RemoteUIAWindow win = getMainWindow(driver);
      Criteria mountain1 = new NameCriteria("Mountain 1");
      Criteria mountain2 = new NameCriteria("Mountain 2");

      UIAElement result1 = win.findElement(new OrCriteria(mountain1, mountain2));
      UIAElement result2 = win.findElement(new OrCriteria(mountain2, mountain1));

      Assert.assertEquals(result1.getName(), "Mountain 1");
      Assert.assertEquals(result2.getName(), "Mountain 2");



    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }


  @Test(groups = "broken")
  public void complexOrCriteriaReturnsFirstMatch() {
    RemoteUIADriver driver = null;
    try {

      driver = getDriver(Localizable.en);
      RemoteUIAWindow win = getMainWindow(driver);
      Criteria mountain1 = new NameCriteria("Mountain 1");
      Criteria mountain2 = new NameCriteria("Mountain 2");
      Criteria mountain3 = new NameCriteria("Mountain 3");


      UIAElement result1 = win.findElement(new OrCriteria(mountain1, mountain2, mountain3));
      UIAElement result3 =
          win.findElement(new OrCriteria(new OrCriteria(mountain3, mountain2), mountain1));

      Assert.assertEquals(result1.getName(), "Mountain 1");
      Assert.assertEquals(result3.getName(), "Mountain 3");



    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
}
