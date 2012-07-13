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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.exceptions.IOSAutomationException;

public class IOSApplication {

  private final File app;
  private final Localizable currentLanguage;
  private final List<LanguageDictionary> dictionaries = new ArrayList<LanguageDictionary>();


  /**
   * 
   * @param currentLanguage
   * @param pathToApp
   * @throws IOSAutomationException
   */
  public IOSApplication(Localizable currentLanguage, String pathToApp)
      throws IOSAutomationException {
    this.app = new File(pathToApp);
    if (!app.exists()) {
      throw new IOSAutomationException(pathToApp + "isn't an IOS app.");
    }
    this.currentLanguage = currentLanguage;
  }


  /**
   * get the list of languages the application if localized to.
   * 
   * @return
   * @throws Exception
   */
  public List<Localizable> getSupportedLanguages() throws Exception {
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
}
