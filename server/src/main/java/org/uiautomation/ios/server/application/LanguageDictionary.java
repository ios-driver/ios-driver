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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.exceptions.IOSAutomationSetupException;
import org.uiautomation.ios.server.utils.Command;

/**
 * 
 * Represents the apple localisation of an IOS native app for a given language. In Xcode, it will be
 * Localizable.string ( theLanguage ) file.
 */
public class LanguageDictionary {

  // TODO freynaud
  // public final Form normalizer = Form.NFD;
  public static final Form norme = Form.NFKC;
  private final Localizable language;
  private final boolean legacyFormat;
  private final Map<String, String> content = new HashMap<String, String>();

  /**
   * Creates a new dictionary for the language specified. Will guess the format of the underlying
   * project structure, legacy ( with verbose name) or new.
   * 
   * @param language
   * @throws IOSAutomationException if the language isn't recognized.
   */
  public LanguageDictionary(String language) throws IOSAutomationException {
    if (Localizable.isLegacyName(language)) {
      this.language = Localizable.createFromLegacyName(language);
      this.legacyFormat = true;
    } else if (Localizable.isNewName(language)) {

      this.language = Localizable.createFromNewName(language);
      this.legacyFormat = false;
    } else {
      throw new IOSAutomationException(language + " isn't recognized.");
    }
  }

  public List<ContentResult> getPotentialMatches(String string) throws IOSAutomationException {

    List<ContentResult> res = new ArrayList<ContentResult>();
    for (String key : content.keySet()) {
      String original = content.get(key);

      boolean match = match(string, original);
      boolean tooGeneric = key.equals("%@ %d of %d") || key.equals("%@ at %@");
      if (match && !tooGeneric) {
        ContentResult r = new ContentResult(language, key, original, string);
        for (String s : r.getArgs()) {
          List<ContentResult> rec = getPotentialMatches(s);
          if (!rec.isEmpty()) {
            // TODO freynaud an argument can be l10ned too....
            // System.out.println("recursion is found..." + getPotentialMatches(s));
          }
        }
        res.add(r);
      }
    }

    // if 2 strings are
    // 1) %@ meters
    // and 2) %@ climbed mountain %@ that is %@ meters,
    // the real match is 2.1) is just noise from another sentence.
    if (res.size() > 1) {
      int maxVariable = 0;
      for (ContentResult r : res) {
        if (r.getArgs().size() > maxVariable) {
          maxVariable = r.getArgs().size();
        }
      }
      List<ContentResult> res2 = new ArrayList<ContentResult>();
      for (ContentResult r : res) {
        if (r.getArgs().size() == maxVariable) {
          res2.add(r);
        }
      }
      return res2;
    }

    return res;
  }

  public boolean match(String content, String originalText) {
    String normalizedContent = Normalizer.normalize(content, norme);
    String normalizedOriginalText = Normalizer.normalize(originalText, norme);


    String pattern = getRegexPattern(normalizedOriginalText);
    try {
      boolean regex = normalizedContent.matches(pattern);
      return regex;
    } catch (PatternSyntaxException e) {
      // TODO freynaud debug that
    }
    return false;
  }

  // "Shipping from: %@": "Versand ab: %@",
  public static String getRegexPattern(String original) {
    String res = original.replace("%@", "(.*){1}");
    res = res.replaceAll("%1\\$@", "(.*){1}");
    res = res.replaceAll("%2\\$@", "(.*){1}");
    res = res.replaceAll("%3\\$@", "(.*){1}");
    res = res.replaceAll("%d", "(.*){1}");
    return res;
  }


  /**
   * 
   * @param aut the application under test. /A/B/C/xxx.app
   * @return the list of the folders hosting the l10ned files.
   * @throws IOSAutomationException
   */
  public static List<File> getL10NFiles(File aut) throws IOSAutomationException {
    List<File> res = new ArrayList<File>();
    File[] files = aut.listFiles(new FileFilter() {

      public boolean accept(File pathname) {
        return pathname.getName().endsWith("lproj");
      }
    });
    for (File f : files) {
      File[] all = f.listFiles();
      for (File potential : all) {
        if (potential.getAbsoluteFile().getAbsolutePath().endsWith(".strings")) {
          res.add(potential);
        }
      }
      /*
       * File resource = new File(f, "Localizable.strings"); if (!resource.exists()) { throw new
       * IOSAutomationException("expected a l10n file here : " + resource); } else {
       * res.add(resource); }
       */
    }
    return res;
  }

