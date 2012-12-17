package org.uiautomation.ios.server.command.uiautomation;


import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class SetLocationNHandler extends UIAScriptHandler {

  private static final String template = "var res = UIATarget.localTarget().setLocation(:coord);"
                                         + "UIAutomation.createJSONResponse(':sessionId',0,'')";

  public SetLocationNHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);

    JSONObject payload = request.getPayload();

    String js = template.replace(":sessionId", request.getSession())
        .replace(":coord", "{'latitude': 45 ,'longitude': 45 }");
    setJS(js);
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}

/**
 *
 * /Users/freynaud/Library/Application Support/iPhone Simulator/6.1/Library/Caches/locationd/clients.plist
 *
 *  {"com.yourcompany.Geocoder":
 *  {"Whitelisted":false,
 *  "BundleId":"com.yourcompany.Geocoder",
 *  "Authorized":true,
 *  "Purpose":"This may be used to obtain your reverse geocoded address",
 *  "Executable":"",
 *  "Registered":""}
 *  }
 */