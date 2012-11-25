package org.uiautomation.ios.client.uiamodels.impl;

import java.util.List;

import org.openqa.selenium.Keyboard;
import org.openqa.selenium.Keys;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.UIAModels.UIAKey;
import org.uiautomation.ios.UIAModels.UIAKeyboard;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;

import com.google.common.collect.ImmutableMap;

public class RemoteUIAKeyboard extends RemoteUIAElement implements Keyboard,UIAKeyboard {

  public RemoteUIAKeyboard(RemoteUIADriver driver, String reference) {
    super(driver, reference);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<UIAButton> getButtons() {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.KEYBOARD_BUTTONS);
    return getDriver().execute(request);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<UIAKey> getKeys() {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.KEYBOARD_KEYS);
    return getDriver().execute(request);
  }

  @Override
  public void sendKeys(CharSequence... keysToSend) {
    StringBuilder builder = new StringBuilder();
    for (CharSequence seq : keysToSend){
      builder.append(seq.toString());
    }
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.TYPE_STRING,ImmutableMap.of("string", "\""+builder.toString()+"\""));
    getDriver().execute(request);
  }
  
  @Override
  public void pressKey(Keys keyToPress) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void releaseKey(Keys keyToRelease) {
    // TODO Auto-generated method stub
    
  }



}
