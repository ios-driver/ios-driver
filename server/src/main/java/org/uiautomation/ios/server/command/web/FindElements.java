package org.uiautomation.ios.server.command.web;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
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

    RemoteWebElement element = null;

    if (getRequest().hasVariable(":reference")) {
      String id = getRequest().getVariableValue(":reference");
      element = new RemoteWebElement(id, getSession());
    }
    String cssSelector = ToCSSSelectorConvertor.convertToCSSSelector(type, value);

    JSONArray array = new JSONArray();
    List<RemoteWebElement> res = getSession().getWebInspector().findElementsByCSSSelector(element, cssSelector);

    for (RemoteWebElement el : res) {
      array.put(new JSONObject().put("ELEMENT", el.getId()));
    }

    return new WebDriverLikeResponse(getRequest().getSession(), 0, array);
  }

}
