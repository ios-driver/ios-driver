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
package org.uiautomation.ios.UIAModels;

import java.util.ArrayList;

public interface UIACollectionView extends UIAScrollView {

  public ArrayList<UIACollectionCell> getCells();

  //Apple documention wrong about getVisibleCells for UIACollectionViews - method not available
  //http://stackoverflow.com/questions/13789905/uiacollectionview-cells-vs-visiblecells
  //public ArrayList<UIACollectionCell> getVisibleCells();


}
