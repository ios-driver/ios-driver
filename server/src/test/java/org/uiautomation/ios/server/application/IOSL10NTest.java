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

import java.util.List;

import com.google.common.collect.ImmutableList;
import org.apache.http.annotation.Immutable;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.SampleApps;

public class IOSL10NTest {

  private IOSRunningApplication app;

  @BeforeClass
  public void setup() throws Exception {

    APPIOSApplication arch = new APPIOSApplication(SampleApps.getIntlMountainsFile());
    app = arch.createInstance(AppleLanguage.en);
  }

  @Test
  public void noVariable() {
    String content = "Mountain Detail";

    ImmutableList<ContentResult> results = app.getCurrentDictionary().getPotentialMatches(content);

    Assert.assertEquals(results.size(), 1);

    ContentResult res = results.get(0);
    Assert.assertEquals(res.getArgs().size(), 0);

    String french = app.translate(res, AppleLanguage.fr);
    Assert.assertEquals(french, "Détail de l'apostrophe \" Montagne");

  }

  @Test
  public void matchesPatternStart() {
    // %@ items found in %@
    String content = "42 feet";
    ImmutableList<ContentResult> results = app.getCurrentDictionary().getPotentialMatches(content);
    Assert.assertEquals(results.size(), 1);
    ContentResult res = results.get(0);
    Assert.assertEquals(res.getArgs().size(), 1);
    Assert.assertEquals(res.getArgs().get(0), "42");

    String french = app.getDictionary("fr").translate(res);
    Assert.assertEquals(french, "42 pieds");

  }

  @Test
  public void notFound() {
    String content = "ferrets are great pets";
    ImmutableList<ContentResult> results = app.getDictionary("en").getPotentialMatches(content);
    Assert.assertEquals(results.size(), 0);
  }

  @Test(expectedExceptions = {WebDriverException.class})
  public void languageNotFound() {
    String content = "42 feet";
    ImmutableList<ContentResult> results = app.getDictionary("en").getPotentialMatches(content);
    Assert.assertEquals(results.size(), 1);

    app.translate(results.get(0), AppleLanguage.es);
  }

  @Test
  public void complex() {
    // %@ items found in %@
    String content = "Mountain 1 was first climbed on 29 May 1953 and has a height of 8,848 meters";
    ImmutableList<ContentResult> results = app.getDictionary("en").getPotentialMatches(content);
    Assert.assertEquals(results.size(), 1);
    ContentResult res = results.get(0);
    String french = app.getDictionary("fr").translate(res);
    // returns
    // "Bien que Mountain 1 de haut, 29 May 1953 aient été montés la première fois 8,848 meters."
  }

}
