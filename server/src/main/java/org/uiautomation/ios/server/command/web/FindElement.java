package org.uiautomation.ios.server.command.web;

import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
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

    RemoteWebElement element = null;
    
    if (getRequest().hasVariable(":reference")){
      String id = getRequest().getVariableValue(":reference");
      element = new RemoteWebElement(id, getSession());
    }
    
    
    
    String cssSelector = ToCSSSelectorConvertor.convertToCSSSelector(type, value); 
    JSONObject res = new JSONObject();
    RemoteWebElement rmo = getSession().getWebInspector().findElementByCSSSelector(element, cssSelector);
    
    if (rmo == null){
      return new WebDriverLikeResponse(getRequest().getSession(), 7,"No element found for "+type+"="+value );
    }else{
      res.put("ELEMENT", rmo.getId());
      return new WebDriverLikeResponse(getRequest().getSession(), 0, res);
    }
  }

}
