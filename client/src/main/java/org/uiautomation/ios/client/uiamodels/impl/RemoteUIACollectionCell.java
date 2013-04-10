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

import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.UIAModels.UIACollectionCell;

public class RemoteUIACollectionCell extends RemoteUIAElement implements UIACollectionCell {

  public RemoteUIACollectionCell(RemoteWebDriver driver, String reference) {
    super(driver, reference);
  }

}