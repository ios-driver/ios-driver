/*
 * Copyright 2012 ios-driver committers.
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

import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.UIAElementArray;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.UIATableGroup;
import org.uiautomation.ios.UIAModels.UIATableView;
import org.uiautomation.ios.communication.WebDriverLikeCommand;

// TODO freynaud use findElements
public class RemoteUIATableView extends RemoteUIAScrollView implements UIATableView {

  public RemoteUIATableView(RemoteUIADriver driver, String reference) {
    super(driver, reference);
  }



  /*public UIAElementArray<UIATableGroup> getGroups() {
    return (UIAElementArray<UIATableGroup>) getRemoteObject(WebDriverLikeCommand.TABLE_GROUPS,
        new JSONObject());
  }

  public UIAElementArray<UIATableCell> getCells() {
    return (UIAElementArray<UIATableCell>) getRemoteObject(WebDriverLikeCommand.TABLE_CELLS,
        new JSONObject());
  }

  public UIAElementArray<UIATableCell> getVisibleCells() {
    return (UIAElementArray<UIATableCell>) getRemoteObject(
        WebDriverLikeCommand.TABLE_VISIBLE_CELLS, new JSONObject());
  }*/

}
