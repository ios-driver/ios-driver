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

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSONToXMLConverter {

  private final String xml;

  public JSONToXMLConverter(JSONObject tree) {
    xml = buildXMLDoc(tree).asXML();
  }

  public String asXML() {
    return xml;
  }

  private Document buildXMLDoc(JSONObject tree) {
    Document document = DocumentHelper.createDocument();
    document.setXMLEncoding("UTF-8");
    Element root = document.addElement("root");
    buildXMLNode(tree, root);
    return document;

  }

  private void buildXMLNode(JSONObject from, Element parent) {
    if (from == null) {
      return;
    }
    Element node = parent.addElement(from.optString("type"))
        .addAttribute("name", from.optString("name"))
        .addAttribute("label", from.optString("label"))
        .addAttribute("value", from.optString("value"))
        .addAttribute("ref", from.optString("ref"));

    JSONArray array = from.optJSONArray("children");
    if (array != null) {
      for (int i = 0; i < array.length(); i++) {
        JSONObject n = array.optJSONObject(i);
        buildXMLNode(n, node);
      }
    }
  }
}
