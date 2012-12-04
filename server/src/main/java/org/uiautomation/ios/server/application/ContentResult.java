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

import java.util.ArrayList;
import java.util.List;


/**
 * "Shipping from: %@": "Versand ab: %@",
 */
public class ContentResult {

  // German
  private AppleLocale language;
  // "Shipping from: %@"
  private String key;

  // "Versand ab: %@"
  private String l10ned;
  // "Versand ab: UK"
  private String l10nFormatted;

  private List<String> args = new ArrayList<String>();

  public ContentResult(AppleLocale language, String key, String l10ned, String l10nFormatted) {
    this.language = language;
    this.key = key;
    this.l10ned = l10ned;
    this.l10nFormatted = l10nFormatted;
    extractArgs();
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append("\nlanguage:" + language);
    b.append("\n" + key);
    b.append("\n" + l10ned + "-> " + l10nFormatted + "\n");
    b.append("\nargs:" + args);
    return b.toString();
  }

  private void extractArgs() {
    String pattern = LanguageDictionary.getRegexPattern(l10ned);

    String wildCard = "\\(\\.\\*\\)\\{1\\}";
    String[] pieces = pattern.split(wildCard);

    String content = l10nFormatted;

    for (String s : pieces) {

      int start = content.indexOf(s);
      int end = start + s.length();

      if (start == 0) {
        content = content.substring(end);

      } else {
        String arg = content.substring(0, start);
        args.add(arg);
        content = content.substring(start + s.length());
      }

    }
    if (content.length() != 0) {
      args.add(content);
    }

    // each arg can potentialy be a translated snipet.
    for (String arg : args) {
      //TODO freynaud not implemented.
    }

  }

  public List<String> getArgs() {
    return args;
  }

  public AppleLocale getLanguage() {
    return language;
  }

  public String getKey() {
    return key;
  }

  public String getL10ned() {
    return l10ned;
  }

  public String getL10nFormatted() {
    return l10nFormatted;
  }
}
