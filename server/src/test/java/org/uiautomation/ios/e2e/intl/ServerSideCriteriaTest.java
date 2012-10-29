package org.uiautomation.ios.e2e.intl;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.L10NStrategy;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;
import org.uiautomation.ios.server.application.Localizable;

public class ServerSideCriteriaTest extends IntlMountainTestsBase {



  private String expected =
      "Bien que 8,848 mètres de haut, Montagne 1 aient été montés la première fois 29 May 1953.";


  @Test
  public void findElementDriver() {
    RemoteUIADriver driver = null;
    try {

      driver = getDriver(Localizable.fr);
      Criteria c1 = new TypeCriteria(UIATableCell.class);
      UIAElement element = driver.findElement(c1);
      element.tap();

      NameCriteria criteria =
          new NameCriteria("sentenceFormat", L10NStrategy.serverL10N, MatchingStrategy.regex);
      UIAElement text = driver.findElement(criteria);
      String actual = text.getName();
      Assert.assertEquals(actual, expected);

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void findElementElement() {
    RemoteUIADriver driver = null;
    try {

      driver = getDriver(Localizable.fr);
      RemoteUIAWindow win = getMainWindow(driver);
      Criteria c1 = new TypeCriteria(UIATableCell.class);
      UIAElement element = win.findElement(c1);
      element.tap();

      NameCriteria criteria =
          new NameCriteria("sentenceFormat", L10NStrategy.serverL10N, MatchingStrategy.regex);
      UIAElement text = win.findElement(criteria);
      String actual = text.getName();
      Assert.assertEquals(actual, expected);

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }



}
