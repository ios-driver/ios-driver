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

package org.uiautomation.ios.command.uiautomation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.BeanToJsonConverter;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.IOSServerManager;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.application.APPIOSApplication;
import org.uiautomation.ios.command.BaseNativeCommandHandler;
import org.uiautomation.ios.utils.BuildInfo;
import org.uiautomation.ios.utils.ClassicCommands;

import java.util.List;

public class ServerStatusNHandler extends BaseNativeCommandHandler {

  public ServerStatusNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.uiautomation.ios.command.Handler#handle()
   */
  @Override
  public Response handle() throws Exception {
    JSONObject res = new JSONObject();

    res.put("state", "success");

    res.put("os", new JSONObject().put("name", System.getProperty("os.name"))
        .put("arch", System.getProperty("os.arch"))
        .put("version", System.getProperty("os.version")));

    res.put("java", new JSONObject().put("version", System.getProperty("java.version")));

    res.put("ios", new JSONObject().put("simulatorVersion", ClassicCommands.getDefaultSDK()));

    res.put("supportedApps", getSupportedApps());

    res.put(
        "build",
        new JSONObject().put("version", BuildInfo.getAttribute("version"))
            .put("time", BuildInfo.getAttribute("buildTimestamp"))
            .put("revision", BuildInfo.getAttribute("sha")));

    List<ServerSideSession> sessions = getServer().getSessions();
    Response resp = new Response();

    resp.setStatus(0);
    resp.setValue(res);
    if (sessions.size() == 0) {
      resp.setSessionId(null);
    } else if (sessions.size() == 1) {
      resp.setSessionId(sessions.get(0).getSessionId());
    } else {
      throw new WebDriverException("NI multi sessions per server.");
    }
    return resp;
  }

  private JSONArray getSupportedApps() throws JSONException {
    JSONArray supportedApps = new JSONArray();

    List<IOSCapabilities> caps = getServer().getAllCapabilities();
    for (IOSCapabilities cap : caps) {
      try {
        String json = new BeanToJsonConverter().convert(cap);
        JSONObject app = new JSONObject(json);
        app.put("resources", getAppResources(app));
        supportedApps.put(app);
      } catch (JSONException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
    }
    return supportedApps;
  }

  private JSONObject getAppResources(JSONObject app) {
    JSONObject resources = new JSONObject();
    if (app.has("applicationPath")) {
      try {
        String applicationPath = (String) app.get("applicationPath");
        APPIOSApplication a = getServer().getApplicationStore().getApplication(applicationPath);
        for (String key : a.getResources().keySet()) {
          resources.put(key, "/wd/hub/resources/" + getServer().getCache().getKey(a, key));
        }
      } catch (JSONException ignored) {
      }
    }
    return resources;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
