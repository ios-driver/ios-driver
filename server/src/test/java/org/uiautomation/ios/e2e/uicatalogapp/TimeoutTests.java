package org.uiautomation.ios.e2e.uicatalogapp;

import org.testng.annotations.Test;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;

public class TimeoutTests  extends UICatalogTestsBase{

  
  @Test
  public void getSetTimeout() throws InterruptedException {
    RemoteUIADriver driver = null;
    try {

      driver = getDriver();
      driver.getLocalTarget().setTimeout(6);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
}
