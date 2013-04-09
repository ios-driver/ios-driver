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

import java.util.Locale;
import java.util.logging.Logger;

/**
 * name for localization has changed in 10.4 from English , French ( old ) to en , fr ( new )
 * http:// stackoverflow.com/questions/7051120/why-doesnt-my-file-move
 * -into-en-lproj-but-instead-into- a-new-english-lproj
 */
public class AppleLocale {

  private static final Logger log = Logger.getLogger(AppleLocale.class.getName());
  private final Locale locale;
  private final String lproj;

  private static Locale findBestLocale(String lprojname) {
    String corrected = lprojname;
    if ("French".equals(lprojname)) {
      corrected = "fr"; 
    }
    if ("German".equals(lprojname)) {
      corrected = "de";
    }
    if ("Spanish".equals(lprojname)) {
      corrected = "es";
    }
    if("Dutch".equals(lprojname)){
    	corrected = "nl";
    }
    if("English".equals(lprojname)){
    	corrected= "en";
    }
    if("Italian".equals(lprojname)){
    	corrected= "it";
    }
    if("Japanese".equals(lprojname)){
    	corrected= "ja";
    }
    for (Locale l : Locale.getAvailableLocales()) {
      if (l.toString().equals(corrected.replace('-','_'))) {
        return l;
      }
    }
    for (Locale l : Locale.getAvailableLocales()) {
      if (l.getLanguage().equals(corrected)) {
        return l;
      }
    }
    for (Locale l : Locale.getAvailableLocales()) {
      if (l.getDisplayLanguage().equals(corrected)) {
        return l;
      }
    }
    if ("zh-Hant".equals(lprojname)) {
      return Locale.CHINESE;
    }
    if ("zh-Hans".equals(lprojname)) {
      return Locale.CHINESE;
    }
    if ("nb".equals(lprojname)) {
      for (Locale l : Locale.getAvailableLocales()) {
        if (l.getDisplayCountry().equals("Norway")) {
          return l;
        }
      }
    }
    if ("he".equals(lprojname)) {
      for (Locale l : Locale.getAvailableLocales()) {
        if (l.getDisplayCountry().equals("Israel")) {
          return l;
        }
      }
    }
    if ("id".equals(lprojname)) {
      for (Locale l : Locale.getAvailableLocales()) {
        if (l.getDisplayCountry().equals("Indonesia")) {
          return l;
        }
      }
    }
    log.warning(lprojname + " isn't recognized.Please file a bug on github.You won't be able to "
                + "start the app in that language.");
    return new Locale(lprojname);
  }

  public AppleLocale(String lprojname) {
    Locale loc = findBestLocale(lprojname);

    if (loc == null) {
      loc = Locale.ENGLISH;
    }

    locale = loc;
    lproj = lprojname;
  }

  public Locale getLocale() {
    return locale;
  }

  public String getLProj() {
    return lproj;
  }

  public String getAppleLanguagesForPreferencePlist() {
    if (Locale.CHINESE == locale) {
      return "zh-Hant";
    } else {
      return locale == null ? null : locale.toString();
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((locale == null) ? 0 : locale.hashCode());
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
    AppleLocale other = (AppleLocale) obj;
    if (locale == null) {
      if (other.locale != null) {
        return false;
      }
    } else if (!locale.equals(other.locale)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return locale.toString() + " for project " + lproj + ".lproj";
  }

  private AppleLocale(Locale locale, String lproj) {
    this.locale = locale;
    this.lproj = lproj;
  }

  public static AppleLocale emptyLocale(String locale) {
    Locale l = AppleLocale.findBestLocale(locale);
    return new AppleLocale(l, null);
  }

  public boolean exist() {
    return lproj != null;
  }
}
