package org.uiautomation.ios.e2e.intl;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.L10NStrategy;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;

import java.util.Locale;

public class SourceTest extends BaseIOSDriverTest {

  private
  String
      expected =
      "Bien que 8,848 mètres de haut, Montagne 1 aient été montés la première fois 29 May 1953.";

  @Test
  public void logElement() throws Exception {
    RemoteUIADriver driver = null;
    try {

      driver =
          new RemoteUIADriver(getRemoteURL(),
                              SampleApps.intlMountainsCap(Locale.FRENCH.toString()));
      Criteria c1 = new TypeCriteria(UIATableCell.class);
      UIAElement element = driver.findElement(c1);
      element.tap();

      NameCriteria
          criteria =
          new NameCriteria("sentenceFormat", L10NStrategy.serverL10N, MatchingStrategy.regex);
      UIAElement el = driver.findElement(criteria);
      JSONObject log = el.logElementTree(null, true);
      Orientation o = Orientation.fromInterfaceOrientation(log.getInt("deviceOrientation"));
      Assert.assertEquals(o, Orientation.PORTRAIT);
      JSONObject tree = log.getJSONObject("tree");

      Assert.assertEquals(tree.getString("type"), "UIAStaticText");
      Assert.assertEquals(tree.getString("value"), expected);
      Assert.assertEquals(tree.getString("label"), expected);
      Assert.assertEquals(tree.getString("name"), expected);
      Assert.assertNull(tree.optJSONArray("children"));

      log = driver.logElementTree(null, true);
      o = Orientation.fromInterfaceOrientation(log.getInt("deviceOrientation"));
      Assert.assertEquals(o, Orientation.PORTRAIT);
      tree = log.getJSONObject("tree");

      Assert.assertEquals(tree.getString("type"), "UIAApplication");
      Assert.assertEquals(tree.get("value"), JSONObject.NULL);
      Assert.assertEquals(tree.get("label"), "Montagnes");
      Assert.assertEquals(tree.get("name"), "Montagnes");
      Assert.assertEquals(tree.optJSONArray("children").length(), 2);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
}
