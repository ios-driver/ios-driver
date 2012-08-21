package org.uiautomation.ios.client.uiamodels.impl;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.UIAModels.UIAElementArray;
import org.uiautomation.ios.UIAModels.UIAKey;
import org.uiautomation.ios.UIAModels.UIAKeyboard;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.exceptions.IOSAutomationException;

public class RemoteUIAKeyboard extends RemoteUIAElement implements UIAKeyboard {

  public RemoteUIAKeyboard(RemoteUIADriver driver, String reference) {
    super(driver, reference);
  }

  @SuppressWarnings("unchecked")
  @Override
  public UIAElementArray<UIAButton> getButtons() {
    return (UIAElementArray<UIAButton>) getRemoteObject(WebDriverLikeCommand.KEYBOARD_BUTTONS);
  }

  @SuppressWarnings("unchecked")
  @Override
  public UIAElementArray<UIAKey> getKeys() {
    return (UIAElementArray<UIAKey>) getRemoteObject(WebDriverLikeCommand.KEYBOARD_KEYS);
  }

  @Override
  public void typeString(String s) {
    try {
      JSONObject payload = new JSONObject();
      payload.put("string", "\""+s+"\"");
      execute(WebDriverLikeCommand.TYPE_STRING, payload);
    } catch (JSONException e) {
      throw new IOSAutomationException(e);
    }
  }



}
