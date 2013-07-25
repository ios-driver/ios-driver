package org.uiautomation.ios.client.uiamodels.impl;

import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.UIAModels.UIASlider;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;

public class RemoteUIASlider extends RemoteUIAElement implements UIASlider {

    public RemoteUIASlider(RemoteWebDriver driver, String reference) {
        super(driver, reference);
    }

    public void dragToValue(double value) {
        WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.SLIDER_DRAG_TO_VALUE,
                ImmutableMap.of("dragToValue", String.valueOf(value)));
        commandExecutor.execute(request);
    }
}
