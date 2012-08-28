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


import static org.uiautomation.ios.IOSCapabilities.BUNDLE_NAME;
import static org.uiautomation.ios.IOSCapabilities.BUNDLE_VERSION;
import static org.uiautomation.ios.IOSCapabilities.DEVICE_FAMILLY;
import static org.uiautomation.ios.IOSCapabilities.ICON;
import static org.uiautomation.ios.IOSCapabilities.MAGIC_PREFIX;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.utils.Command;
import org.uiautomation.ios.server.utils.PlistFileUtils;


// TODO freynaud create IOSApp vs Running App that has locale + language
public class IOSApplication {



  private final JSONObject metadata;
  private final File app;
  private Localizable currentLanguage;
  private final List<LanguageDictionary> dictionaries = new ArrayList<LanguageDictionary>();


  /**
   * 
   * @param currentLanguage
   * @param pathToApp
   * @throws IOSAutomationException
   */
  public IOSApplication(String pathToApp) throws IOSAutomationException {
    this.app = new File(pathToApp);
    if (!app.exists()) {
      throw new IOSAutomationException(pathToApp + "isn't an IOS app.");
    }
    loadAllContent();
    try {
      metadata = getFullPlist();
    } catch (Exception e) {
      throw new IOSAutomationException("cannot load the metadata from the Info.plist file for "
          + pathToApp);
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


  /**
   * get the list of languages the application if localized to.
   * 
   * @return
   * @throws Exception
   */
  public List<Localizable> getSupportedLanguages() {
    Set<Localizable> res = new HashSet<Localizable>();
    List<File> l10ns = LanguageDictionary.getL10NFiles(app);
    for (File f : l10ns) {
      String name = LanguageDictionary.extractLanguageName(f);
      res.add(new LanguageDictionary(name).getLanguage());
    }
    return new ArrayList<Localizable>(res);
  }



  public LanguageDictionary getDictionary(Localizable language) throws IOSAutomationException {
    for (LanguageDictionary dict : dictionaries) {
      if (dict.getLanguage() == language) {
        return dict;
      }
    }
    throw new IOSAutomationException("Cannot find dictionary for " + language);
  }



  /**
   * Load all the dictionaries for the application.
   * 
   * @throws Exception
   */
  public void loadAllContent() throws IOSAutomationException {
    if (!dictionaries.isEmpty()) {
      throw new IOSAutomationException("Content already present.");
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
        throw new IOSAutomationException("error loading content for l10n", e);
      }


    }
    dictionaries.addAll(dicts.values());

  }



  public String translate(ContentResult res, Localizable language) throws IOSAutomationException {
    LanguageDictionary destinationLanguage = getDictionary(language);
    return destinationLanguage.translate(res);

  }

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
        for (Localizable language : getSupportedLanguages()) {
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

  // TODO freynaud return a single resutl and throw when multiple are found would simplify
  // everything
  private List<ContentResult> getPotentialMatches(String name) throws IOSAutomationException {
    LanguageDictionary dict = getDictionary(currentLanguage);
    List<ContentResult> res = dict.getPotentialMatches(name);
    return res;
  }


  public void addDictionary(LanguageDictionary dict) {
    dictionaries.add(dict);
  }


  public Localizable getCurrentLanguage() {
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
      throw new IOSAutomationException("no property " + key + " for this app.");
    }
    try {
      return metadata.getString(key);
    } catch (JSONException e) {
      throw new IOSAutomationException("property " + key + " can't be returned. " + e.getMessage(),
          e);
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
      throw new IOSAutomationException("Cannot load device family", e);
    }
  }


  public void setLanguage(String language) {
    currentLanguage = Localizable.getEnum(language);
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
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    IOSApplication other = (IOSApplication) obj;
    if (app == null) {
      if (other.app != null) return false;
    } else if (!app.equals(other.app)) return false;
    return true;
  }
}
