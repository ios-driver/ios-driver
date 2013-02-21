package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class IsEnabledNHandler extends UIAScriptHandler {

  private static final
  String
      template =
      "var element = UIAutomation.cache.get(:reference, false);" +
      "var result = element.isEnabled();" +
      "UIAutomation.createJSONResponse(':sessionId',0,result)";

  public IsEnabledNHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);

    String js = template
        .replace(":sessionId", request.getSession())
        .replace(":reference", request.getVariableValue(":reference"));
    setJS(js);

  }


  @Override
  public JSONObject configurationDescription() throws JSONException {
    return super.noConfigDefined();
  }

}
