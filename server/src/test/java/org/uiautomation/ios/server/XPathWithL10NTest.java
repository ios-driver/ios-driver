package org.uiautomation.ios.server;

import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.server.utils.XPathWithL10N;

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
    Assert.assertEquals(xpath.getXPath(), "//*[contains(@name,'KEY_1')]//*[contains(@name,'KEY_2')]");
  }
}
