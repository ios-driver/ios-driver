package org.uiautomation.ios.inspector.controllers;

import javax.servlet.http.HttpServletRequest;

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
		this.html = "<html>  <head>    <title>Tutorial: HelloWorld</title>  </head>  <body>    <h1>HelloWorld Web Inspector</h1></body></html>";
	}

	@Override
	public boolean canHandle(String pathInfo) {
		boolean ok = pathInfo.startsWith("/latestWebView");
		return ok;
	}

	@Override
	public View handle(HttpServletRequest req) throws Exception {
		final Session s = new Session(extractSession(req.getPathInfo()));
		IDESessionModel model = cache.getModel(s);
		this.html = model.getTree().toString();
		// mock to check that this code is served in the IDE
		return new WebView(this.html);
	}

	private String extractSession(String pathInfo) {
		String[] splitUrl = pathInfo.split("/");
		return splitUrl[splitUrl.length - 1];
	}
}
