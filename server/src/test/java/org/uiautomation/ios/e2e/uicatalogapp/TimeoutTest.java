package org.uiautomation.ios.e2e.uicatalogapp;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;

public class TimeoutTest extends BaseIOSDriverTest {

  /*
   * @Test public void getSetTimeout() throws InterruptedException {
   * RemoteUIADriver driver = null; try {
   * 
   * driver = getDriver(); driver.manage().timeouts().
   * Assert.assertEquals(driver.getTimeout("implicit"), 0);
   * driver.setTimeout("implicit",17);
   * Assert.assertEquals(driver.getTimeout("implicit"), 17);
   * 
   * } finally { if (driver != null) { driver.quit(); } } }
   */

  @Test
  public void getElement() throws InterruptedException {
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(getRemoteURL(), SampleApps.uiCatalogCap());
      String name = "Buttons, Various uses of UIButton2";

      Criteria c1 = new TypeCriteria(UIATableCell.class);
      Criteria c2 = new NameCriteria(name);
      Criteria c = new AndCriteria(c1, c2);
      long start = System.currentTimeMillis();
      try {
        driver.findElement(c);
        Assert.fail("shouldn't find element" + name);
      } catch (NoSuchElementException e) {
        long total = System.currentTimeMillis() - start;
        Assert.assertTrue(total < 2000);
      }
      try {
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        start = System.currentTimeMillis();
        driver.findElement(c);
        Assert.fail("shouldn't find element" + name);

      } catch (NoSuchElementException e) {
        long total = System.currentTimeMillis() - start;
        Assert.assertTrue(total > 10000);
      }
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

}
