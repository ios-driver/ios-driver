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

import java.util.logging.Logger;

/**
 * List of languages a user can select from General>International>Languages
 *
 * name for localization has changed in 10.4 from English , French ( old ) to en , fr ( new )
 * http:// stackoverflow.com/questions/7051120/why-doesnt-my-file-move
 * -into-en-lproj-but-instead-into- a-new-english-lproj Understanding
 * http://developer.apple.com/library/ios/#referencelibrary/GettingStarted/RoadMapiOS/chapters/InternationalizeYourApp/InternationalizeYourApp/InternationalizeYourApp.html
 * and how en-CA or en-JP should be handled would be nice too.
 * http://stackoverflow.com/questions/3308519/iphone-app-localization-english-problems/5974927#5974927
 *
 * Lists of languages:
 *   http://www.loc.gov/standards/iso639-2/php/English_list.php
 *   http://www.lingoes.net/en/translator/langcode.htm
 *
 */
public enum AppleLanguage {

  aa("aa", "Afar"),
  ab("ab", "Abkhazian"),
  ae("ae", "Avestan"),
  af("af", "Afrikaans"),
  ak("ak", "Akan"),
  am("am", "Amharic"),
  an("an", "Aragonese"),

  ar("ar", "Arabic"),
  ar_AE("ar-AE", "Arabic (U.A.E.)"),
  ar_BH("ar-BH", "Arabic (Bahrain)"),
  ar_DZ("ar-DZ", "Arabic (Algeria)"),
  ar_EG("ar-EG", "Arabic (Egypt)"),
  ar_IQ("ar-IQ", "Arabic (Iraq)"),
  ar_JO("ar-JO", "Arabic (Jordan)"),
  ar_KW("ar-KW", "Arabic (Kuwait)"),
  ar_LB("ar-LB", "Arabic (Lebanon)"),
  ar_LY("ar-LY", "Arabic (Libya)"),
  ar_MA("ar-MA", "Arabic (Morocco)"),
  ar_OM("ar-OM", "Arabic (Oman)"),
  ar_QA("ar-QA", "Arabic (Qatar)"),
  ar_SA("ar-SA", "Arabic (Saudi Arabia)"),
  ar_SY("ar-SY", "Arabic (Syria)"),
  ar_TN("ar-TN", "Arabic (Tunisia)"),
  ar_YE("ar-YE", "Arabic (Yemen)"),

  as("as", "Assamese"),
  av("av", "Avaric"),
  ay("ay", "Aymara"),
  az("az", "Azerbaijani"),
  ba("ba", "Bashkir"),
  be("be", "Belarusian"),
  bg("bg", "Bulgarian"),
  bh("bh", "Bihari languages"),
  bi("bi", "Bislama"),
  bm("bm", "Bambara"),
  bn("bn", "Bengali"),
  bo("bo", "Tibetan"),
  br("br", "Breton"),
  bs("bs", "Bosnian"),
  ca("ca", "Catalan"),
  ce("ce", "Chechen"),
  ch("ch", "Chamorro"),
  co("co", "Corsican"),
  cr("cr", "Cree"),
  cs("cs", "Czech"),
  cu("cu", "Church Slavic"),
  cv("cv", "Chuvash"),
  cy("cy", "Welsh"),
  da("da", "Danish"),

  de("de", "German"),
  da_AT("de-AT", "German (Austria)"),
  de_CH("de-CH", "German (Switzerland)"),
  de_DE("de-DE", "German (Germany)"),
  de_LI("de-LI", "German (Liechtenstein)"),
  de_LU("de-LU", "German (Luxembourg)"),

  dv("dv", "Divehi"),
  dz("dz", "Dzongkha"),
  ee("ee", "Ewe"),
  el("el", "Greek, Modern (1453-)"),

  en("en", "English"),
  en_AU("en-AU", "English (Australia)"),
  en_BZ("en-BZ", "English (Belize)"),
  en_CA("en-CA", "English (Canada)"),
  en_CB("en-CB", "English (Caribbean)"),
  en_GB("en-GB", "English (United Kingdom)"),
  en_IE("en-IE", "English (Ireland)"),
  en_JM("en-JM", "English (Jamaica)"),
  en_NZ("en-NZ", "English (New Zealand)"),
  en_PH("en-PH", "English (Republic of the Philippines)"),
  en_TT("en-TT", "English (Trinidad and Tobago)"),
  en_US("en-US", "English (United States)"),
  en_ZA("en-ZA", "English (South Africa)"),
  en_ZW("en-ZW", "English (Zimbabwe)"),

  eo("eo", "Esperanto"),

