package org.uiautomation.ios.e2e.uicatalogapp;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.L10NStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.PropertyEqualCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;

public class CriteriaTest extends UICatalogTestsBase {


  @Test
  public void exactMatch() {
    RemoteUIADriver driver = null;
    try {
      String name = "Buttons, Various uses of UIButton";
      driver = getDriver();
      RemoteUIAWindow win = getMainWindow(driver);
      Criteria c1 = new TypeCriteria(UIATableCell.class);
      PropertyEqualCriteria c2 = new NameCriteria(name);
      Assert.assertEquals(c2.getMatchingStrategy(), MatchingStrategy.exact);
      Criteria c = new AndCriteria(c1, c2);
      UIAElement element = win.findElement(c);
      Assert.assertEquals(element.getName(), name);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void regexMatch() {
    RemoteUIADriver driver = null;
    try {
      String name = "Buttons, Various uses of UIButton";
      String regex = "Buttons, V[a-z]* uses of UIButton";
      driver = getDriver();
      RemoteUIAWindow win = getMainWindow(driver);
      Criteria c1 = new TypeCriteria(UIATableCell.class);
      PropertyEqualCriteria c2 = new NameCriteria(regex, MatchingStrategy.regex);
      Criteria c = new AndCriteria(c1, c2);
      UIAElement element = win.findElement(c);
      Assert.assertEquals(element.getName(), name);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }


}
