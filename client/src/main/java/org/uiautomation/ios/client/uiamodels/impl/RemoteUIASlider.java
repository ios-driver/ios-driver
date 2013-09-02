/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

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
