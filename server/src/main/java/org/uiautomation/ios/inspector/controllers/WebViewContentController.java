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

package org.uiautomation.ios.inspector.controllers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.inspector.model.Cache;
import org.uiautomation.ios.inspector.model.IDESessionModel;
import org.uiautomation.ios.inspector.views.View;
import org.uiautomation.ios.inspector.views.WebView;

import javax.servlet.http.HttpServletRequest;

public class WebViewContentController implements IDECommandController {

  private String html = "";
  private final Cache cache;

  public WebViewContentController(Cache cache) {
    this.cache = cache;
  }

  @Override
  public boolean canHandle(String pathInfo) {
    boolean ok = pathInfo.endsWith("/latestWebView");
    return ok;
  }

  @Override
  public View handle(HttpServletRequest req) throws Exception {
    final Session s = new Session(extractSession(req.getPathInfo()));
    IDESessionModel model = cache.getModel(s);
    JSONObject tree = (JSONObject) model.getTree().get("tree");
    JSONObject webview = findWebViewNode(tree);
    if (webview != null) {
      html = webview.optString("source");
    }
    return new WebView(html);
  }

  private JSONObject findWebViewNode(JSONObject node) {
    String type = node.optString("type");
    if ("UIAWebView".equals(type)) {
      return node;
    } else {
      if (node.has("children")) {
        JSONArray children = node.optJSONArray("children");
        for (int i = 0; i < children.length(); i++) {
          JSONObject child = children.optJSONObject(i);
          JSONObject res = findWebViewNode(child);
          if (res != null) {
            return res;
          }
        }
      }
    }
    return null;
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
