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
package org.uiautomation.ios.command.web;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.IOSServerManager;
import org.uiautomation.ios.command.BaseWebCommandHandler;
import org.uiautomation.ios.wkrdp.model.RemoteObject;
import org.uiautomation.ios.wkrdp.model.RemoteWebElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExecuteAsyncScriptHandler extends BaseWebCommandHandler {

  public ExecuteAsyncScriptHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    String script = getRequest().getPayload().getString("script");
    JSONArray args = getRequest().getPayload().getJSONArray("args");
    Object res = getWebDriver().executeAsyncScript(script, args);

    Response resp = new Response();
    resp.setSessionId(getSession().getSessionId());
    resp.setStatus(0);

    if (res instanceof RemoteObject) {
      RemoteObject ro = (RemoteObject) res;
      RemoteWebElement rwe = ro.getWebElement();
      JSONObject jo = new JSONObject().put("ELEMENT", rwe.getReference());
      resp.setValue(jo);
    } else if (res instanceof Integer) {
      resp.setValue(res);
    } else if (res instanceof Boolean) {
      resp.setValue(res);
    } else if (res instanceof Collection) {
      List<Object> rwes = new ArrayList<Object>();

      Collection<Object> all = (Collection<Object>) res;
      for (Object ro : all) {
        if (ro instanceof RemoteObject) {
          JSONObject
              jo =
              new JSONObject()
                  .put("ELEMENT", "" + ((RemoteObject) ro).getWebElement().getNodeId().getId());
          rwes.add(jo);
        } else {
          rwes.add(ro);
        }
      }

      resp.setValue(rwes);
    } else {
      resp.setValue(res);
    }

    return resp;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
