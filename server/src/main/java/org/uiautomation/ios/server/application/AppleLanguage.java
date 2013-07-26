package org.uiautomation.ios.server.application;

import java.util.logging.Logger;

/**
 * List of languages a user can select from General>International>Languages
 *
 * name for localization has changed in 10.4 from English , French ( old ) to en , fr ( new )
 * http:// stackoverflow.com/questions/7051120/why-doesnt-my-file-move
 * -into-en-lproj-but-instead-into- a-new-english-lproj Understanding
 * http://developer.apple.com/library/ios/#referencelibrary/GettingStarted/RoadMapiOS/chapters/InternationalizeYourApp/InternationalizeYourApp/InternationalizeYourApp.html
 * and how en-CA or en-JP should be handled would be nice too.
 */
public enum AppleLanguage {

  en("en", "English"),
  fr("fr", "Français"),
  de("de", "Deutsch"),
  ja("ja", ""),
  nl("nl", "Nederlands"),
  it("it", "Italiano"),
  es("es", "Español"),
  pt("pt", "Portugês"),
  pt_PT("pt-PT", "Portugës (Portugal)"),
  da("da", "Dansk"),
  fi("fi", "Suomi"),
  nb("nb", "Norsk (Bokmål)"),
  sv("sv", "Svenska"),
  ko("ko", ""),
  zh_Hans("zh-Hans", ""),
  zh_Hant("zh-Hant", ""),
  ru("ru", "Pýсский"),
  pl("pl", "Polski"),
  tr("tr", "Türkçe"),
  uk("uk", "украї́нська"),
  ar("ar", ""),
  hr("hr", "Hrvatski"),
  cs("cs", "Čeština"),
  el("el", ""),
  he("he", ""),
  ro("ro", "Română"),
  sk("sk", "Slovenščina"),
  th("th", ""),
  id("id", "Bahasa Indonesia"),
  ms("ms", "Bahasa Melayu"),
  en_GB("en-GB", "British English"),
  ca("ca", "Català"),
  hu("hu", "Magyar"),
  vi("vi", "tiếng Việt"),
  unknown("unknown", "unknown");

  private final String code;
  private final String desc;


  private AppleLanguage(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  private static final Logger log = Logger.getLogger(AppleLanguage.class.getName());


  public static AppleLanguage create(String lprojname) {
    if ("French".equals(lprojname)) {
      return fr;
    }
    if ("German".equals(lprojname)) {
      return de;
    }
    if ("Spanish".equals(lprojname)) {
      return es;
    }
    if ("Dutch".equals(lprojname)) {
      return nl;
    }
    if ("English".equals(lprojname)) {
      return en;
    }
    if ("Italian".equals(lprojname)) {
      return it;
    }
    if ("Japanese".equals(lprojname)) {
      return ja;
    }
    if ("Israel".equals(lprojname)) {
      return he;
    }
    if ("Indonesia".equals(lprojname)) {
      return id;
    }
    if ("zh-Hant".equals(lprojname)) {
      return zh_Hant;
    }
    if ("zh-Hans".equals(lprojname)) {
      return zh_Hans;
    }

    try {
      return AppleLanguage.valueOf(lprojname);
    } catch (Exception e) {
      log.warning(lprojname + " isn't recognized.Please file a bug on github.You won't be able to "
                  + "start the app in that language.");
    }
    return unknown;
  }


  public static AppleLanguage emptyLocale(String languageCode) {
    return null;
  }

  public boolean exists() {
    return true;
  }

  public String getIsoCode() {
    return code;
  }
}
