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
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAPickerWheel;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;

import java.util.ArrayList;

public class RemoteUIAPickerWheel extends RemoteUIAElement implements UIAPickerWheel {

    public RemoteUIAPickerWheel(RemoteWebDriver driver, String reference) {
        super(driver, reference);
    }

    public ArrayList<UIAElement> getValues() {
        WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.PICKER_WHEEL_VALUES);
        return commandExecutor.execute(request);
    }

    public void select(String option) {

        WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.PICKER_WHEEL_SET_VALUE,
                ImmutableMap.of("value", option));

        commandExecutor.execute(request);
    }
}

