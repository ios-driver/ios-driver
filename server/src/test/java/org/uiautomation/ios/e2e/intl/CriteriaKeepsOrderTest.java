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

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.OrCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;

public class CriteriaKeepsOrderTest extends BaseIOSDriverTest {

  @BeforeClass
  public void startDriver() {
    driver = new RemoteIOSDriver(getRemoteURL(), SampleApps.intlMountainsCap("en"));
  }

  @Test
  public void orCriteriaReturnsFirstMatch() {
    Criteria mountain1 = new NameCriteria("Mountain 1");
    Criteria mountain2 = new NameCriteria("Mountain 2");

    UIAElement result1 = driver.findElement(new OrCriteria(mountain1, mountain2));
    UIAElement result2 = driver.findElement(new OrCriteria(mountain2, mountain1));

    Assert.assertEquals(result1.getName(), "Mountain 1");
    Assert.assertEquals(result2.getName(), "Mountain 2");
  }

  @Test
  public void complexOrCriteriaReturnsFirstMatch() {
    Criteria mountain1 = new NameCriteria("Mountain 1");
    Criteria mountain2 = new NameCriteria("Mountain 2");
    Criteria mountain3 = new NameCriteria("Mountain 3");

    UIAElement result1 = driver.findElement(new OrCriteria(mountain1, mountain2, mountain3));
    UIAElement
        result3 =
        driver.findElement(new OrCriteria(new OrCriteria(mountain3, mountain2), mountain1));

    Assert.assertEquals(result1.getName(), "Mountain 1");
    Assert.assertEquals(result3.getName(), "Mountain 3");
  }
}
