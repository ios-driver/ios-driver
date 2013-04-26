package org.uiautomation.ios.client.uiamodels.impl;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.UIAModels.UIATouchScreen;


public abstract class RemoteUIATouchScreen extends RemoteUIAElement implements UIATouchScreen {

    public RemoteUIATouchScreen(RemoteWebDriver driver, String reference) {
        super(driver, reference);
    }


}
