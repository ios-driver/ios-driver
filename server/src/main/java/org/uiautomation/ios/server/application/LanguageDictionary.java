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

import com.google.common.collect.ImmutableList;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.utils.PlistFileUtils;

import java.io.File;
import java.io.FileFilter;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Represents the apple localization of an IOS native app for a given language. In Xcode, it will be
 * Localizable.string ( theLanguage ) file.
 */
public class LanguageDictionary {

  private static final Logger log = Logger.getLogger(LanguageDictionary.class.getName());
  private static final Pattern specialCharsPattern = Pattern.compile("([\\[\\](){}.?*+])");

  public static final Form form = Form.NFKC;
  public static final Pattern specifierPattern = Pattern.compile("(?<!\\\\)%((?:[1-9]+\\$)?@|d)");

  private final Map<String, String> content = new HashMap<String, String>();
  private final AppleLanguage language;

  /**
   * Creates a new dictionary for the language specified. Will guess the format of the underlying
   * project structure, legacy (with verbose name) or new.
   *
   * @throws WebDriverException if the language isn't recognized.
   */
  public LanguageDictionary(String lprojName) throws WebDriverException {
    language = AppleLanguage.create(lprojName);
  }

  /**
   * @param f the Localizable.strings file to use for the content.
   */
  public static LanguageDictionary createFromFile(File f) throws Exception {
    String name = extractLanguageName(f);
    LanguageDictionary res = new LanguageDictionary(name);
    // and load the content.
    JSONObject content = res.readContentFromBinaryFile(f);
    res.addJSONContent(content);
    return res;
  }

  /**
   * load the content of the binary file and returns it as a json object.
   */
  public JSONObject readContentFromBinaryFile(File binaryFile) throws Exception {
    PlistFileUtils util = new PlistFileUtils(binaryFile);
    return util.toJSON();
  }

  /**
   * Take a json file ( plist exported as json format ) localizable.strings and loads its content.
   */
  public void addJSONContent(JSONObject content) throws JSONException {
    this.content.putAll(convertToMap(content));
  }

  /**
   * @param json the json object containing all the key : value pairs for the translation of the
   *             app.
   * @return a key : value map.
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
   * @param aut the application under test. /A/B/C/xxx.app
   * @return the list of the folders hosting the l10ned files.
   */
  public static List<File> getL10NFiles(File aut) {
    List<File> res = new ArrayList<File>();
    File[] files = aut.listFiles(new FileFilter() {

      public boolean accept(File pathname) {
        return pathname.getName().endsWith("lproj");
      }
    });
    for (File f : files) {
      File[] all = f.listFiles();
      if (all != null) {
        for (File potential : all) {
          String file = potential.getName();
          if (file.endsWith(".strings") && !file.endsWith("InfoPlist.strings")) {
            res.add(potential);
          }
        }
      }
    }
    return res;
  }

  public static String extractLanguageName(File f) {
    String parent = f.getParentFile().getName();
    return parent.replaceFirst(".lproj", "");
  }

  public ImmutableList<ContentResult> getPotentialMatches(String string) throws WebDriverException {
    List<ContentResult> candidates = new ArrayList<ContentResult>();
    for (Map.Entry<String, String> entry : content.entrySet()) {
      String key = entry.getKey();
      String original = entry.getValue();
      if (match(string, original)) {
        candidates.add(new ContentResult(language, key, original, string));
      }
    }
    return filterPotentialMatches(candidates);
  }

  // Filter out noise. If two strings are
  //   1) "%@ meters" and
  //   2) "%@ climbed mountain %@ that is %@ meters",
  // the real match is (2). (1) is just noise from another sentence.
  private ImmutableList<ContentResult> filterPotentialMatches(List<ContentResult> candidates) {
    // Sort candidates such that those with the longest formatted strings come first.
    Collections.sort(candidates, new Comparator<ContentResult>() {
      @Override
      public int compare(ContentResult o1, ContentResult o2) {
        String o1Formatted = o1.getL10nFormatted();
        String o2Formatted = o2.getL10nFormatted();
        int o1Length = o1Formatted.length();
        int o2Length = o2Formatted.length();
        // Sort longest to shortest, then lexicographically.
        if (o1Length > o2Length) {
          return -1;
        } else if (o1Length == o2Length) {
          return o1Formatted.compareTo(o2Formatted);
        } else {
          return 1;
        }
      }
    });
    // Build the final result by filtering out ContentResult instances that with formatted strings that are contained by
    // the already selected instances' formatted strings.
    List<ContentResult> res = new ArrayList<ContentResult>();
    for (ContentResult candidate : candidates) {
      boolean contained = false;
      for (ContentResult member : res) {
        if (member.getL10nFormatted().contains(candidate.getL10nFormatted())) {
          contained = true;
          break;
        }
      }
      if (!contained) {
        res.add(candidate);
      }
    }
    return ImmutableList.copyOf(res);
  }

  public boolean match(String content, String originalText) {
    String normalizedContent = Normalizer.normalize(content, form);
    String normalizedOriginalText = Normalizer.normalize(originalText, form);

    String pattern = getRegexPattern(normalizedOriginalText);
    try {
      return normalizedContent.matches(pattern);
    } catch (PatternSyntaxException e) {
      throw new RuntimeException(String.format("Unexpected error matching against pattern \"%s\"", pattern));
    }
  }

  // "Shipping from: %@": "Versand ab: %@",
  public static String getRegexPattern(String original) {
    String res = original;
    res = specialCharsPattern.matcher(res).replaceAll("\\\\$1");
    res = specifierPattern.matcher(res).replaceAll("(.*){1}");
    return res;
  }

  /**
   * format used to store the l10n files. See
   * http://stackoverflow.com/questions/7051120/why-doesnt-my
   * -file-move-into-en-lproj-but-instead-into-a-new-english-lproj
   *
   * @return
   */
  /*
   * public boolean isLegacyFormat() { return legacyFormat; }
   */

  /**
   * the language this dictionary is for.
   */
  public AppleLanguage getLanguage() {
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
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    LanguageDictionary other = (LanguageDictionary) obj;
    if (language == null) {
      if (other.language != null) {
        return false;
      }
    } else if (!language.equals(other.language)) {
      return false;
    }
    return true;
  }

  public String translate(ContentResult res) {
    String languageTemplate = content.get(res.getKey());
    ImmutableList<String> args = res.getArgs();
    Matcher matcher = specifierPattern.matcher(languageTemplate);
    int matchCount = 0;
    while (matcher.find()) {
      matchCount++;
    }
    if (matchCount != args.size()) {
      log.warning(String.format("Format string \"%s\" requires %d argument%s, but %d argument%s provided: %s",
          languageTemplate,
          matchCount,
          matchCount == 1 ? "" : "s",
          args.size(),
          args.size() == 1 ? "" : "s",
          Arrays.toString(args.toArray())));
      return "ERR:languageTemplate";
    }
    String format = matcher.replaceAll("%s");
    return String.format(format, res.getArgs().toArray());
  }

  public String getContentForKey(String key) {
    return content.get(key);
  }
}
