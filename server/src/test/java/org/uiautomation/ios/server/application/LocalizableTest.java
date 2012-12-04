package org.uiautomation.ios.server.application;

import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LocalizableTest {

  @Test
  public void mappingOK() {
    AppleLocale fr = new AppleLocale("fr");
    AppleLocale fr2 = new AppleLocale("French");
    Assert.assertEquals(fr2, fr);
  }

  @Test(expectedExceptions = { WebDriverException.class })
  public void doesntExistThrowsException() {
    AppleLocale kg = new AppleLocale("Klingon");
    Assert.assertNotNull(kg);
  }

}
