/*
 * Copyright 2013 ios-driver committers.
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
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.UIAModels.ScrollDirection;
import org.uiautomation.ios.UIAModels.UIAScrollView;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;

public class RemoteUIAScrollView extends RemoteUIAElement implements UIAScrollView {

  public RemoteUIAScrollView(RemoteWebDriver driver, String reference) {
    super(driver, reference);
  }

  @Override
  public void scroll(ScrollDirection scrollDirection) {

    switch (scrollDirection) {

      case UP:
        createScrollRequest("up");
        break;
      case DOWN:
        createScrollRequest("down");
        break;
      case LEFT:
        createScrollRequest("left");
        break;
      case RIGHT:
        createScrollRequest("right");
        break;
      default:
        throw new WebDriverException("Scrolling direction : " + scrollDirection + " not recognised");

    }

  }

  private void createScrollRequest(String direction) {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.ELEMENT_SCROLL,
            ImmutableMap.of("direction", direction));

    commandExecutor.execute(request);
  }


  @Override
  public void scrollToElementWithName(String name) {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.ELEMENT_SCROLL,
            ImmutableMap.of("name", name));
    commandExecutor.execute(request);
  }

  @Override
  public void scrollToElementWithPredicate(String predicate) {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.ELEMENT_SCROLL,
            ImmutableMap.of("predicateString", predicate));
    commandExecutor.execute(request);
  }


}
