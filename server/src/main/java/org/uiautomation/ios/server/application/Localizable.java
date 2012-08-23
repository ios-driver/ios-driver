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

import org.uiautomation.ios.exceptions.IOSAutomationException;

/**
 * 
 * name for localization has changed in 10.4 from English , French ( old ) to en , fr ( new )
 * http://
 * stackoverflow.com/questions/7051120/why-doesnt-my-file-move-into-en-lproj-but-instead-into-
 * a-new-english-lproj
 * 
 */
public enum Localizable {


  en("en", "English"), 
  fr("fr", "French"), 
  de("de", "German"), 
  it("it", "Italian"), 
  es("es", "Spanish"),
  zh("zh-Hant", "Chinese");

  private final String newFormat;
  private final String legacy;

  private Localizable(String newFormat, String legacy) {
    this.legacy = legacy;
    this.newFormat = newFormat;
  }

  public String getLegacyName() {
    return legacy;
  }

  public static Localizable createFromLegacyName(String appleMapping) throws IOSAutomationException {
    for (Localizable l10n : Localizable.values()) {
      if (l10n.legacy.equals(appleMapping)) {
        return l10n;
      }
    }
    throw new IOSAutomationException("Cannot find mapping for " + appleMapping);
  }

  public static Localizable createFromNewName(String name) throws IOSAutomationException {
    for (Localizable l10n : Localizable.values()) {
      if (l10n.newFormat.equals(name) || l10n.toString().equals(name)) {
        return l10n;
      }
    }

    throw new IOSAutomationException("Cannot find mapping for " + name);

  }

  public static Localizable getEnum(String oldOrNewFormat) {
    if (Localizable.isLegacyName(oldOrNewFormat)) {
      return Localizable.createFromLegacyName(oldOrNewFormat);
    } else if (Localizable.isNewName(oldOrNewFormat)) {
      return Localizable.createFromNewName(oldOrNewFormat);
    } else {
      throw new IOSAutomationException(oldOrNewFormat + " isn't recognized.");
    }
  }


  // TODO freynaud : use Locale as this is the new format apple follows.
  // ISO-639-1 (two-letter) or ISO-639-2 (three-letter)
  public static boolean isNewName(String name) {
    for (Localizable l10n : Localizable.values()) {
      if (l10n.newFormat.equals(name) || l10n.toString().equals(name)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isLegacyName(String name) {
    for (Localizable l10n : Localizable.values()) {
      if (l10n.legacy.equals(name)) {
        return true;
      }
    }
    return false;
  }

  public String getName() {
    return newFormat;
  }



}
