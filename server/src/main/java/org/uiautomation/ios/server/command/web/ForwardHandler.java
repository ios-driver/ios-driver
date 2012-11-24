package org.uiautomation.ios.server.command.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;

public class ForwardHandler extends BaseWebCommandHandler {

  private static final boolean nativeEvents = false;

  public ForwardHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);

  }

  @Override
  public Response handle() throws Exception {
    boolean useNativeEvents = getConfiguration("nativeEvents", nativeEvents);
    getSession().getContext().getDOMContext().reset();
    if (useNativeEvents) {
      // forwardNative();
    } else {
      getSession().getWebInspector().forward();
    }

    // no page loading event for forward ?
    // getSession().getWebInspector().waitForPageToLoad();
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
        "{boolean}, default to "
            + nativeEvents
            + ".true = UIAutomation native events will be used to enter click the forward arrow (slow) , Web =  WebKit remote debugging will be used.Faster.");
    return desc;
  }

}
