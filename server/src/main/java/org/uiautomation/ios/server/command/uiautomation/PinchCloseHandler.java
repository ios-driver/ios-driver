package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class PinchCloseHandler extends UIAScriptHandler{
  //UIATarget.localTarget().pinchCloseFromToForDuration({x:"300", y:"400"}, {x:"50", y:"100"}, "1");
  
  
    private static final String template = 
        "UIATarget.localTarget().pinchCloseFromToForDuration({x:':x1', y:':y1'}, {x:':x2', y:':y2'}, ':duration');" +
        "UIAutomation.createJSONResponse(':sessionId',0,'')";
    
    public PinchCloseHandler(IOSDriver driver, WebDriverLikeRequest request) {
      super(driver, request);
      
      Object payload = request.getPayload();
      System.out.println(payload);
      String js =  template
              .replace(":sessionId", request.getSession())
              .replace(":x1", "310")
              .replace(":y1", "410")
              .replace(":x2", "10")
              .replace(":y2", "25")
              .replace(":duration", "1");
      setJS(js);
    }
    
    @Override
    public JSONObject configurationDescription() throws JSONException {
      return noConfigDefined();
    }

}
