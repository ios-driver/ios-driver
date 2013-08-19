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

package org.uiautomation.ios.server;

import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.utils.XPathWithL10N;

public class XPathWithL10NTest {

  private String base = "//*[@name='42']";
  private String simple = "//*[@name=l10n('key_1')]";
  private String simple2 = "//*[@name=l10n('key_1')]";

  private String test = "//*[contains(@name,l10n('key_1'))]//*[contains(@name,l10n(\"key_2\"))]";

  @Test
  public void findKeySingleQuote() {
    XPathWithL10N xpath = new XPathWithL10N(simple);
    Assert.assertEquals(xpath.getKeysToL10N().size(), 1);
    Assert.assertEquals(xpath.getKeysToL10N().toArray()[0], "key_1");
  }

  @Test
  public void findKeyDoubleQuote() {
    XPathWithL10N xpath = new XPathWithL10N(simple2);
    Assert.assertEquals(xpath.getKeysToL10N().size(), 1);
    Assert.assertEquals(xpath.getKeysToL10N().toArray()[0], "key_1");
  }

  @Test
  public void findKeys() {
    XPathWithL10N xpath = new XPathWithL10N(test);
    Assert.assertEquals(xpath.getKeysToL10N().size(), 2);
    Assert.assertTrue(xpath.getKeysToL10N().contains("key_1"));
    Assert.assertTrue(xpath.getKeysToL10N().contains("key_2"));
  }

  @Test
  public void noL10N() {
    XPathWithL10N xpath = new XPathWithL10N(base);
    Assert.assertEquals(xpath.getXPath(), base);
  }

  @Test(expectedExceptions = WebDriverException.class)
  public void throwIfNotFullyL10N() {
    XPathWithL10N xpath = new XPathWithL10N(simple);
    xpath.getXPath();
  }

  @Test
  public void l10n() {
    XPathWithL10N xpath = new XPathWithL10N(simple);
    for (String key : xpath.getKeysToL10N()) {
      xpath.setTranslation(key, key.toUpperCase());
    }
    Assert.assertEquals(xpath.getXPath(), "//*[@name='KEY_1']");
  }

  @Test
  public void l10n2() {
    XPathWithL10N xpath = new XPathWithL10N(test);
    for (String key : xpath.getKeysToL10N()) {
      xpath.setTranslation(key, key.toUpperCase());
    }
    Assert
        .assertEquals(xpath.getXPath(), "//*[contains(@name,'KEY_1')]//*[contains(@name,'KEY_2')]");
  }
}
