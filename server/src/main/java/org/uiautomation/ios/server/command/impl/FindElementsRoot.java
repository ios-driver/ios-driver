package org.uiautomation.ios.server.command.impl;

import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.predicate.AbstractCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.application.ServerSideL10NDecorator;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class FindElementsRoot extends UIAScriptHandler {

  private static final String jsTemplate =
      "var root = UIAutomation.cache.get(':reference');" +
      "var result = root.elements2(:depth,:criteria);" +
      "UIAutomation.createJSONResponse(':sessionId',0,result);";

  public FindElementsRoot(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
    
    String reference = "0";
    if (request.hasVariable(":reference")){
       reference = request.getVariableValue(":reference");
    }
    
    ServerSideSession session = driver.getSession(request.getSession());
    ServerSideL10NDecorator decorator = new ServerSideL10NDecorator(session.getApplication());
    Criteria decorated=null;
    try {
      int depth = request.getPayload().getInt("depth");
     
      JSONObject json = request.getPayload().getJSONObject("criteria");
      decorated = AbstractCriteria.parse(json, decorator);
      String js =
          jsTemplate.replace(":sessionId", request.getSession())
          .replace(":depth", "" + depth)
          .replace(":reference", reference)
          .replace(":criteria", decorated.getJSONRepresentation().toString());
      setJS(js);
    } catch (Exception e) {
      throw new IOSAutomationException("error parsing the payload", e);
    }
  }
}
