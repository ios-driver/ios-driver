package org.uiautomation.ios.server.application;

import java.io.IOException;

import org.json.JSONException;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;

public class ServerSideL10NTest {


  private ServerSideL10NFactory create(AppleLanguage lang) {
    APPIOSApplication app = new APPIOSApplication(SampleApps.getIntlMountainsFile());
    IOSRunningApplication running = app.createInstance(lang);
    ServerSideL10NFactory factory = new ServerSideL10NFactory(running);
    return factory;
  }

  @Test(expectedExceptions = WebDriverException.class)
  public void throwsProperly() throws JSONException {
    ServerSideL10NFactory factory = create(AppleLanguage.en);
    factory.nameCriteria("I don't exist");
  }


  @Test
  public void nameEN() throws JSONException {
    ServerSideL10NFactory factory = create(AppleLanguage.en);
    NameCriteria c = factory.nameCriteria("rootViewNavTitle");
    Assert.assertEquals(c.getValue(), "Mountains");
  }

  @Test
  public void nameFR() throws JSONException {
    ServerSideL10NFactory factory = create(AppleLanguage.fr);
    NameCriteria c = factory.nameCriteria("rootViewNavTitle");
    Assert.assertEquals(c.getValue(), "Montagnes");
  }

  @Test
  public void nameZH() throws JSONException, IOException {
    ServerSideL10NFactory factory = create(AppleLanguage.zh_Hant);
    NameCriteria c = factory.nameCriteria("rootViewNavTitle");
    Assert.assertEquals(c.getValue(), "山");
  }


  @Test
  public void nameWithParametersEN() {
    ServerSideL10NFactory factory = create(AppleLanguage.en);
    NameCriteria c = factory.nameCriteria("footFormat");
    Assert.assertEquals(c.getValue(), "(.*){1} feet");
  }

  @Test
  public void nameWithParametersFR() {
    ServerSideL10NFactory factory = create(AppleLanguage.fr);
    NameCriteria c = factory.nameCriteria("footFormat");
    Assert.assertEquals(c.getValue(), "(.*){1} pieds");
  }

  @Test
  public void nameWithParametersZH() {
    ServerSideL10NFactory factory = create(AppleLanguage.zh_Hant);
    NameCriteria c = factory.nameCriteria("footFormat");
    Assert.assertEquals(c.getValue(), "(.*){1}英尺");
  }
}
