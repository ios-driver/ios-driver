package org.uiautomation.ios.e2e.uicatalogapp;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAWindow;

public class SmokeTests extends UICatalogTestsBase {

  @Test
  public void canGetMainWindow() throws Exception {
    RemoteUIADriver driver = null;
    try {

      driver = getDriver();
      RemoteUIAWindow win = getMainWindow(driver);

      UIARect size = win.getRect();
      Assert.assertEquals(size.getX(), 0);
      Assert.assertEquals(size.getY(), 0);
      Assert.assertEquals(size.getHeight(), 480);
      Assert.assertEquals(size.getWidth(), 320);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

}
