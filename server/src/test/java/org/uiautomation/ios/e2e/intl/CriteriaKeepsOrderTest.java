package org.uiautomation.ios.e2e.intl;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.OrCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;

public class CriteriaKeepsOrderTest extends BaseIOSDriverTest {

  private RemoteUIADriver driver;

  @BeforeClass
  public void startDriver() {
    driver = new RemoteUIADriver(getRemoteURL(), SampleApps.intlMountainsCap("en"));
  }

  @AfterClass
  public void stopDriver() {
    if (driver != null) {
      driver.quit();
    }
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
    UIAElement result3 = driver.findElement(new OrCriteria(new OrCriteria(mountain3, mountain2), mountain1));

    Assert.assertEquals(result1.getName(), "Mountain 1");
    Assert.assertEquals(result3.getName(), "Mountain 3");
  }
}
