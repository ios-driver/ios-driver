package org.uiautomation.ios.server.command.impl;

import org.json.JSONArray;
import org.uiautomation.ios.UIAModels.UIAWebView;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.WorkingMode;
import org.uiautomation.ios.server.command.BaseCommandHandler;

public class GetWindowHandlesCommandHandler extends BaseCommandHandler {

  public GetWindowHandlesCommandHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {

    ServerSideSession sss = getDriver().getSession(getRequest().getSession());
    JSONArray json = new JSONArray();
    json.put(WorkingMode.Native);
    if (sss.getNativeDriver().findElements(new TypeCriteria(UIAWebView.class)).size() > 0) {
      json.put(WorkingMode.Web);
    }
    WebDriverLikeResponse response = new WebDriverLikeResponse(sss.getSessionId(), 0, json);
    return response;
  }

}
