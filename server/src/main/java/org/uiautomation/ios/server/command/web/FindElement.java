package org.uiautomation.ios.server.command.web;

import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseCommandHandler;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class FindElement extends BaseCommandHandler {

  public FindElement(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
    JSONObject payload = getRequest().getPayload();

    String type = payload.getString("using");
    String value = payload.getString("value");

    JSONObject element = new JSONObject();
    RemoteWebElement rmo;
    if ("css selector".equals(type)) {
      rmo = getSession().getWebInspector().findElementByCSSSelector(null, value);   
    } else if ("id".equals(type)) {
      rmo = getSession().getWebInspector().findElementById(null, value);
    } else {
      throw new IOSAutomationException("selector not implemented : " + type);
    }
    
    if (rmo == null){
      return new WebDriverLikeResponse(getRequest().getSession(), 7,"No element found for "+type+"="+value );
    }else{
      element.put("ELEMENT", rmo.getId());
      return new WebDriverLikeResponse(getRequest().getSession(), 0, element);
    }
  }

}
