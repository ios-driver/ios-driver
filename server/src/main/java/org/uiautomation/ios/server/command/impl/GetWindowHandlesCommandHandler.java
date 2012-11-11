package org.uiautomation.ios.server.command.impl;

import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.uiautomation.ios.UIAModels.UIAWebView;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.mobileSafari.WebInspector;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.command.BaseCommandHandler;
import org.uiautomation.ios.webInspector.DOM.Node;

public class GetWindowHandlesCommandHandler extends BaseCommandHandler {

  public GetWindowHandlesCommandHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {


    ServerSideSession sss = getDriver().getSession(getRequest().getSession());
    JSONArray json = new JSONArray();
    json.put("nativeView");

    if (sss.getNativeDriver().findElements(new TypeCriteria(UIAWebView.class)).size() > 0) {
      try {
        Node node = sss.getWebInspector().getDocument();
        json.put("webView_" + node.getNodeId());
      } catch (TimeoutException te) {
        // ignore.
        System.err.println("timeout");
      }
    }

    WebDriverLikeResponse response = new WebDriverLikeResponse(sss.getSessionId(), 0, json);
    return response;
  }

}
