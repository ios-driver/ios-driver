package org.uiautomation.ios.server.application;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.application.Localizable;

public class LocalizableTests {


  @Test
  public void mappingOK() throws IOSAutomationException {
    Localizable fr = Localizable.fr;
    Localizable fr2 = Localizable.createFromLegacyName("French");
    Assert.assertEquals(fr2, fr);
  }


  @Test
  public void findExisting() throws IOSAutomationException {
    Assert.assertTrue(Localizable.isLegacyName("French"));
    Assert.assertFalse(Localizable.isLegacyName("Klingon"));

  }

  @Test(expectedExceptions = {IOSAutomationException.class})
  public void doesntExistThrowsException() throws IOSAutomationException {
    Localizable kg = Localizable.createFromLegacyName("Klingon");
    Assert.assertNotNull(kg);
  }

}
