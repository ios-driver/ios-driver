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

import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.exceptions.NoSuchElementException;


public interface UIAElement {

  // Determining Element Positioning
  public UIAPoint getHitPoint();

  public UIARect getRect();

  // Determining and Manipulating Element Hierarchy
  public UIAElement getParent();

  public UIAElement findElement(Criteria c) throws NoSuchElementException;
  public <T> T findElement(Class<T> type,Criteria c) throws NoSuchElementException;

  public UIAElementArray<UIAElement> findElements(Criteria c);

  public UIAElementArray<UIAElement> getAncestry();



  /*
   * pageIndicators pickers popover progressIndicators scrollViews searchBars secureTextFields
   * segmentedControls sliders staticTexts switches tabBar tabBars tableViews textFields textViews
   * toolbar toolbars webViews
   */

  // Gestures and Actions
  // These methods allow you to effect the common gestures and actions a user can perform through
  // the user interface. Options are available for use with some of these methods to give you
  // flexibility in defining and varying the attributes of the gesture or action to be performed.
  public void tap();

  public void touchAndHold(int duration);

  public void doubleTap();

  public void twoFingerTap();

  // tapWithOptions();
  // dragInsideWithOptions
  // flickInsideWithOptions
  public void scrollToVisible();

  // rotateWithOptions

  // Determining Element State
  // Use these methods to determine whether an element is still valid.
  /*
   * isValid checkIsValid waitForInvalid hasKeyboardFocus isEnabled
   */
  /**
   * 
   * @return true is the element is currently on the screen and can be scrolled to.
   */
  public boolean isValid();

  /**
   * 
   * @return true is the element is currently on the screen. It doesn't have to be currently
   *         displayed, it can be outside of the view area and accessible via scrolling only.In that
   *         case, returns true as well.
   */
  public boolean isVisible();

  // Identifying Elements
  public String getLabel();

  public String getName();

  public String getValue();



  public JSONObject logElementTree(File screenshot) throws Exception;

}
