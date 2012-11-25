package org.uiautomation.ios.UIAModels;

import java.util.List;

import org.openqa.selenium.Keyboard;

public interface UIAKeyboard extends Keyboard{
	public List<UIAButton> getButtons();
	public List<UIAKey> getKeys();
}
