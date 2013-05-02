package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class SetPickerWheelValueNHandler extends UIAScriptHandler {

  private static final
  String
      template =
          "var element = UIAutomation.cache.get(:reference);" +
                  "element.selectValue(':value');" +
                  "UIAutomation.createJSONResponse(':sessionId',0,'')";

  public SetPickerWheelValueNHandler(IOSServerManager driver, WebDriverLikeRequest request) throws JSONException {
    super(driver, request);

      String js = template
              .replace(":sessionId", request.getSession())
              .replace(":reference", request.getVariableValue(":reference"))
              .replace(":value", request.getPayload().getString("value"));
      setJS(js);
  }

  @Override
  public Response handle() throws Exception {
    Response r = super.handle();
    return r;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
