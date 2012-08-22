package org.uiautomation.ios.server.application;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.exceptions.InvalidCriteriaException;
import org.uiautomation.ios.server.tests.SampleApps;

public class ServerSideL10N {



  private ServerSideL10NFactory create(Localizable l) {
    IOSApplication app = new IOSApplication(l, SampleApps.getIntlMountainsApp());
    app.loadAllContent();
    ServerSideL10NFactory factory = new ServerSideL10NFactory(app);
    return factory;
  }

  @Test(expectedExceptions = InvalidCriteriaException.class)
  public void throwsProperly() throws JSONException {
    ServerSideL10NFactory factory = create(Localizable.en);
    factory.nameCriteria("I don't exist");
  }


  @Test
  public void nameEN() throws JSONException {
    ServerSideL10NFactory factory = create(Localizable.en);
    NameCriteria c = factory.nameCriteria("rootViewNavTitle");
    Assert.assertEquals(c.getValue(), "Mountains");
    System.out.println(c.getJSONRepresentation().toString());
  }

  @Test
  public void nameFR() throws JSONException {
    ServerSideL10NFactory factory = create(Localizable.fr);
    NameCriteria c = factory.nameCriteria("rootViewNavTitle");
    Assert.assertEquals(c.getValue(), "Montagnes");
    System.out.println(c.getJSONRepresentation().toString());
  }

  @Test
  public void nameZH() throws JSONException, IOException {
    ServerSideL10NFactory factory = create(Localizable.zh);
    NameCriteria c = factory.nameCriteria("rootViewNavTitle");
    Assert.assertEquals(c.getValue(), "山");
  }


  @Test
  public void nameWithParametersEN() {
    ServerSideL10NFactory factory = create(Localizable.en);
    NameCriteria c = factory.nameCriteria("footFormat");
    Assert.assertEquals(c.getValue(), "(.*){1} feet");
  }

  @Test
  public void nameWithParametersFR() {
    ServerSideL10NFactory factory = create(Localizable.fr);
    NameCriteria c = factory.nameCriteria("footFormat");
    Assert.assertEquals(c.getValue(), "(.*){1} pieds");
  }

  @Test
  public void nameWithParametersZH() {
    ServerSideL10NFactory factory = create(Localizable.zh);
    NameCriteria c = factory.nameCriteria("footFormat");
    Assert.assertEquals(c.getValue(), "(.*){1}英尺");
  }
}
