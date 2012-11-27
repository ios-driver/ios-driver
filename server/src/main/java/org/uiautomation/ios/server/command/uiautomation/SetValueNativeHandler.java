package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class SetValueNativeHandler extends UIAScriptHandler{

  private static final String voidTemplate =
      "var parent = UIAutomation.cache.get(:reference);" +
      "parent:jsMethod;" +
      "UIAutomation.createJSONResponse(':sessionId',0,'')";
  
  public SetValueNativeHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
    try {
      JSONArray array  =request.getPayload().getJSONArray("value");
      String value = array.getString(0);
      String corrected = value.replaceAll("\\\\", "\\\\\\\\");
      corrected = corrected.replaceAll("\\n", "\\\\n");
      corrected = corrected.replaceAll("\\t", "\\\\t");


      
      String js = voidTemplate
          .replace(":sessionId", request.getSession())
          .replace(":reference", request.getVariableValue(":reference"))
          .replace(":jsMethod", ".setValue('"+corrected+"')");
      setJS(js);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    
    return noConfigDefined();
  }

}
