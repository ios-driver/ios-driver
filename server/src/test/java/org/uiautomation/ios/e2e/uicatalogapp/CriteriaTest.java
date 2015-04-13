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

package org.uiautomation.ios.e2e.uicatalogapp;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.LocationCriteria;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.PropertyEqualCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;

public class CriteriaTest extends BaseIOSDriverTest {

  @BeforeClass
  public void startDriver() throws InterruptedException {
    driver = getDriver(SampleApps.uiCatalogCap());
    driver.findElement(By.xpath("//UIAButton[@name='UICatalog']")).click();
  }

  private static final String buttonName = "Buttons";

  @Test
  public void exactMatch() {
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    PropertyEqualCriteria c2 = new NameCriteria(buttonName);
    Assert.assertEquals(c2.getMatchingStrategy(), MatchingStrategy.exact);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = driver.findElement(c);
    Assert.assertEquals(element.getName(), buttonName);
  }

  @Test
  public void regexMatch() throws InterruptedException {
    String regex = "But[a-z]*";
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    PropertyEqualCriteria c2 = new NameCriteria(regex, MatchingStrategy.regex);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = driver.findElement(c);
    Assert.assertEquals(element.getName(), buttonName);
  }

  @Test
  public void positionMatch() {
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    PropertyEqualCriteria c2 = new NameCriteria(buttonName);
    Assert.assertEquals(c2.getMatchingStrategy(), MatchingStrategy.exact);
    Criteria c = new AndCriteria(c1, c2);
    UIAElement element = driver.findElement(c);

    UIARect position = element.getRect();

    //center
    Criteria
        test =
        new LocationCriteria(position.getX() + (position.getWidth() / 2),
                             position.getY() + (position.getHeight() / 2));
    UIAElement res = driver.findElement(test);
    Assert.assertEquals(res.getName(), buttonName);

    // bottom right corner
    int x = position.getX() + position.getWidth() - 1;
    int y = position.getY() + position.getHeight() - 1;
    test = new LocationCriteria(x, y);
    res = driver.findElement(test);
    Assert.assertEquals(res.getName(), buttonName);

    // middle
    x = position.getX() + (position.getWidth() / 2);
    y = position.getY() + (position.getHeight() / 2);
    test = new LocationCriteria(x, y);
    res = driver.findElement(test);
    Assert.assertEquals(res.getName(), buttonName);
  }

}
