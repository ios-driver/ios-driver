package org.uiautomation.ios.server.application;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;

import java.util.List;

public class IOSRunningApplication {

  private final AppleLocale currentLanguage;
  private final APPIOSApplication underlyingApplication;
  private String bundleId;
  private String dotAppAbsolutePath;
  private DeviceType defaultDevice;

  public IOSRunningApplication(String language, APPIOSApplication app) {
    this.underlyingApplication = app;
    this.currentLanguage = setLanguage(language);
  }

  public String getBundleId() {
    return underlyingApplication.getBundleId();
  }

  public AppleLocale getAppleLocaleFromLanguageCode(String language) {
    return underlyingApplication.getAppleLocaleFromLanguageCode(language);
  }

  public String getDotAppAbsolutePath() {
    return underlyingApplication.getApplicationPath().getAbsolutePath();
  }

  // TODO will have to be syncronized, or copy the app.
  public void setDefaultDevice(DeviceType defaultDevice) {
    underlyingApplication.setDefaultDevice(defaultDevice);
  }


  public AppleLocale getCurrentLanguage() {
    return currentLanguage;
  }

  public AppleLocale setLanguage(String lang) {
    if (underlyingApplication.getSupportedLanguages().isEmpty()) {
      return AppleLocale.emptyLocale(lang);

    }
    for (AppleLocale loc : underlyingApplication.getSupportedLanguages()) {
      if (loc.getLocale().toString().equals(lang)) {
        return loc;
      }
    }
    throw new WebDriverException(
        "Cannot find " + lang + " in the supported languages for the app.");
  }


  private List<ContentResult> getPotentialMatches(String name) throws WebDriverException {
    LanguageDictionary dict = underlyingApplication.getDictionary(currentLanguage);
    List<ContentResult> res = dict.getPotentialMatches(name);
    return res;
  }  // TODO freynaud return a Map

  public JSONObject getTranslations(String name) throws JSONException {

    JSONObject l10n = new JSONObject();
    l10n.put("matches", 0);
    if (name != null && !name.isEmpty() && !"null".equals(name)) {
      try {
        List<ContentResult> results = getPotentialMatches(name);

        int size = results.size();
        if (size != 0) {
          l10n.put("matches", size);
          l10n.put("key", results.get(0).getKey());
        }

        JSONArray langs = new JSONArray();
        for (AppleLocale language : underlyingApplication.getSupportedLanguages()) {
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
    caps.setLanguage(currentLanguage.getAppleLanguagesForPreferencePlist());
    return caps;
  }

  public String translate(ContentResult contentResult, AppleLocale loc) {
    return underlyingApplication.translate(contentResult, loc);
  }
}
