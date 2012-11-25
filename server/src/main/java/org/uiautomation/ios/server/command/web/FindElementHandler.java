package org.uiautomation.ios.server.command.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class FindElementHandler extends BaseWebCommandHandler {

  public FindElementHandler(IOSDriver driver, WebDriverLikeRequest request) {
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

    RemoteWebElement rwe;

    if ("link text".equals(type)) {
      rwe = element.findElementByLinkText(value, false);
    } else if ("partial link text".equals(type)) {
      rwe = element.findElementByLinkText(value, true);
    } else if ("xpath".equals(type)) {
      rwe = element.findElementByXpath(value);
    } else {
      String cssSelector = ToCSSSelectorConvertor.convertToCSSSelector(type, value);
      rwe = element.findElementByCSSSelector(cssSelector);
    }

    JSONObject res = new JSONObject();
    if (rwe == null) {
      throw new NoSuchElementException("No element found for " + type + "=" + value);
    } else {
      res.put("ELEMENT", ""+rwe.getNodeId().getId());
      Response resp = new Response();
      resp.setSessionId(getSession().getSessionId());
      resp.setStatus(0);
      resp.setValue(res);
      return resp;
    }
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
