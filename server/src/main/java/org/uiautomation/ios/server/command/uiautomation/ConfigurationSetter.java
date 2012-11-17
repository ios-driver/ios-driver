package org.uiautomation.ios.server.command.uiautomation;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseNativeCommandHandler;

public class ConfigurationSetter extends BaseNativeCommandHandler {

  public ConfigurationSetter(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
    WebDriverLikeCommand command = WebDriverLikeCommand.valueOf((String) getRequest().getVariableValue(":command"));
    JSONObject payload = getRequest().getPayload();

    Iterator<String> iter = payload.keys();
    while (iter.hasNext()) {
      String key = iter.next();
      Object value = payload.opt(key);
      getSession().configure(command).set(key, value);
      System.out.println(getSession().configure(command).get(key));
    }

    return new WebDriverLikeResponse(getSession().getSessionId(), 0, new JSONObject());

  }
  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
