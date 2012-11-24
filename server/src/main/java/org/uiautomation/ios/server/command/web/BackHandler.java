package org.uiautomation.ios.server.command.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;

public class BackHandler extends BaseWebCommandHandler {

  private static final boolean nativeEvents = false;

  public BackHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    boolean useNativeEvents = getConfiguration("nativeEvents", nativeEvents);
    getSession().getContext().getDOMContext().reset();
    if (useNativeEvents) {
      // backNative();
    } else {
      backWeb();
    }

    getSession().getWebInspector().waitForPageToLoad();
    Response resp = new Response();
    resp.setSessionId(getSession().getSessionId());
    resp.setStatus(0);
    resp.setValue(new JSONObject());
    return resp;
  }

  private void backWeb() throws Exception {
    getSession().getWebInspector().back();
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    JSONObject desc = new JSONObject();
    desc.put(
        "nativeEvents",
        "{boolean}, default to "
            + nativeEvents
            + ".true = UIAutomation native events will be used to enter click the back arrow (slow) , Web =  WebKit remote debugging will be used.Faster.");
    return desc;
  }

}
