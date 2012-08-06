package org.uiautomation.ios.e2e.uicatalogapp;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;

public class TimeoutTests extends UICatalogTestsBase {


  @Test
  public void getSetTimeout() throws InterruptedException {
    RemoteUIADriver driver = null;
    try {

      driver = getDriver();
      Assert.assertEquals(driver.getLocalTarget().getTimeout(), 5);
      driver.getLocalTarget().setTimeout(6);
      Assert.assertEquals(driver.getLocalTarget().getTimeout(), 6);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
}
