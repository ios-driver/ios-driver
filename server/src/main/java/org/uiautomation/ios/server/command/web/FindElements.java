package org.uiautomation.ios.server.command.web;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseCommandHandler;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class FindElements extends BaseCommandHandler {

  public FindElements(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
    JSONObject payload = getRequest().getPayload();

    String type = payload.getString("using");
    String value = payload.getString("value");

    JSONArray array = new JSONArray();
    List<RemoteWebElement> res;
    if ("css selector".equals(type)) {
      res = getSession().getWebInspector().findElementsByCSSSelector(null, value);
    } else if ("id".equals(type)) {
      res = getSession().getWebInspector().findElementsById(null, value);
    } else if ("tag name".equals(type)){
      res = getSession().getWebInspector().findElementsByTagName(null, value);
    }else {
    
      throw new IOSAutomationException("selector not implemented : " + type);
    }

    for (RemoteWebElement el : res) {
      array.put(new JSONObject().put("ELEMENT", el.getId()));
    }

    return new WebDriverLikeResponse(getRequest().getSession(), 0, array);
  }

}
