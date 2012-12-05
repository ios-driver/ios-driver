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
package org.uiautomation.ios.inspector.controllers;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.inspector.model.Cache;
import org.uiautomation.ios.inspector.model.IDESessionModel;
import org.uiautomation.ios.inspector.views.JSONView;
import org.uiautomation.ios.inspector.views.View;

public class TreeController implements IDECommandController {

  private final Cache cache;

  public TreeController(Cache cache) {
    this.cache = cache;
  }

  @Override
  public boolean canHandle(String pathInfo) {
    return pathInfo.endsWith("/tree");
  }

  @Override
  public View handle(HttpServletRequest req) throws Exception {
    final Session s = new Session(extractSession(req.getPathInfo()));
    IDESessionModel model = cache.getModel(s);
    JSONObject rootNode = model.getTree().getJSONObject("tree");
    JSONObject jsTreeObject = createFrom(rootNode);
    return new JSONView(jsTreeObject);

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
    if (from.has("source")){
      metadata.put("source", from.getString("source"));
    }

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
    b.append("[" + node.getString("type") + "]-");

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

  private String extractSession(String pathInfo) {

    if (pathInfo.startsWith("/session/")) {
      String tmp = pathInfo.replace("/session/", "");
      if (tmp.contains("/")) {
        return tmp.split("/")[0];
      } else {
        return tmp;
      }
    } else {
      throw new WebDriverException("cannot extract session id from " + pathInfo);
    }
  }

}
