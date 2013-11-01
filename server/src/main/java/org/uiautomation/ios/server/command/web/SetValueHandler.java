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
package org.uiautomation.ios.server.command.web;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;
import org.uiautomation.ios.wkrdp.model.RemoteWebElement;
import org.uiautomation.ios.wkrdp.model.RemoteWebNativeBackedElement;

import java.util.logging.Logger;

public class SetValueHandler extends BaseWebCommandHandler {

  private static final Logger log = Logger.getLogger(SetValueHandler.class.getName());

  public SetValueHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    String ref = getRequest().getVariableValue(":reference");
    RemoteWebElement element = getWebDriver().createElement(ref);

    JSONArray array = getRequest().getPayload().getJSONArray("value");
    log.fine("payload : " + getRequest().getPayload().toString(2));
    String value = "";

    for (int i = 0; i < array.length(); i++) {
      value += array.get(i);
    }

    boolean useNativeEvents = (Boolean) getConfiguration("nativeEvents");

    if (useNativeEvents && (element instanceof RemoteWebNativeBackedElement)) {
      ((RemoteWebNativeBackedElement) element).setValueNative(value);
    } else {
      element.setValueAtoms(value);
    }

    Response res = new Response();
    res.setSessionId(getSession().getSessionId());
    res.setStatus(0);
    res.setValue(new JSONObject());
    return res;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    JSONObject desc = new JSONObject();
    desc.put(
        "nativeEvents",
        "{boolean}"
        + "true = UIAutomation native events will be used to enter text. , falwse =  WebKit remote "
        + "debugging will be used.Faster but doesn't always fire all JS events.");
    return desc;
  }
}
