package org.uiautomation.ios.server.command.uiautomation;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseNativeCommandHandler;

public class ConfigurationSetter extends BaseNativeCommandHandler {

  public ConfigurationSetter(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    WebDriverLikeCommand command = WebDriverLikeCommand.valueOf((String) getRequest().getVariableValue(":command"));
    JSONObject payload = getRequest().getPayload();

    Iterator<String> iter = payload.keys();
    while (iter.hasNext()) {
      String key = iter.next();
      Object value = payload.opt(key);
      getSession().configure(command).set(key, value);
      System.out.println(getSession().configure(command).get(key));
    }

    Response resp = new Response();
    resp.setSessionId(getSession().getSessionId());
    resp.setStatus(0);
    resp.setValue(new JSONObject());
    return resp;

  }
  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
