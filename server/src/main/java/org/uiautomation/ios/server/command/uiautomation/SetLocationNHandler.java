/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uiautomation.ios.server.command.uiautomation;


import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.utils.JSTemplate;

public class SetLocationNHandler extends UIAScriptHandler {

  private static final JSTemplate template = new JSTemplate(
      "var res = UIATarget.localTarget().setLocation(%:coord$s);" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,'')",
      "sessionId", "coord");

  public SetLocationNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
    JSONObject payload = request.getPayload();
    String js = template.generate(
        request.getSession(),
        "{'latitude': 45 ,'longitude': 45 }");
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