package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class ScrollToElementWithPredicateNHandler extends UIAScriptHandler {


  private static final String voidTemplate =
          "var element = UIAutomation.cache.get(:reference);" +
                  "element.scrollToElementWithPredicate(:predicateString);" +
                  "UIAutomation.createJSONResponse(':sessionId',0,'')";


  public ScrollToElementWithPredicateNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);

    JSONObject payload = request.getPayload();
    String predicateString =  payload.optString("predicateString");

    String js = voidTemplate
            .replace(":sessionId", request.getSession())
            .replace(":reference", request.getVariableValue(":reference"))
            .replace(":predicateString", predicateString);

    setJS(js);

  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