  es("es", "Spanish"),
  es_AR("es-AR", "Spanish (Argentina)"),
  es_BO("es-BO", "Spanish (Bolivia)"),
  es_CL("es-CL", "Spanish (Chile)"),
  es_CO("es-CO", "Spanish (Colombia)"),
  es_CR("es-CR", "Spanish (Costa Rica)"),
  es_DO("es-DO", "Spanish (Dominican Republic)"),
  es_EC("es-EC", "Spanish (Ecuador)"),
  es_ES("es-ES", "Spanish (Spain)"),
  es_GT("es-GT", "Spanish (Guatemala)"),
  es_HN("es-HN", "Spanish (Honduras)"),
  es_MX("es-MX", "Spanish (Mexico)"),
  es_NI("es-NI", "Spanish (Nicaragua)"),
  es_PA("es-PA", "Spanish (Panama)"),
  es_PE("es-PE", "Spanish (Peru)"),
  es_PR("es-PR", "Spanish (Puerto Rico)"),
  es_PY("es-PY", "Spanish (Paraguay)"),
  es_SV("es-SV", "Spanish (El Salvador)"),
  es_UY("es-UY", "Spanish (Uruguay)"),
  es_VE("es-VE", "Spanish (Venezuela)"),

  et("et", "Estonian"),
  eu("eu", "Basque"),
  fa("fa", "Persian"),
  ff("ff", "Fulah"),
  fi("fi", "Finnish"),
  fj("fj", "Fijian"),
  fo("fo", "Faroese"),

  fr("fr", "French"),
  fr_BE("fr-BE", "French (Belgium)"),
  fr_CA("fr-CA", "French (Canada)"),
  fr_CH("fr-CH", "French (Switzerland)"),
  fr_FR("fr-FR", "French (France)"),
  fr_LU("fr-LU", "French (Luxembourg)"),
  fr_MC("fr-MC", "French (Monaco)"),

  fy("fy", "Western Frisian"),
  ga("ga", "Irish"),
  gd("gd", "Gaelic"),
  gl("gl", "Galician"),
  gn("gn", "Guarani"),
  gu("gu", "Gujarati"),
  gv("gv", "Manx"),
  ha("ha", "Hausa"),
  he("he", "Hebrew"),
  hi("hi", "Hindi"),
  ho("ho", "Hiri Motu"),

  hr("hr", "Croatian"),
  hr_BA("hr-BA", "Croatian (Bosnia and Herzegovina)"),
  hr_HR("hr-HR", "Croatian (Croatia)"),

  ht("ht", "Haitian"),
  hu("hu", "Hungarian"),
  hy("hy", "Armenian"),
  hz("hz", "Herero"),
  ia("ia", "Interlingua (International Auxiliary Language Association)"),
  id("id", "Indonesian"),
  ie("ie", "Interlingue"),
  ig("ig", "Igbo"),
  ii("ii", "Sichuan Yi"),
  ik("ik", "Inupiaq"),
  io("io", "Ido"),
  is("is", "Icelandic"),
  it("it", "Italian"),
  iu("iu", "Inuktitut"),
  ja("ja", "Japanese"),
  jv("jv", "Javanese"),
  ka("ka", "Georgian"),
  kg("kg", "Kongo"),
  ki("ki", "Kikuyu"),
  kj("kj", "Kuanyama"),
  kk("kk", "Kazakh"),
  kl("kl", "Kalaallisut"),
  km("km", "Central Khmer"),
  kn("kn", "Kannada"),
  ko("ko", "Korean"),
  kr("kr", "Kanuri"),
  ks("ks", "Kashmiri"),
  ku("ku", "Kurdish"),
  kv("kv", "Komi"),
  kw("kw", "Cornish"),
  ky("ky", "Kirghiz"),
  la("la", "Latin"),
  lb("lb", "Luxembourgish"),
  lg("lg", "Ganda"),
  li("li", "Limburgan"),
  ln("ln", "Lingala"),
  lo("lo", "Lao"),
  lt("lt", "Lithuanian"),
  lu("lu", "Luba-Katanga"),
  lv("lv", "Latvian"),
  mg("mg", "Malagasy"),
  mh("mh", "Marshallese"),
  mi("mi", "Maori"),
  mk("mk", "Macedonian"),
  ml("ml", "Malayalam"),
  mn("mn", "Mongolian"),
  mr("mr", "Marathi"),

  ms("ms", "Malay"),
  ms_BN("ms-BN", "Malay (Brunei Darussalam)"),
  ms_MY("ms-MY", "Malay (Malaysia)"),

  mt("mt", "Maltese"),
  my("my", "Burmese"),
  na("na", "Nauru"),
  nb("nb", "Bokmål, Norwegian"),
  nd("nd", "Ndebele, North"),
  ne("ne", "Nepali"),
  ng("ng", "Ndonga"),

  nl("nl", "Dutch"),
  nl_BE("nl-BE", "Dutch (Belgium)"),
  nl_NL("nl-NL", "Dutch (Netherlands)"),

