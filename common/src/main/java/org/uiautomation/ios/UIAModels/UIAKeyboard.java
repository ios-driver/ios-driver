package org.uiautomation.ios.UIAModels;

public interface UIAKeyboard extends UIAElement{
	public UIAElementArray<UIAButton> getButtons();
	public UIAElementArray<UIAKey> getKeys();
	public void typeString(String s);
}
