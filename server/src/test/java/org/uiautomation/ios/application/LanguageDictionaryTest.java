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

package org.uiautomation.ios.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.SampleApps;

public class LanguageDictionaryTest {

  @Test
  public void canGuessLegacyNaming() {
    String name = "French";
    LanguageDictionary dict = new LanguageDictionary(name);
    Assert.assertEquals(dict.getLanguage().getIsoCode(), "fr");
  }

  @Test
  public void canGuessCurrentNaming() {
    String name = "fr";
    LanguageDictionary dict = new LanguageDictionary(name);
    Assert.assertEquals(dict.getLanguage().getIsoCode(), "fr");
  }

  @Test(enabled = false, expectedExceptions = {WebDriverException.class})
  public void notARecognizedLanguage() {
    String name = "Klingon";
    LanguageDictionary dict = new LanguageDictionary(name);
    Assert.assertNotNull(dict);
  }

  @Test
  public void reflectionOnProjectToFindLanguageFiles() {
    File app = new File(SampleApps.getIntlMountainsFile());
    List<File> languageFiles = LanguageDictionary.getL10NFiles(app);
    Assert.assertEquals(languageFiles.size(), 4);
  }

  @Test
  public void reflectionOnProjectToFindLanguageFiles2() {
    File app = new File(SampleApps.getUICatalogFile());
    List<File> languageFiles = LanguageDictionary.getL10NFiles(app);
    Assert.assertEquals(languageFiles.size(), 1);
  }

  @Test(dependsOnMethods = {"reflectionOnProjectToFindLanguageFiles"})
  public void reflectionOFindsTheRightLanguages() throws Exception {
    File app = new File(SampleApps.getIntlMountainsFile());
    List<File> languageFiles = LanguageDictionary.getL10NFiles(app);

    List<LanguageDictionary> dicts = new ArrayList<>();
    for (File f : languageFiles) {
      dicts.add(LanguageDictionary.createFromFile(f));
    }

    Assert.assertTrue(dicts.contains(new LanguageDictionary("en")));
    Assert.assertTrue(dicts.contains(new LanguageDictionary("fr")));
    Assert.assertTrue(dicts.contains(new LanguageDictionary("zh-Hant")));
  }

  @Test(dependsOnMethods = {"reflectionOnProjectToFindLanguageFiles2"})
  public void reflectionOFindsTheRightLanguages2() throws Exception {
    File app = new File(SampleApps.getUICatalogFile());
    List<File> languageFiles = LanguageDictionary.getL10NFiles(app);

    List<LanguageDictionary> dicts = new ArrayList<>();
    for (File f : languageFiles) {
      dicts.add(LanguageDictionary.createFromFile(f));
    }

    Assert.assertTrue(dicts.contains(new LanguageDictionary("en")));
  }

  @Test
  public void extractNames() {
    File en = new File(
        "/Users/abc/build/intlMountains/Applications/InternationalMountains.app/en.lproj/Localizable.strings");
    File fr = new File(
        "/Users/abc/build/intlMountains/Applications/InternationalMountains.app/fr.lproj/Localizable.strings");
    File zh = new File(
        "/Users/abc/build/intlMountains/Applications/InternationalMountains.app/zh-Hant.lproj/Localizable.strings");

    File french = new File(
        "/Users/abc/build/intlMountains/Applications/InternationalMountains.app/French.lproj/Localizable.strings");

    String enStr = LanguageDictionary.extractLanguageName(en);
    String frStr = LanguageDictionary.extractLanguageName(fr);
    String zhStr = LanguageDictionary.extractLanguageName(zh);
    String frenchStr = LanguageDictionary.extractLanguageName(french);

    Assert.assertEquals(enStr, "en");
    Assert.assertEquals(frStr, "fr");
    Assert.assertEquals(zhStr, "zh-Hant");
    Assert.assertEquals(frenchStr, "French");
  }

  @Test
  public void getRegexPattern() {
    Assert.assertEquals(LanguageDictionary.getRegexPattern(
        "%@"),
        "(.*){1}");
    Assert.assertEquals(LanguageDictionary.getRegexPattern(
        "\\%@"),
        "\\%@");
    Assert.assertEquals(LanguageDictionary.getRegexPattern(
        "[](){}.?*+"),
        "\\[\\]\\(\\)\\{\\}\\.\\?\\*\\+");
    Assert.assertEquals(LanguageDictionary.getRegexPattern(
        "% %%"),
        "% %%");
    Assert.assertEquals(LanguageDictionary.getRegexPattern(
        "(%@ %1$@ %23$@ %d %@ %1$@ %54$@ %d % %%)."),
        "\\((.*){1} (.*){1} (.*){1} (.*){1} (.*){1} (.*){1} (.*){1} (.*){1} % %%\\)\\.");
  }
}
