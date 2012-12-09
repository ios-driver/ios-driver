package org.uiautomation.ios.e2e.intl;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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

public class ComplexCriteriaTest extends BaseIOSDriverTest {

  @DataProvider(name = "intlMountain", parallel = false)
  public Object[][] createDataPerLocale() {
    return new Object[][]{
        {"en", "Mountain 1 was first climbed on 29 May 1953 and has a height of 8,848 meters"},
        {"fr",
         "Bien que 8,848 mètres de haut, Montagne 1 aient été montés la première fois 29 May 1953."},
        {"zh", "山 1 是8,848米高。它第一次攀登了在29 May 1953。"}
    };
  }

  @Test(dataProvider = "intlMountain")
  public void selectAndValidateServerSideL10NedContent(String lang, String expectedContent) {
    RemoteUIADriver driver = null;
    try {

      driver = new RemoteUIADriver(getRemoteURL(), SampleApps.intlMountainsCap(lang));

      Criteria c1 = new TypeCriteria(UIATableCell.class);
      UIAElement element = driver.findElement(c1);
      element.tap();

      NameCriteria
          criteria =
          new NameCriteria("sentenceFormat", L10NStrategy.serverL10N, MatchingStrategy.regex);
      UIAElement text = driver.findElement(criteria);
      String actual = text.getName();
      Assert.assertEquals(actual, expectedContent);

      criteria =
          new NameCriteria("meterFormat", L10NStrategy.serverL10N, MatchingStrategy.contains);
      text = driver.findElement(criteria);
      actual = text.getName();
      Assert.assertEquals(actual, expectedContent);

      // and using Xpath
      WebElement el = driver.findElement(By.xpath("//*[matches(@name,l10n('meterFormat'))]"));

      Assert.assertEquals(el.getAttribute("name"), expectedContent);

      el = driver.findElement(By.xpath("//*[matches(@name,l10n('sentenceFormat'))]"));
      Assert.assertEquals(el.getAttribute("name"), expectedContent);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

}
