package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class GetElementSizeNHandler extends UIAScriptHandler {

  private static final
  String
      template =
      "var element = UIAutomation.cache.get(:reference, :opt_checkStale);" +
      "var result = element.rect();" +
      "UIAutomation.createJSONResponse(':sessionId',0,result)";

  public GetElementSizeNHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);

    String js = template
        .replace(":sessionId", request.getSession())
        .replace(":opt_checkStale", shouldCheckForStaleness() + "")
        .replace(":reference", request.getVariableValue(":reference"));
    setJS(js);

  }


  private boolean shouldCheckForStaleness() {
    boolean check = getConfiguration("checkForStale", true);
    return check;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    // TODO Auto-generated method stub
    return null;
  }

}
