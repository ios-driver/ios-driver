package org.uiautomation.ios.server.command.web;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class FindElementsHandler extends BaseWebCommandHandler {

  public FindElementsHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    JSONObject payload = getRequest().getPayload();

    String type = payload.getString("using");
    String value = payload.getString("value");

    RemoteWebElement element = null;

    if (getRequest().hasVariable(":reference")) {
      int id = Integer.parseInt(getRequest().getVariableValue(":reference"));
      element = new RemoteWebElement(new NodeId(id), getSession());
    } else {
      element = getSession().getWebInspector().getDocument();
    }

    List<RemoteWebElement> res;
    if ("link text".equals(type)) {
      res = element.findElementsByLinkText(value, false);
    } else if ("partial link text".equals(type)) {
      res = element.findElementsByLinkText(value, true);
    } else if ("xpath".equals(type)){
      res = element.findElementsByXpath(value);
    }else {
      String cssSelector = ToCSSSelectorConvertor.convertToCSSSelector(type, value);
      res = element.findElementsByCSSSelector(cssSelector);
    }

    JSONArray array = new JSONArray();

    List<JSONObject> list = new ArrayList<JSONObject>();
    for (RemoteWebElement el : res) {
      list.add(new JSONObject().put("ELEMENT", ""+el.getNodeId().getId()));
    }

    Response resp = new Response();
    resp.setSessionId(getSession().getSessionId());
    resp.setStatus(0);
    resp.setValue(list);
    return resp;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
