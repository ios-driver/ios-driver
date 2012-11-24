package org.uiautomation.ios.server.command.web;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;
import org.uiautomation.ios.webInspector.DOM.RemoteObject;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class ExecuteScriptHandler extends BaseWebCommandHandler {

  public ExecuteScriptHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    String script = getRequest().getPayload().getString("script");
    JSONArray args = getRequest().getPayload().getJSONArray("args");
    Object res = getSession().getWebInspector().executeScript(script, args);

    Response resp = new Response();
    resp.setSessionId(getSession().getSessionId());
    resp.setStatus(0);
    
    
    if (res instanceof RemoteObject) {
      RemoteObject ro = (RemoteObject) res;
      RemoteWebElement rwe = ro.getWebElement();
      JSONObject jo = new JSONObject().put("ELEMENT", rwe.getNodeId().getId());
      resp.setValue(jo);
    } else  if (res instanceof Integer){
      resp.setValue(res);
    }else  if (res instanceof Boolean){
      resp.setValue(res);
    } else {
      System.err.println("don't understand the return of the script." + res);
      resp.setValue(res);
    }
    
   
    return resp;

  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
