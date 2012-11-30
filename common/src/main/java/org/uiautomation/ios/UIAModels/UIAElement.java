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

import java.io.File;
import java.util.List;

import org.json.JSONObject;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.uiautomation.ios.UIAModels.predicate.Criteria;


public interface UIAElement extends WebElement{

  public UIARect getRect();

  public <T extends UIAElement> T findElement(Criteria c) throws NoSuchElementException;

  public <T extends UIAElement> T findElement(Class<T> type, Criteria c) throws NoSuchElementException;

  public List<UIAElement> findElements(Criteria c);

  public void tap();

  public void touchAndHold(int duration);

  public void doubleTap();

  public void twoFingerTap();

  public void flickInsideWithOptions(int fingersNumber, UIAPoint startOffset, UIAPoint endOffset);

  public void scrollToVisible();

  public String getLabel();

  public String getName();

  public String getValue();

  public JSONObject logElementTree(File screenshot, boolean translation) throws Exception;

}
