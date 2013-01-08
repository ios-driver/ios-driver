package org.uiautomation.ios.server.command.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;

import java.util.ArrayList;
import java.util.List;


public class SetImplicitWaitTimeoutHandler extends BaseWebCommandHandler {

  private static final List<WebDriverLikeCommand> impacted = new ArrayList<WebDriverLikeCommand>();

  public SetImplicitWaitTimeoutHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
    impacted.add(WebDriverLikeCommand.ELEMENT);
    impacted.add(WebDriverLikeCommand.ELEMENT_ROOT);
    impacted.add(WebDriverLikeCommand.ELEMENTS);
    impacted.add(WebDriverLikeCommand.ELEMENTS_ROOT);
  }


  @Override
  public Response handle() throws Exception {
    Integer ms = getRequest().getPayload().getInt("ms");
    for (WebDriverLikeCommand command : impacted) {
      getSession().configure(command).set("implicit_wait", ms);
    }

    Response res = new Response();
    res.setSessionId(getSession().getSessionId());
    res.setStatus(0);
    res.setValue(new JSONObject());
    return res;
  }


  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
