/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.uiautomation.ios.server.application;

import org.json.JSONException;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;

import java.io.IOException;

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

  @Test(enabled = false)
  public void apostrophe() throws JSONException {
    ServerSideL10NFactory factory = create(AppleLanguage.fr);
    NameCriteria c = factory.nameCriteria("detailViewNavTitle");
    Assert.assertEquals(c.getValue(), "Détail de l'apostrophe Montagne");
  }

  @Test
  public void normalStringDoNotRequireConcat() {
    String s = "ABC";
    String res = LocatorWithL10N.escapeXPath(s);
    Assert.assertEquals(res, "'"+s+"'");
  }

  @Test
  public void useDoubleQuoteIfStringContainsSingleQuote() {
    String s = "ABC's";
    String res = LocatorWithL10N.escapeXPath(s);
    Assert.assertEquals(res, "\"" + s + "\"");
  }

  @Test
  public void useSingleQuoteIfStringContainsDouble() {
    String s = "ABC\"D";
    String res = LocatorWithL10N.escapeXPath(s);
    Assert.assertEquals(res, "'" + s + "'");

  }

  @Test
  public void concat() {
    String base = "I'm using \"that\"";
    String res = LocatorWithL10N.escapeXPath(base);
    Assert.assertEquals(res, "concat('I',\"'\",'m using ','\"','that','\"')");

  }

}
