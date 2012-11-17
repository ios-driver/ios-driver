package org.uiautomation.ios.server.command.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.command.BaseCommandHandler;
import org.uiautomation.ios.server.command.BaseNativeCommandHandler;

public class GetSessions extends BaseNativeCommandHandler {

  public GetSessions(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
    JSONArray res = new JSONArray();

    List<ServerSideSession> sessions = getDriver().getSessions();

    for (ServerSideSession s : sessions) {
      JSONObject session = new JSONObject();
      session.put("id", s.getSessionId());

      IOSApplication app = s.getApplication();
      IOSCapabilities cap = getDriver().getCapabilities(app);
      session.put("capabilities", cap.getRawCapabilities());
      res.put(session);
    }
    return new WebDriverLikeResponse(null, 0, res);
  }
  
  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
