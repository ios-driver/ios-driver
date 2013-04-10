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
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.UIATableGroup;
import org.uiautomation.ios.UIAModels.UIATableView;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;

import java.util.ArrayList;

public class RemoteUIATableView extends RemoteUIAScrollView implements UIATableView {

  public RemoteUIATableView(RemoteWebDriver driver, String reference) {
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
  public ArrayList<UIATableGroup> getGroups() {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.TABLE_GROUPS);
    return commandExecutor.execute(request);
  }

  @Override
  public ArrayList<UIATableCell> getCells() {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.TABLE_CELLS);
    return commandExecutor.execute(request);
  }

  @Override
  public ArrayList<UIATableCell> getVisibleCells() {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.TABLE_VISIBLE_CELLS);
    return commandExecutor.execute(request);
  }
}
