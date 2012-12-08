package org.uiautomation.ios.inspector.controllers;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.UIAModels.Session;
import org.uiautomation.ios.inspector.model.Cache;
import org.uiautomation.ios.inspector.model.IDESessionModel;
import org.uiautomation.ios.inspector.views.View;
import org.uiautomation.ios.inspector.views.WebView;

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
