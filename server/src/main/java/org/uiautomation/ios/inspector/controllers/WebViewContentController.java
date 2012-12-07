package org.uiautomation.ios.inspector.controllers;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
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
		boolean ok = pathInfo.startsWith("/latestWebView");
		return ok;
	}

	@Override
	public View handle(HttpServletRequest req) throws Exception {
		this.html = "";

		final Session s = new Session(extractSession(req.getPathInfo()));
		IDESessionModel model = cache.getModel(s);
		JSONObject tree = (JSONObject) model.getTree().get("tree");
		this.html = findSourceHTML(tree);
		return new WebView(this.html);
	}


	private String findSourceHTML(JSONObject node) throws Exception {
		String[] keys = JSONObject.getNames(node);
		try {
			for (int i = 0; i < keys.length; i++) {
				if (keys[i].equals("source")) {
					return node.get(keys[i]).toString();
				} else {
					Object child = node.get(keys[i]);
					if (child instanceof JSONArray) {
						if (((JSONArray)child).length()> 0) {
							String result = findSourceHTML((JSONArray)child);
							if (result != null) {
								return result;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage()
					+ "  Failure when searching the node:" + node
					+ " .Looking for the webView Source html");
		}
		return null;
	}

	private String findSourceHTML(JSONArray array) throws Exception {
		try {
			for (int i = 0; i < array.length(); i++) {
				if(array.get(i) instanceof JSONObject){
					if (array.get(i)!=null) {
						String result = findSourceHTML((JSONObject)array.get(i));
						if (result != null) {
							return result;
						}
					}
				}
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage()
					+ "  Failure when searching in the Array:" + array
					+ " .Looking for the webView Source html");
		}
		return null;
	}

	private String extractSession(String pathInfo) {
		String[] splitUrl = pathInfo.split("/");
		return splitUrl[splitUrl.length - 1];
	}
}
