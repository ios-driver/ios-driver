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

package org.uiautomation.ios.server.command.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.command.BaseCommandHandler;
import org.uiautomation.ios.server.utils.BuildInfo;
import org.uiautomation.ios.server.utils.ClassicCommands;

public class ServerStatus extends BaseCommandHandler {

  public ServerStatus(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.uiautomation.ios.server.command.Handler#handle()
   */
  @Override
  public WebDriverLikeResponse handle() throws Exception {
    JSONObject res = new JSONObject();

    res.put(
        "os",
        new JSONObject().put("name", System.getProperty("os.name"))
            .put("arch", System.getProperty("os.arch"))
            .put("version", System.getProperty("os.version")));

    res.put("java", new JSONObject().put("version", System.getProperty("java.version")));

    List<String> sdks = ClassicCommands.getInstalledSDKs();

    res.put("ios", new JSONObject().put("simulatorVersion", sdks.get(sdks.size() - 1)));

    JSONArray supportedApps = new JSONArray();
    for (IOSApplication a : getDriver().getSupportedApplications()) {
      JSONObject app = new JSONObject();
      app.put("applicationPath", a.getApplicationPath().getAbsolutePath());


      app.put("locales", a.getSupportedLanguages());

      JSONObject resources = new JSONObject();
      for (String key : a.getResources().keySet()) {
        resources.put(key, "/wd/hub/resources/" + getDriver().getCache().getKey(a, key));
      }
      app.put("resources", resources);



      app.put("Info.plist", a.getMetadata());
      supportedApps.put(app);
    }

    res.put("supportedApps", supportedApps);

    res.put(
        "build",
        new JSONObject().put("version", BuildInfo.getAttribute("version"))
            .put("time", BuildInfo.getAttribute("buildTimestamp"))
            .put("revision", BuildInfo.getAttribute("sha")));

    // String currentSession = getSessionsManager().getCurrentSessionId();
    // res.put("currentSession",currentSession == null ? JSONObject.NULL : currentSession);
    List<ServerSideSession> sessions = getDriver().getSessions();
    if (sessions.isEmpty()){
      return new WebDriverLikeResponse(null, 0, res);
    }else if (sessions.size()==1){
      return new WebDriverLikeResponse(sessions.get(0).getSessionId(), 0, res);
    }else {
      throw new IOSAutomationException("NI multi sessions per server.");
    }
    
    
  }
}
