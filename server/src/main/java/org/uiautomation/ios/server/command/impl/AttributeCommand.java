package org.uiautomation.ios.server.command.impl;

import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class AttributeCommand extends UIAScriptHandler {

  private static final String template = 
      "var parent = UIAutomation.cache.get(:reference);" +
      "var myStringResult = parent:attribute ;" +
      "UIAutomation.createJSONResponse(':sessionId',0,myStringResult)";
  
  public AttributeCommand(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
    
    String attributeMethod = "."+request.getVariableValue(":name")+"()";
    String js =  template
            .replace(":sessionId", request.getSession())
            .replace(":attribute",attributeMethod)
            .replace(":reference", request.getVariableValue(":reference"));
    setJS(js);
  }

}
