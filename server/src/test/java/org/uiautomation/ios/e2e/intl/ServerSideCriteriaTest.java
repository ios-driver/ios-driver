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
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAApplication;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.L10NStrategy;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;

public class ServerSideCriteriaTest extends BaseIOSDriverTest {

  private RemoteIOSDriver driver;

  @BeforeClass
  public void startDriver() {

    driver = new RemoteIOSDriver(getRemoteURL(), SampleApps.intlMountainsCap("fr"));
  }

  @AfterClass
  public void stopDriver() {
    if (driver != null) {
      driver.quit();
    }
  }

  private
  String
      expected =
      "Bien que 8,848 mètres de haut, Montagne 1 aient été montés la première fois 29 May 1953.";

  @Test
  public void findElementDriver() {
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    UIAElement element = driver.findElement(c1);
    element.tap();

    NameCriteria
        criteria =
        new NameCriteria("sentenceFormat", L10NStrategy.serverL10N, MatchingStrategy.regex);
    UIAElement text = driver.findElement(criteria);
    String actual = text.getName();
    Assert.assertEquals(actual, expected);

  }

  @Test(dependsOnMethods = {"findElementDriver"})
  public void findElementElement() {

    UIAApplication app = (UIAApplication) driver.findElement(By.tagName("UIAApplication"));

    NameCriteria
        criteria =
        new NameCriteria("sentenceFormat", L10NStrategy.serverL10N, MatchingStrategy.regex);
    UIAElement text = app.findElement(criteria);
    String actual = text.getName();
    Assert.assertEquals(actual, expected);

  }

}
