package org.uiautomation.ios.e2e.intl;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.L10NStrategy;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.server.application.Localizable;

public class ComplexCriteriaTest extends BaseIOSDriverTest {

  @DataProvider(name = "intlMountain", parallel = false)
  public Object[][] createDataPerLocale() {
    return new Object[][] {
        { Localizable.en, "Mountain 1 was first climbed on 29 May 1953 and has a height of 8,848 meters" },
        { Localizable.fr, "Bien que 8,848 mètres de haut, Montagne 1 aient été montés la première fois 29 May 1953." },
        { Localizable.zh, "山 1 是8,848米高。它第一次攀登了在29 May 1953。" } };
  }

  @Test(dataProvider = "intlMountain")
  public void selectAndValidateServerSideL10NedContent(Localizable l, String expectedContent) {
    RemoteUIADriver driver = null;
    try {

      driver = new RemoteUIADriver(getRemoteURL(), SampleApps.intlMountainsCap(l));

      Criteria c1 = new TypeCriteria(UIATableCell.class);
      UIAElement element = driver.findElement(c1);
      element.tap();

      NameCriteria criteria = new NameCriteria("sentenceFormat", L10NStrategy.serverL10N, MatchingStrategy.regex);
      UIAElement text = driver.findElement(criteria);
      String actual = text.getName();
      Assert.assertEquals(actual, expectedContent);

      criteria = new NameCriteria("meterFormat", L10NStrategy.serverL10N, MatchingStrategy.contains);
      text = driver.findElement(criteria);
      actual = text.getName();
      Assert.assertEquals(actual, expectedContent);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

}
