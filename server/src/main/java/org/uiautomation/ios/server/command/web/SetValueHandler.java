package org.uiautomation.ios.server.command.web;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class SetValueHandler extends BaseWebCommandHandler {

  private static final boolean nativeEvents = true;
  
  public SetValueHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    int id = Integer.parseInt(getRequest().getVariableValue(":reference"));
    RemoteWebElement element = new RemoteWebElement(new NodeId(id), getSession());

    JSONArray array = getRequest().getPayload().getJSONArray("value");
    String value = "";
    if (array.length() == 1) {
      Object o = array.get(0);
      if (o instanceof String) {
        value = (String) o;
      } else {
        throw new RuntimeException("NI");
      }
    } else {
      throw new RuntimeException("NI");
    }
    
    boolean useNativeEvents = getConfiguration("nativeEvents", nativeEvents);

    if (useNativeEvents){
      element.setValueNative(value);
    }else{
      element.setValueAtoms(value);
    }
    
    
    Response res = new Response();
    res.setSessionId(getSession().getSessionId());
    res.setStatus(0);
    res.setValue(new JSONObject());
    return res;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    JSONObject desc = new JSONObject();
    desc.put(
        "nativeEvents",
        "{boolean}, default to "
            + nativeEvents
            + ".true = UIAutomation native events will be used to enter the URL (slow) , Web =  WebKit remote debugging will be used.Faster but doesn't fire events.");
    return desc;
  }

}
