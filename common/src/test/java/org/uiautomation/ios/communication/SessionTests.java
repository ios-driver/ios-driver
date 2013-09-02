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

package org.uiautomation.ios.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;

public class SessionTests {

  private JSONObject empty = new JSONObject();
  private JSONObject simple1 = new JSONObject();
  private JSONObject array = new JSONObject();


  @BeforeClass
  public void setup() throws JSONException {
    simple1.put("boolean", true);
    simple1.put("string", "abc");

    array.put("boolean", true);
    array.put("string", "abc");
    JSONArray a = new JSONArray();
    a.put("a1");
    a.put("a2");
    a.put("a3");
    array.put("array", a);

  }

  @Test
  public void empty() throws JSONException {
    Map<String, Object> decoded = new IOSCapabilities(empty).getRawCapabilities();
    Assert.assertTrue(decoded.isEmpty());
  }


  @Test
  public void simple1() throws JSONException {
    Map<String, Object> decoded = new IOSCapabilities(simple1).getRawCapabilities();
    Assert.assertEquals(decoded.size(), simple1.length());
    Assert.assertEquals(decoded.get("boolean"), true);
    Assert.assertEquals(decoded.get("string"), "abc");
  }

  @Test
  public void array() throws JSONException {
    Map<String, Object> decoded = new IOSCapabilities(array).getRawCapabilities();
    Assert.assertEquals(decoded.size(), array.length());
    Assert.assertEquals(decoded.get("boolean"), true);
    Assert.assertEquals(decoded.get("string"), "abc");
    Assert.assertTrue(decoded.get("array") instanceof List);
    List<String> a = new ArrayList<String>((List) decoded.get("array"));
    Assert.assertEquals(a.size(), 3);
  }
}
