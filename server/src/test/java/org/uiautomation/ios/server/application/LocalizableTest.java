package org.uiautomation.ios.server.application;

import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LocalizableTest {

  @Test
  public void mappingOK() {
    Localizable fr = Localizable.fr;
    Localizable fr2 = Localizable.createFromLegacyName("French");
    Assert.assertEquals(fr2, fr);
  }

  @Test
  public void findExisting() {
    Assert.assertTrue(Localizable.isLegacyName("French"));
    Assert.assertFalse(Localizable.isLegacyName("Klingon"));

  }

  @Test(expectedExceptions = { WebDriverException.class })
  public void doesntExistThrowsException() {
    Localizable kg = Localizable.createFromLegacyName("Klingon");
    Assert.assertNotNull(kg);
  }

}
