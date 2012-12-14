/*
 * Copyright 2012 ios-driver committers.
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

import java.util.ArrayList;
import java.util.List;

public class FindElementsHandler extends BaseWebCommandHandler {

  public FindElementsHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    JSONObject payload = getRequest().getPayload();
    String type = payload.getString("using");
    String value = payload.getString("value");

    RemoteWebElement element = null;

    if (getRequest().hasVariable(":reference")) {
      int id = Integer.parseInt(getRequest().getVariableValue(":reference"));
      element = new RemoteWebElement(new NodeId(id), getSession());
    } else {
      element = getSession().getWebInspector().getDocument();
    }

    List<RemoteWebElement> res;
    if ("link text".equals(type)) {
      res = element.findElementsByLinkText(value, false);
    } else if ("partial link text".equals(type)) {
      res = element.findElementsByLinkText(value, true);
    } else if ("xpath".equals(type)) {
      res = element.findElementsByXpath(value);
    } else {
      String cssSelector = ToCSSSelectorConvertor.convertToCSSSelector(type, value);
      res = element.findElementsByCSSSelector(cssSelector);
    }

    JSONArray array = new JSONArray();

    List<JSONObject> list = new ArrayList<JSONObject>();
    for (RemoteWebElement el : res) {
      list.add(new JSONObject().put("ELEMENT", "" + el.getNodeId().getId()));
    }

    Response resp = new Response();
    resp.setSessionId(getSession().getSessionId());
    resp.setStatus(0);
    resp.setValue(list);
    return resp;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
