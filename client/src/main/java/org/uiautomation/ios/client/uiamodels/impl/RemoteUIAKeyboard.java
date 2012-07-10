package org.uiautomation.ios.client.uiamodels.impl;

import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.UIAModels.UIAElementArray;
import org.uiautomation.ios.UIAModels.UIAKey;
import org.uiautomation.ios.UIAModels.UIAKeyboard;
import org.uiautomation.ios.communication.WebDriverLikeCommand;

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

	//TODO: Implement getKeys and typeString

}
