package org.uiautomation.ios.application;

import org.openqa.selenium.WebDriverException;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper to access the localization feature of the app.
 * @see LocatorWithL10N#translate(String)
 * @see LocatorWithL10N#translateKey(String)
 */
class LocatorWithL10N {

  private static final String pattern = "l10n\\(('|\")(.*?)('|\")\\)";
  private final IOSRunningApplication running;

  LocatorWithL10N(IOSRunningApplication app) {
    this.running = app;
  }

  private Set<String> extractKeysToL10N(String locator) {
    Pattern p = Pattern.compile(pattern);
    Matcher matcher = p.matcher(locator);
    Set<String> keys = new HashSet<String>();
    while (matcher.find()) {
      keys.add(getKey(matcher.group()));
    }
    return keys;
  }

  private String getKey(String function) {
    String res = function.replaceAll("l10n\\(", "");
    res = res.replaceAll("(\"|'|\\))", "");
    return res;
  }


  /**
   *
    * @param locator
   * @return a JS string
   */
  public String translate(String locator) {
    Set<String> keys = extractKeysToL10N(locator);
    String res = locator;
    for (String key : keys) {
      String value = translateKey(key);
      String originalFunction = "l10n\\('" + key + "'\\)|l10n\\(\"" + key + "\"\\)";
      String l10ned = escapeXPath(value);
      res = res.replaceAll(originalFunction, l10ned);
    }

    return res;
  }

  public static String escapeXPath(String base){
    boolean needsConcat= false;
    if (base.contains("'") && base.contains("\"")){
      Pattern pattern = Pattern.compile("[^'\"]+|['\"]");
      Matcher matcher = pattern.matcher(base);
      StringBuilder b = new StringBuilder();
      b.append("concat(");
      boolean first = true;
      while (matcher.find()) {
        String s = matcher.group();
        if (first) {
          first = false;
        } else {
          b.append(",");
        }
        switch (s) {
          case "'":
            b.append("\"'\"");
            break;
          case "\"":
            b.append("'\"'");
            break;
          default:
            b.append("'" + s + "'");
            break;
        }
      }
      b.append(")");
      return b.toString();
    }

    if (base.contains("'")){
      return "\""+base+"\"";
    }

    if (base.contains("\"")){
      return "'"+base+"'";
    }

    return "'"+base+"'";
  }

  public String translateKey(String key) {
    LanguageDictionary dict = running.getCurrentDictionary();
    String value = dict.getContentForKey(key);
    if (value == null) {
      throw new WebDriverException("One of the key requested for localization :" + key
                                   + " isn't available in the l10n files. Most likely the key "
                                   + "provided is wrong. You can use the inspector to find the "
                                   + "correct keys.");
    }
    return LanguageDictionary.getRegexPattern(value);
  }
}