package org.uiautomation.ios.server.command.web;

import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAKeyboard;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.UIAModels.predicate.ValueCriteria;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.communication.WebDriverLikeResponse;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseCommandHandler;

public class GetCommand extends BaseCommandHandler {

  // TODO freynaud cached by session.
  private static UIAElement addressBar;

  public GetCommand(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public WebDriverLikeResponse handle() throws Exception {
    String url = getRequest().getPayload().getString("url");
    getSession().getContext().getDOMContext().reset();

    WorkingMode mode = WorkingMode.valueOf((String) getConfiguration(WorkingMode.Web).opt("mode",
        WorkingMode.Native.toString()));
    if (WorkingMode.Native == mode) {
      typeURLNative(url);
    } else {
      fakeTypeURL(url);
    }

    getSession().getWebInspector().waitForPageToLoad();
    return new WebDriverLikeResponse(getSession().getSessionId(), 0, new JSONObject());
  }

  private UIAElement getAddressBar() {
    if (addressBar == null) {
      Criteria urlAddressBar = new AndCriteria(new TypeCriteria(UIAElement.class), new ValueCriteria(
          "Go to this address"));
      addressBar = getSession().getNativeDriver().findElement(urlAddressBar);
    }
    return addressBar;
  }

  private void typeURLNative(String url) {

    WorkingMode base = getSession().getMode();
    try {

      getSession().setMode(WorkingMode.Native);
      getAddressBar().tap();
      UIAKeyboard keyboard = getSession().getNativeDriver().getLocalTarget().getFrontMostApp().getKeyboard();
      keyboard.typeString(url);
      keyboard.findElement(new NameCriteria("Go")).tap();

    } finally {
      getSession().setMode(base);
    }
  }

  private void fakeTypeURL(String url) {
    try {
      getSession().getWebInspector().get(url);

    } catch (Exception e) {
      throw new IOSAutomationException("cannot navigate to URL " + url + ", error " + e.getMessage());
    }
  }

}