  nn("nn", "Norwegian Nynorsk"),
  no("no", "Norwegian"),
  nr("nr", "Ndebele, South"),
  nv("nv", "Navajo"),
  ny("ny", "Chichewa"),
  oc("oc", "Occitan (post 1500)"),
  oj("oj", "Ojibwa"),
  om("om", "Oromo"),
  or("or", "Oriya"),
  os("os", "Ossetian"),
  pa("pa", "Panjabi"),
  pi("pi", "Pali"),
  pl("pl", "Polish"),
  ps("ps", "Pushto"),

  pt("pt", "Portuguese"),
  pt_BR("pt-BR", "Portuguese (Brazil)"),
  pt_PT("pt-PT", "Portuguese (Portugal)"),

  qu("qu", "Quechua"),
  qu_BO("qu-BO", "Quechua (Bolivia)"),
  qu_EC("qu-EC", "Quechua (Ecuador)"),
  qu_PE("qu-PE", "Quechua (Peru)"),

  rm("rm", "Romansh"),
  rn("rn", "Rundi"),
  ro("ro", "Romanian"),
  ru("ru", "Russian"),
  rw("rw", "Kinyarwanda"),
  sa("sa", "Sanskrit"),
  sc("sc", "Sardinian"),
  sd("sd", "Sindhi"),

  se("se", "Northern Sami"),
  se_FI("se-FI", "Northern Sami (Finland)"),
  se_NO("se-NO", "Northern Sami (Norway)"),
  se_SE("se-SE", "Northern Sami (Sweden)"),

  sg("sg", "Sango"),
  si("si", "Sinhala"),
  sk("sk", "Slovak"),
  sl("sl", "Slovenian"),
  sm("sm", "Samoan"),
  sn("sn", "Shona"),
  so("so", "Somali"),
  sq("sq", "Albanian"),

  sr("sr", "Serbian"),
  sr_BA("sr-BA", "Serbian (Bosnia and Herzegovina)"),
  sr_SP("sr-SP", "Serbian (Serbia and Montenegro)"),

  ss("ss", "Swati"),
  st("st", "Sotho, Southern"),
  su("su", "Sundanese"),

  sv("sv", "Swedish"),
  sv_FI("sv-FI", "Swedish (Finland)"),
  sv_SE("sv-SE", "Swedish (Sweden)"),

  sw("sw", "Swahili"),
  ta("ta", "Tamil"),
  te("te", "Telugu"),
  tg("tg", "Tajik"),
  th("th", "Thai"),
  ti("ti", "Tigrinya"),
  tk("tk", "Turkmen"),
  tl("tl", "Tagalog"),
  tn("tn", "Tswana"),
  to("to", "Tonga (Tonga Islands)"),
  tr("tr", "Turkish"),
  ts("ts", "Tsonga"),
  tt("tt", "Tatar"),
  tw("tw", "Twi"),
  ty("ty", "Tahitian"),
  ug("ug", "Uighur"),
  uk("uk", "Ukrainian"),
  ur("ur", "Urdu"),
  uz("uz", "Uzbek"),
  ve("ve", "Venda"),
  vi("vi", "Vietnamese"),
  vo("vo", "Volapük"),
  wa("wa", "Walloon"),
  wo("wo", "Wolof"),
  xh("xh", "Xhosa"),
  yi("yi", "Yiddish"),
  yo("yo", "Yoruba"),
  za("za", "Zhuang"),

  zh("zh", "Chinese"),
  zh_CHS("zh-CHS", "Chinese (Simplified)"),
  zh_CHT("zh-CHT", "Chinese (Traditional)"),
  zh_CN("zh-CN", "Chinese (China)"),
  zh_HK("zh-HK", "Chinese (Hong Kong SAR)"),
  zh_MO("zh-MO", "Chinese (Macao SAR)"),
  zh_SG("zh-SG", "Chinese (Singapore)"),
  zh_TW("zh-TW", "Chinese (Taiwan)"),
  zh_Hans("zh-Hans", "Chinese (Simplified)"),
  zh_Hant("zh-Hant", "Chinese (Traditional)"),
  zh_Hant_HK("zh-Hant-HK", "Chinese (Traditional, Hong Kong)"),

  zu("zu", "Zulu"),
  unknown("unknown", "unknown");

  private final String code;
  private final String desc;

  private AppleLanguage(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  private static final Logger log = Logger.getLogger(AppleLanguage.class.getName());

  public static AppleLanguage create(String lprojname) {
    switch (lprojname) {
      case "German":    return de;
      case "English":   return en;
      case "Spanish":   return es;
      case "French":    return fr;
      case "Israel":    return he;
      case "Indonesia": return id;
      case "Italian":   return it;
      case "Japanese":  return ja;
      case "Dutch":     return nl;
      default: {
        String normalizedLprojname = lprojname.replace("-", "_");
        try {
          return AppleLanguage.valueOf(normalizedLprojname);
        } catch (Exception e) {
          log.warning(String.format(
              "%s isn't recognized. Please file a bug on github. You won't be able to start the app in that language.",
              lprojname));
        }
        return unknown;
      }
    }
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
