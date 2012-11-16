package org.uiautomation.ios.server.command.web;


import org.json.JSONArray;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseCommandHandler;
import org.uiautomation.ios.webInspector.DOM.RemoteObject;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class ExecuteScript extends BaseCommandHandler {

  public ExecuteScript(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
    String script = getRequest().getPayload().getString("script");
    JSONArray args =  getRequest().getPayload().getJSONArray("args");
    Object res = getSession().getWebInspector().executeScript(script,args);
   
    if (res instanceof RemoteObject){
      RemoteObject ro = (RemoteObject)res;
      System.out.println("script returned : "+ro.getId());
      RemoteWebElement rwe = ro.getWebElement();
      JSONObject jo = new JSONObject().put("ELEMENT", rwe.getNodeId().getId());
      return new WebDriverLikeResponse(getRequest().getSession(), 0, jo);
    }else {
      System.err.println("don't understand the return of the script."+res);
      return new WebDriverLikeResponse(getRequest().getSession(), 0, res);
    }
   
  }

}