  /**
   * Take a json file ( plist exported as json format ) localizable.strings and loads its content.
   * 
   * @param content
   * @throws JSONException
   */
  public void addJSONContent(JSONObject content) throws JSONException {
    this.content.putAll(convertToMap(content));
  }

  /**
   * 
   * @param json the json object containing all the key : value pairs for the translation of the
   *        app.
   * @return a key : value map.
   * @throws JSONException
   */
  private Map<String, String> convertToMap(JSONObject json) throws JSONException {
    Map<String, String> res = new HashMap<String, String>();
    @SuppressWarnings("unchecked")
    Iterator<String> iter = json.keys();
    while (iter.hasNext()) {
      String key = iter.next();
      res.put(key, json.getString(key));
    }
    return res;
  }

  /**
   * 
   * @param f the Localizable.strings file to use for the content.
   * @return
   * @throws Exception
   */
  public static LanguageDictionary createFromFile(File f) throws Exception {
    String name = extractLanguageName(f);
    LanguageDictionary res = new LanguageDictionary(name);
    // and load the content.
    JSONObject content = res.readContentFromBinaryFile(f);
    res.addJSONContent(content);
    return res;
  }


  public static String extractLanguageName(File f) {
    String parent = f.getParentFile().getName();
    String name = parent.replaceFirst(".lproj", "");
    return name;
  }

  /**
   * load the content of the binary file and returns it as a json object.
   * 
   * @param binaryFile
   * @return
   * @throws Exception
   */
  public JSONObject readContentFromBinaryFile(File binaryFile) throws Exception {
    File tmp = File.createTempFile("tmp1234", ".tmp");
    convertL10NFile(binaryFile, tmp);
    JSONObject res = readJSONFile(tmp);
    tmp.delete();
    return res;
  }

  // TODO freynaud use Commands.
  private void convertL10NFile(File strings, File tojson) throws IOSAutomationSetupException {
    List<String> c = new ArrayList<String>();
    c.add("plutil");
    c.add("-convert");
    c.add("json");
    c.add("-o");
    c.add(tojson.getAbsolutePath());
    c.add(strings.getAbsolutePath());
    Command com = new Command(c, true);
    com.executeAndWait();
  }

  /**
   * load the content of the file to a JSON object
   * 
   * @param from
   * @return
   * @throws Exception
   */
  private JSONObject readJSONFile(File from) throws Exception {
    FileInputStream is = new FileInputStream(from);
    StringWriter writer = new StringWriter();
    IOUtils.copy(is, writer, "UTF-8");
    String content = writer.toString();
    content = Normalizer.normalize(content, norme);
    return new JSONObject(content);
  }


  /**
   * format used to store the l10n files. See
   * http://stackoverflow.com/questions/7051120/why-doesnt-my
   * -file-move-into-en-lproj-but-instead-into-a-new-english-lproj
   * 
   * @return
   */
  public boolean isLegacyFormat() {
    return legacyFormat;
  }



  /**
   * the language this dictionary is for.
   * 
   * @return
   */
  public Localizable getLanguage() {
    return language;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((language == null) ? 0 : language.hashCode());
    return result;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    LanguageDictionary other = (LanguageDictionary) obj;
    if (language != other.language) return false;
    return true;
  }



  public String translate(ContentResult res) {
    String languageTemplate = content.get(res.getKey());
    try {

      String format = languageTemplate.replaceAll("%@", "%s");
      format = format.replaceAll("%1\\$@", "%s");
      format = format.replaceAll("%2\\$@", "%s");
      format = format.replaceAll("%3\\$@", "%s");
      format = format.replaceAll("%d", "%s");
      String r = String.format(format, res.getArgs().toArray());
      return r;
    } catch (Exception e) {
      System.err.println("err on " + languageTemplate);
      return "parse error";
    }

  }

  public String getContentForKey(String key) {
    String value = content.get(key);
    return value;
  }



}
