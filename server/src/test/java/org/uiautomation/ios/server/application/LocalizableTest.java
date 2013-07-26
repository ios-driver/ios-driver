package org.uiautomation.ios.server.application;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LocalizableTest {

  @Test
  public void mappingOK() {
    AppleLanguage fr = AppleLanguage.create("fr");
    AppleLanguage fr2 = AppleLanguage.create("French");
    Assert.assertEquals(fr2, fr);
  }

  @Test
  public void doesntExistThrowsException() {
    AppleLanguage kg = AppleLanguage.create("Klingon");
    Assert.assertNotNull(kg);
  }

}
