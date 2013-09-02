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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.predicate.L10NStrategy;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.client.uiamodels.impl.ClientSideCriteriaFactory;


public class ClientSideCriteriaTests {

  ClientSideCriteriaFactory factory;

  @BeforeClass
  public void setup() throws IOException, JSONException {
    InputStream is = ClientSideCriteriaTests.class.getResourceAsStream("/ClientSideL10N.json");

    StringWriter writer = new StringWriter();
    IOUtils.copy(is, writer, "UTF-8");
    JSONArray json = new JSONArray(writer.toString());
    Map<String, String> content = new HashMap<String, String>();
    for (int i = 0; i < json.length(); i++) {
      JSONObject entry = json.getJSONObject(i);
      String key = (String) entry.keys().next();
      content.put(key, entry.getString(key));
    }

    factory = new ClientSideCriteriaFactory(content);

  }

  @Test
  public void clienSideMapping() throws JSONException {
    NameCriteria c = factory.nameCriteria("abc", L10NStrategy.clientL10N,MatchingStrategy.exact);
    Assert.assertEquals(c.getValue(), "abc localisé.");
  }

}
