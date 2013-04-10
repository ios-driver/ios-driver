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
import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.UIAModels.UIACollectionCell;
import org.uiautomation.ios.UIAModels.UIACollectionView;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;

import java.util.ArrayList;

public class RemoteUIACollectionView extends RemoteUIAElement implements UIACollectionView {

    public RemoteUIACollectionView(RemoteWebDriver driver, String reference) {
        super(driver, reference);
    }

    public void scrollUp() {
        WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.SCROLL_UP);
        commandExecutor.execute(request);

    }

    @Override
    public void scrollDown() {
        WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.SCROLL_DOWN);
        commandExecutor.execute(request);
    }

    @Override
    public void scrollLeft() {
        WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.SCROLL_LEFT);
        commandExecutor.execute(request);
    }

    @Override
    public void scrollRight() {
        WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.SCROLL_RIGHT);
        commandExecutor.execute(request);
    }

    @Override
    public void scrollToElementWithName(String name) {
        WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.SCROLL_TO_ELEMENT_WITH_NAME,
                ImmutableMap.of("name", name));
        commandExecutor.execute(request);
    }

    @Override
    public void scrollToElementWithPredicate(String predicate) {
        WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.SCROLL_TO_ELEMENT_WITH_PREDICATE,
                ImmutableMap.of("predicateString", predicate));
        commandExecutor.execute(request);
    }


  @Override
  public ArrayList<UIACollectionCell> getCells() {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.TABLE_CELLS);
    return commandExecutor.execute(request);
  }

  /*
  //Apple documention wrong about getVisibleCells for UIACollectionViews - method not available
  //http://stackoverflow.com/questions/13789905/uiacollectionview-cells-vs-visiblecells
  @Override
  public ArrayList<UIACollectionCell> getVisibleCells() {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.TABLE_VISIBLE_CELLS);
    return commandExecutor.execute(request);
  }
  */
}
