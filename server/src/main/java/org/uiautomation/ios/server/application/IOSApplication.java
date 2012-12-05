/*
 * Copyright 2012 ios-driver committers.
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

import static org.uiautomation.ios.IOSCapabilities.DEVICE_FAMILLY;
import static org.uiautomation.ios.IOSCapabilities.ICON;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.communication.device.Device;
import org.uiautomation.ios.server.utils.PlistFileUtils;

import com.dd.plist.BinaryPropertyListParser;
import com.dd.plist.BinaryPropertyListWriter;
import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;

// TODO freynaud create IOSApp vs Running App that has locale + language
public class IOSApplication {

  private final JSONObject metadata;
  private final File app;
  private AppleLocale currentLanguage;
  private final List<LanguageDictionary> dictionaries = new ArrayList<LanguageDictionary>();

  /**
   * 
   * @param currentLanguage
   * @param pathToApp
   * @throws WebDriverException
   */
  public IOSApplication(String pathToApp) {
    this.app = new File(pathToApp);
    if (!app.exists()) {
      throw new WebDriverException(pathToApp + "isn't an IOS app.");
    }
    loadAllContent();
    try {
      metadata = getFullPlist();
    } catch (Exception e) {
      throw new WebDriverException("cannot load the metadata from the Info.plist file for " + pathToApp);
    }
  }

  /**
   * the content of the Info.plist for the app, as a json object.
   * 
   * @return
   */
  public JSONObject getMetadata() {
    return metadata;
  }

  public List<String> getSupportedLanguagesCodes() {
    List<AppleLocale> list = getSupportedLanguages();
    List<String> res = new ArrayList<String>();
    for (AppleLocale app : list) {
      res.add(app.getLocale().toString());
    }
    return res;
  }

  /**
   * get the list of languages the application if localized to.
   * 
   * @return
   * @throws Exception
   */
  private List<AppleLocale> getSupportedLanguages() {
    if (dictionaries.isEmpty()) {
      loadAllContent();
    }
    /*
     * Set<Localizable> res = new HashSet<Localizable>(); List<File> l10ns =
     * LanguageDictionary.getL10NFiles(app); for (File f : l10ns) { String name
     * = LanguageDictionary.extractLanguageName(f); res.add(new
     * LanguageDictionary(name).getLanguage()); } return new
     * ArrayList<Localizable>(res);
     */
    List<AppleLocale> res = new ArrayList<AppleLocale>();
    for (LanguageDictionary dict : dictionaries) {
      res.add(dict.getLanguage());
    }
    return res;
  }

  public AppleLocale getAppleLocaleFromLanguageCode(String languageCode) {
    for (AppleLocale loc : getSupportedLanguages()) {
      if (languageCode.equals(loc.getLocale().getLanguage())) {
        return loc;
      }
    }
    throw new WebDriverException("Cannot find AppleLocale for " + languageCode);
  }

  public LanguageDictionary getDictionary(String languageCode) throws WebDriverException {
    return getDictionary(getAppleLocaleFromLanguageCode(languageCode));

  }

  public LanguageDictionary getDictionary(AppleLocale language) throws WebDriverException {
    for (LanguageDictionary dict : dictionaries) {
      if (dict.getLanguage() == language) {
        return dict;
      }
    }
    throw new WebDriverException("Cannot find dictionary for " + language);

  }

  /**
   * Load all the dictionaries for the application.
   * 
   * @throws Exception
   */
  private void loadAllContent() throws WebDriverException {
    if (!dictionaries.isEmpty()) {
      throw new WebDriverException("Content already present.");
    }
    Map<String, LanguageDictionary> dicts = new HashMap<String, LanguageDictionary>();

    List<File> l10nFiles = LanguageDictionary.getL10NFiles(app);
    for (File f : l10nFiles) {
      String name = LanguageDictionary.extractLanguageName(f);
      LanguageDictionary res = dicts.get(name);
      if (res == null) {
        res = new LanguageDictionary(name);
        dicts.put(name, res);
      }
      try {
        // and load the content.
        JSONObject content = res.readContentFromBinaryFile(f);
        res.addJSONContent(content);
      } catch (Exception e) {
        throw new WebDriverException("error loading content for l10n", e);
      }

    }
    dictionaries.addAll(dicts.values());

  }

  public String translate(ContentResult res, AppleLocale language) throws WebDriverException {
    LanguageDictionary destinationLanguage = getDictionary(language);
    return destinationLanguage.translate(res);

  }

  // TODO freynaud return a Map
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
        for (AppleLocale language : getSupportedLanguages()) {
          JSONArray possibleMatches = new JSONArray();

          for (ContentResult res : results) {
            possibleMatches.put(translate(res, language));
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

  // TODO freynaud return a single resutl and throw when multiple are found
  // would simplify
  // everything
  private List<ContentResult> getPotentialMatches(String name) throws WebDriverException {
    LanguageDictionary dict = getDictionary(currentLanguage);
    List<ContentResult> res = dict.getPotentialMatches(name);
    return res;
  }

  public void addDictionary(LanguageDictionary dict) {
    dictionaries.add(dict);
  }

  public AppleLocale getCurrentLanguage() {
    return currentLanguage;
  }

  public File getApplicationPath() {
    return app;
  }

  /**
   * the list of resources to publish via http.
   * 
   * @return
   */
  public Map<String, String> getResources() {
    Map<String, String> resourceByResourceName = new HashMap<String, String>();
    resourceByResourceName.put(ICON, getMetadata(ICON));
    return resourceByResourceName;
  }

  private JSONObject getFullPlist() throws Exception {
    File plist = new File(app, "Info.plist");
    PlistFileUtils util = new PlistFileUtils(plist);
    return util.toJSON();
  }

  public String getMetadata(String key) {
    if (!metadata.has(key)) {
      return "";
      // throw new WebDriverException("no property " + key +
      // " for this app.");
    }
    try {
      return metadata.getString(key);
    } catch (JSONException e) {
      throw new WebDriverException("property " + key + " can't be returned. " + e.getMessage(), e);
    }
  }

  public List<Integer> getDeviceFamily() {
    try {
      JSONArray array = metadata.getJSONArray(DEVICE_FAMILLY);
      List<Integer> res = new ArrayList<Integer>();
      for (int i = 0; i < array.length(); i++) {
        res.add(array.getInt(i));
      }
      return res;
    } catch (JSONException e) {
      throw new WebDriverException("Cannot load device family", e);
    }
  }

  public void setLanguage(String lang) {
    for (AppleLocale loc : getSupportedLanguages()) {
      if (loc.getLocale().toString().equals(lang)) {
        currentLanguage = loc;
        return;
      }
    }
    throw new WebDriverException("Cannot find " + lang + " in the supported languages for the app.");
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((app == null) ? 0 : app.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    IOSApplication other = (IOSApplication) obj;
    if (app == null) {
      if (other.app != null)
        return false;
    } else if (!app.equals(other.app))
      return false;
    return true;
  }

  public static IOSApplication findSafariLocation(File xcodeInstall, String sdkVersion) {
    File app = new File(xcodeInstall,
        "/Contents/Developer/Platforms/iPhoneSimulator.platform/Developer/SDKs/iPhoneSimulator" + sdkVersion
            + ".sdk/Applications/MobileSafari.app");
    if (!app.exists()) {
      throw new WebDriverException(app + " should be the safari app, but doesn't exist.");
    }
    return new IOSApplication(app.getAbsolutePath());
  }

  public void setDefaultDevice(Device device) {

    try {
      File plist = new File(app, "Info.plist");
      NSDictionary root = (NSDictionary) BinaryPropertyListParser.parse(new FileInputStream(plist));

      NSArray devices = (NSArray) root.objectForKey("UIDeviceFamily");
      int length = devices.getArray().length;
      if (length == 1) {
        return;
      }

      NSArray rearrangedArray = new NSArray(length);
      NSNumber last = null;
      int index = 0;
      for (int i = 0; i < length; i++) {
        NSNumber d = (NSNumber) devices.objectAtIndex(i);
        if (d.intValue() == device.getDeviceFamily()) {
          last = d;
        } else {
          rearrangedArray.setValue(index, d);
          index++;
        }
      }
      if (last == null) {
        throw new WebDriverException("Cannot find device " + device + " in the supported device list.");
      }
      rearrangedArray.setValue(index, last);
      root.put("UIDeviceFamily", rearrangedArray);
      BinaryPropertyListWriter.write(plist, root);
    } catch (Exception e) {
      throw new WebDriverException("Cannot change the default device for the app." + e.getMessage(), e);
    }

  }
}
