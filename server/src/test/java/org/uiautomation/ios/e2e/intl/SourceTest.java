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
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;

import java.util.Locale;

public class SourceTest extends BaseIOSDriverTest {

  private static final String expected =
      "Bien que 8,848 mètres de haut, Montagne 1 aient été montés la première fois 29 May 1953.";

  @Test
  public void logElement() throws Exception {
    driver =
        new RemoteIOSDriver(getRemoteURL(),
            SampleApps.intlMountainsCap(Locale.FRENCH.toString()));
    Criteria c1 = new TypeCriteria(UIATableCell.class);
    UIAElement element = driver.findElement(c1);
    element.tap();

    NameCriteria criteria =
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

    // there is a new UIAWindow in the app in ios7. 2 becomes 3.
    Assert.assertEquals(tree.optJSONArray("children").length(), 3);
  }
}
