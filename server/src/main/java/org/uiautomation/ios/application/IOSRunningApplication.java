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

import com.google.common.collect.ImmutableList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;

import java.util.List;

public class IOSRunningApplication {

  private final AppleLanguage currentLanguage;
  private final APPIOSApplication underlyingApplication;

  public IOSRunningApplication(AppleLanguage language, APPIOSApplication app) {
    this.underlyingApplication = app;
    this.currentLanguage = language;
  }

  public String getBundleId() {
    return underlyingApplication.getBundleId();
  }

  public boolean isSafari() {
      return "com.apple.mobilesafari".equals(getBundleId());
  }
  
  public boolean isSimulator() {
      return underlyingApplication.isSimulator();
  }

  public String getDotAppAbsolutePath() {
    return underlyingApplication.getApplicationPath().getAbsolutePath();
  }

  // TODO will have to be synchronized, or copy the app.
  public void setDefaultDevice(DeviceType defaultDevice, boolean putDefaultFirst) {
    underlyingApplication.setDefaultDevice(defaultDevice, putDefaultFirst);
  }
  
  public void setSafariBuiltinFavorites() {
    underlyingApplication.setSafariBuiltinFavorites();
  }

  public AppleLanguage getCurrentLanguage() {
    return currentLanguage;
  }

  public String applyL10N(String locator){
    LocatorWithL10N l10n =  new LocatorWithL10N(this);
    return l10n.translate(locator);
  }

  public String applyL10NOnKey(String key){
    LocatorWithL10N l10n =  new LocatorWithL10N(this);
    return l10n.translateKey(key);
  }

  private ImmutableList<ContentResult> getPotentialMatches(String name) throws WebDriverException {
    if (underlyingApplication.getSupportedLanguages().contains(currentLanguage)) {
      return underlyingApplication.getDictionary(currentLanguage).getPotentialMatches(name);
    } else {
      return ImmutableList.of(new ContentResult(currentLanguage, name, name, name));
    }
  }

  public JSONObject getTranslations(String name) throws JSONException {
    JSONObject l10n = new JSONObject();
    l10n.put("matches", 0);
    if (name != null && !name.isEmpty() && !"null".equals(name)) {
      try {
        ImmutableList<ContentResult> results = getPotentialMatches(name);

        int size = results.size();
        if (size != 0) {
          l10n.put("matches", size);
          JSONArray keys = new JSONArray();
          for (ContentResult res : results) {
            keys.put(res.getKey());
          }
          l10n.put("key", keys);
        }

        JSONArray langs = new JSONArray();
        for (AppleLanguage language : underlyingApplication.getSupportedLanguages()) {
          JSONArray possibleMatches = new JSONArray();

          for (ContentResult res : results) {
            possibleMatches.put(underlyingApplication.translate(res, language));
          }
          JSONObject match = new JSONObject();
          match.put(language.toString(), possibleMatches);
          langs.put(match);

        }
        l10n.put("langs", langs);

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return l10n;
  }

  public LanguageDictionary getCurrentDictionary() {
    return underlyingApplication.getDictionary(currentLanguage);
  }

  public LanguageDictionary getDictionary(String lang) {
    return underlyingApplication.getDictionary(lang);
  }

  public List<String> getSupportedLanguagesCodes() {
    return underlyingApplication.getSupportedLanguagesCodes();
  }

  public IOSCapabilities getCapabilities() {
    IOSCapabilities caps = underlyingApplication.getCapabilities();
    caps.setLanguage(currentLanguage.getIsoCode());
    return caps;
  }

  public String translate(ContentResult contentResult, AppleLanguage loc) {
    return underlyingApplication.translate(contentResult, loc);
  }

  public APPIOSApplication getUnderlyingApplication() {
    return underlyingApplication;
  }
  
  @Override
  public String toString() {
    return underlyingApplication.toString();
  }
}
