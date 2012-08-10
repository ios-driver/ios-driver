/*
 * Copyright 2012 ios-driver committers.
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
package org.uiautomation.ios.ide.views;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.server.application.IOSApplication;

public class JSTree {

  // original JSON object tree from instrument
  private final JSONObject original;
  // transformed one the matches the jstree.js requesrements.
  private final JSONObject jstree;

  private final IOSApplication app;

  public JSTree(IOSApplication app, JSONObject tree) throws JSONException {
    this.original = tree;
    this.app = app;
    this.jstree = createFrom(original);
  }

  public JSONObject getJSTree() {
    return jstree;
  }

  private JSONObject createFrom(JSONObject from) throws JSONException {
    JSONObject node = new JSONObject();
    node.put("data", getNodeTitle(from));
    node.put("id", getNodeTitle(from));

    // add an id to the node to make them selectable by :reference
    JSONObject attr = new JSONObject();
    attr.put("id", from.getString("ref"));
    node.put("attr", attr);

    JSONObject metadata = new JSONObject();
    metadata.put("type", from.getString("type"));
    metadata.put("reference", from.getString("ref"));
    metadata.put("label", from.getString("label"));
    metadata.put("name", from.getString("name"));
    metadata.put("value", from.getString("value"));
    metadata.put("l10n", from.getJSONObject("l10n"));


    node.put("metadata", metadata);
    JSONObject rect = new JSONObject();
    rect.put("x", from.getJSONObject("rect").getJSONObject("origin").getInt("x"));
    rect.put("y", from.getJSONObject("rect").getJSONObject("origin").getInt("y"));
    rect.put("h", from.getJSONObject("rect").getJSONObject("size").getInt("height"));
    rect.put("w", from.getJSONObject("rect").getJSONObject("size").getInt("width"));
    metadata.put("rect", rect);



    JSONArray children = from.optJSONArray("children");

    if (children != null && children.length() != 0) {
      JSONArray jstreeChildren = new JSONArray();
      node.put("children", jstreeChildren);
      for (int i = 0; i < children.length(); i++) {
        JSONObject child = children.getJSONObject(i);
        JSONObject jstreenode = createFrom(child);
        jstreeChildren.put(jstreenode);
      }
    }

    return node;
  }



  private String getNodeTitle(JSONObject node) throws JSONException {
    StringBuilder b = new StringBuilder();
    b.append("[" + node.getString("type") + ",ref=" + node.getString("ref") + "] - ");

    String name = node.optString("name");
    String value = node.optString("value");
    String label = node.optString("label");

    if (name != null) {
      if (name.length() > 18) {
        name = name.substring(0, 15) + "...";
      }
      b.append(name);
    }
    return b.toString();

  }

}
