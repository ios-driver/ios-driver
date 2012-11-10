package org.uiautomation.ios.e2e.uicatalogapp;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.LocationCriteria;
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

  @Test
  public void positionMatch() {
    RemoteUIADriver driver = null;
    try {
      driver = getDriver();

      String name = "Buttons, Various uses of UIButton";
      Criteria c1 = new TypeCriteria(UIATableCell.class);
      PropertyEqualCriteria c2 = new NameCriteria(name);
      Assert.assertEquals(c2.getMatchingStrategy(), MatchingStrategy.exact);
      Criteria c = new AndCriteria(c1, c2);
      UIAElement element = driver.findElement(c);

      UIARect position = element.getRect();
      System.out.println("position : " + position);

      // top left corner
      Criteria test = new LocationCriteria(position.getX(), position.getY());
      UIAElement res = driver.findElement(test);
      Assert.assertEquals(res.getName(), name);

      // bottom right corner
      int x = position.getX() + position.getWidth()-1;
      int y = position.getY() + position.getHeight()-1;
      test = new LocationCriteria(x, y);
      res = driver.findElement(test);
      Assert.assertEquals(res.getName(), name);

      // middle
      x = position.getX() + (position.getWidth() / 2);
      y = position.getY() + (position.getHeight() / 2);
      test = new LocationCriteria(x, y);
      res = driver.findElement(test);
      Assert.assertEquals(res.getName(), name);


    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

}
