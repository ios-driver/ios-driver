/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.uiautomation.ios.e2e.intl;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.*;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.L10NStrategy;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;

public class ComplexCriteriaTest extends BaseIOSDriverTest {
    
  @AfterMethod(alwaysRun = true)
  public void afterMethod() throws Exception {
    stopDriver();
  }

  @DataProvider(name = "intlMountain", parallel = false)
  public Object[][] createDataPerLocale() {
  return new Object[][]{
    {"en", "Mountain 1 was first climbed on 29 May 1953 and has a height of 8,848 meters"},
    {"fr",
     "Bien que 8,848 mètres de haut, Montagne 1 aient été montés la première fois 29 May 1953."},
    {"zh-Hant", "山 1 是8,848米高。它第一次攀登了在29 May 1953。"}
  };
  }

  @Test(dataProvider = "intlMountain")
  public void selectAndValidateServerSideL10NedContent(String lang, String expectedContent) {
    driver = new RemoteIOSDriver(getRemoteURL(), SampleApps.intlMountainsCap(lang));

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
  }

  @Test
  public void apostrophe() {
    driver = new RemoteIOSDriver(getRemoteURL(), SampleApps.intlMountainsCap("fr"));

    Criteria c1 = new TypeCriteria(UIATableCell.class);
    UIAElement element = driver.findElement(c1);
    element.tap();

    // and using Xpath
    WebElement
      el =
      driver.findElement(By.xpath("//*[matches(@name,l10n('detailViewNavTitle'))]"));
  }
}
