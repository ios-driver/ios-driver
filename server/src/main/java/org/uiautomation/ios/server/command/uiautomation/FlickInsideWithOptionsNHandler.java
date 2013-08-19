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

public class FlickInsideWithOptionsNHandler extends UIAScriptHandler {

  private static final String voidTemplate =
          "var element = UIAutomation.cache.get(:reference);" +
                  "element.flickInsideWithOptions({touchCount:touchCountx, startOffset:{x:xstart, y:ystart}, endOffset:{x:xend, y:yend}});" +
                  "UIAutomation.createJSONResponse(':sessionId',0,'')";


  public FlickInsideWithOptionsNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);


    JSONObject payload = request.getPayload();

    String js = voidTemplate
            .replace(":sessionId", request.getSession())
            .replace(":reference", request.getVariableValue(":reference"))
            .replace("xstart", payload.optString("xstart"))
            .replace("ystart", payload.optString("ystart"))
            .replace("xend", payload.optString("xend"))
            .replace("yend", payload.optString("yend"))
            .replace("touchCountx", payload.optString("touchCount"));


    setJS(js);


  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
