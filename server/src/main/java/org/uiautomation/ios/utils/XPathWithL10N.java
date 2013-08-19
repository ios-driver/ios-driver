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

package org.uiautomation.ios.utils;

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