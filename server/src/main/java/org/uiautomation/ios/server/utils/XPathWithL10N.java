package org.uiautomation.ios.server.utils;

import org.openqa.selenium.WebDriverException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XPathWithL10N {

  private final String origin;
  private static final String pattern = "l10n\\(('|\")(.*?)('|\")\\)";
  private final Set<String> keys;
  private final Map<String, String> localizedByKey = new HashMap<String, String>();

  public XPathWithL10N(String xpath) {
    origin = xpath;
    keys = extractKeysToL10N();
  }

  public void setTranslation(String key, String value) {
    localizedByKey.put(key, value);
  }

  public void checkIsFullyL10Ned() {
    for (String key : keys) {
      if (localizedByKey.get(key) == null) {
        throw new WebDriverException("key " + key + " in xpath " + origin + " can't be localized.");
      }
    }
  }

  public String getXPath() {
    checkIsFullyL10Ned();
    String res = origin;
    for (String key : keys) {
      String originalFunction = "l10n\\('" + key + "'\\)|l10n\\(\"" + key + "\"\\)";
      String l10ned = "'" + localizedByKey.get(key) + "'";
      res = res.replaceAll(originalFunction, l10ned);
    }
    return res;
  }

  private Set<String> extractKeysToL10N() {
    Pattern p = Pattern.compile(pattern);
    Matcher matcher = p.matcher(origin);
    Set<String> keys = new HashSet<String>();
    while (matcher.find()) {
      keys.add(getKey(matcher.group()));
    }
    return keys;
  }

  public String getKey(String function) {
    String res = function.replaceAll("l10n\\(", "");
    res = res.replaceAll("(\"|'|\\))", "");
    return res;
  }

  public Set<String> getKeysToL10N() {
    return keys;
  }


  public String getKey() {
    return getKeysToL10N().iterator().next();
  }
}