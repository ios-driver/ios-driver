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

import java.util.List;

/**
 * "Shipping from: %@": "Versand ab: %@",
 */
public class ContentResult {

  // German
  private final AppleLanguage language;
  // "Shipping from: %@"
  private final String key;

  // "Versand ab: %@"
  private final String l10ned;
  // "Versand ab: UK"
  private final String l10nFormatted;

  // "Versand ab: %@"
  // "Versand ab: UK"
  //              ^^
  private final ImmutableList<String> args;

  public ContentResult(AppleLanguage language, String key, String l10ned, String l10nFormatted) {
    this.language = language;
    this.key = key;
    this.l10ned = l10ned;
    this.l10nFormatted = l10nFormatted;
    this.args = extractArgs();
  }

  @Override
  public String toString() {
    return String.format("\nlanguage:%s\n%s\n%s->%s\n\nargs:%s", language, key, l10ned, l10nFormatted, args);
  }

  /*
   * Given e.g. l10ned="Hello %@, your %@ called." and l10nFormatted="Hello Dave, your mom called.", extract the args
   * ["Dave", "mom"].  Note that it is possible for ambiguities to prevent reliable arg recovery.  For example,
   * if l10ned="a%@a%@a" and l10nFormatted="aaaaaa", the args could be ["", "aaa"], ["a", "aa"], ..., ["aaa", ""].
   */
  private ImmutableList<String> extractArgs() {
    ImmutableList.Builder<String> builder = ImmutableList.builder();
    String[] pieces = LanguageDictionary.specifierPattern.split(l10ned);
    String content = l10nFormatted;

    for (String s : pieces) {
      int start = content.indexOf(s);
      int end = start + s.length();

      if (start == 0) {
        content = content.substring(end);
      } else {
        String arg = content.substring(0, start);
        builder.add(arg);
        content = content.substring(start + s.length());
      }
    }
    if (content.length() != 0) {
      builder.add(content);
    }
    return builder.build();
  }

  public ImmutableList<String> getArgs() {
    return args;
  }

  public AppleLanguage getLanguage() {
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
