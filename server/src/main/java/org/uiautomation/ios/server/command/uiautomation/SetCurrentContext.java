package org.uiautomation.ios.server.command.uiautomation;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAWebView;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseNativeCommandHandler;

public class SetCurrentContext extends BaseNativeCommandHandler {

  public SetCurrentContext(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    String context = getRequest().getPayload().getString("name");
    WorkingMode mode = WorkingMode.valueOf(context);
    if (mode == WorkingMode.Web) {
      List<UIAElement> views = getSession().getNativeDriver().findElements(new TypeCriteria(UIAWebView.class));
      if (views.isEmpty()) {
        throw new NoSuchWindowException("Cannot find a web view in the current app.");
      }
    }
    getSession().setMode(mode);
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
