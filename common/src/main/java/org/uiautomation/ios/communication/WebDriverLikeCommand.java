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
package org.uiautomation.ios.communication;

import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.UIAApplication;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAElementArray;
import org.uiautomation.ios.UIAModels.UIAKeyboard;
import org.uiautomation.ios.UIAModels.UIAPoint;
import org.uiautomation.ios.UIAModels.UIARect;
import org.uiautomation.ios.UIAModels.UIATarget;
import org.uiautomation.ios.UIAModels.UIAWindow;
import org.uiautomation.ios.UIAModels.UIHost;
import org.uiautomation.ios.exceptions.IOSAutomationException;


// TODO freynaud remove the findElements related ones.
public enum WebDriverLikeCommand {
  NEW_SESSION("POST","/session",String.class),
  GET_SESSION("GET", "/session/:sessionId",JSONObject.class),
  DELETE_SESSION("DELETE","/session/:sessionId",null),
  SET_TIMEOUT("POST", "/session/:sessionId/target/:reference/timeout",Void.class),
  GET_TIMEOUT("GET", "/session/:sessionId/target/:reference/timeout",Integer.class),
  
  // UIATarget
  LOCAL_TARGET("GET","/session/:sessionId/localTarget",UIATarget.class),
  TREE("GET","/session/:sessionId/tree/:reference",String.class),
  HOST("GET","/session/:sessionId/uiaTarget/:reference/host",UIHost.class),
  MODEL("GET","/session/:sessionId/uiaTarget/:reference/model",String.class),
  TARGET_RECT("GET","/session/:sessionId/uiaTarget/:reference/rect",UIARect.class),
  TARGET_TAP("GET","/session/:sessionId/tap/:reference",Void.class),
  TARGET_NAME("GET","/session/:sessionId/uiaTarget/:reference/name",String.class),
  SYSTEM_NAME("GET","/session/:sessionId/uiaTarget/:reference/systemName",String.class),
  SYSTEM_VERSION("GET","/session/:sessionId/uiaTarget/:reference/systemVersion",String.class),
  
  SCREENSHOT_WITH_NAME("POST","/session/:sessionId/uiaTarget/:reference/screenshotWithName",Void.class),
  
   // UIAHost
  PERFORM_TASK_WITH_PATH_ARGUMENTS_TIMEOUT("POST","/session/:sessionId/host/:reference",null), 
  
  FONT_MOST_APP("GET" , "/session/:sessionId/uiaTarget/:reference/fontMostApp",UIAApplication.class),
  
  // UIAApplication
  MAIN_WINDOW("GET" , "/session/:sessionId/uiaApplication/:reference/mainWindow",UIAWindow.class),
  WINDOWS("GET" , "/session/:sessionId/uiaTarget/:reference/windows",UIAElementArray.class),
  
  // UIAElement
  HIT_POINT("GET" , "/session/:sessionId/uiaElement/:reference/hitPoint",UIAPoint.class),
  RECT("GET" , "/session/:sessionId/uiaElement/:reference/rect",UIARect.class),
  
  PARENT("GET" , "/session/:sessionId/uiaElement/:reference/parent",UIAElement.class),
  ELEMENT("GET" , "/session/:sessionId/uiaElement/:reference/elements",UIAElement.class),
  ELEMENTS("GET" , "/session/:sessionId/uiaElement/:reference/elements2",UIAElementArray.class),
  
  ANCESTRY("GET" , "/session/:sessionId/uiaElement/:reference/ancestry",UIAElement.class),
  ACTIVITY_INDICATORS("GET" , "/session/:sessionId/uiaElement/:reference/activityIndicator",UIAElementArray.class),
  BUTTONS("GET" , "/session/:sessionId/uiaElement/:reference/buttons",UIAElementArray.class),
  IMAGES("GET" , "/session/:sessionId/uiaElement/:reference/images",UIAElementArray.class),
  LINKS("GET" , "/session/:sessionId/uiaElement/:reference/links",UIAElementArray.class),
  NAVIGATION_BAR("GET" , "/session/:sessionId/uiaElement/:reference/navigationBar",UIAElement.class),
  NAVIGATION_BARS("GET" , "/session/:sessionId/uiaElement/:reference/navigationBars",UIAElementArray.class),
  
  //UIAKeyboard
  KEYBOARD("GET" , "/session/:sessionId/uiaApplication/:reference/keyboard",UIAKeyboard.class),
  KEYBOARD_BUTTONS("GET","/session/:sessionId/uiaKeyboard/:reference/buttons",UIAElementArray.class),
  KEYBOARD_KEYS("GET","/session/:sessionId/uiaKeyboard/:reference/keys",UIAElementArray.class),
  TYPE_STRING("POST","/session/:sessionId/uiaKeyboard/:reference",Void.class),
  
  IS_VISIBLE("GET" , "/session/:sessionId/uiaElement/:reference/isVisible",Boolean.class),
  IS_STALE("GET" , "/session/:sessionId/uiaElement/:reference/isStale",Boolean.class),
  
  LABEL("GET" , "/session/:sessionId/uiaElement/:reference/label",String.class),
  NAME("GET" , "/session/:sessionId/uiaElement/:reference/name",String.class),
  VALUE("GET" , "/session/:sessionId/uiaElement/:reference/value",String.class),
  WITH_NAME("GET" , "/session/:sessionId/uiaElement/:reference/withName",UIAElement.class),
  WITH_PREDICATE("GET" , "/session/:sessionId/uiaElement/:reference/withPredicate",UIAElement.class),
  WITH_VALUE_FOR_KEY("GET" , "/session/:sessionId/uiaElement/:reference/withValueForKey",UIAElement.class),
  
  TAP("POST" , "/session/:sessionId/uiaElement/:reference/tap",Void.class),
  TOUCH_AND_HOLD("POST" , "/session/:sessionId/uiaElement/:reference/touchAndHold",Void.class),
  DOUBLE_TAP("POST" , "/session/:sessionId/uiaElement/:reference/doubleTap",Void.class),
  TWO_FINGER_TAP("POST" , "/session/:sessionId/uiaElement/:reference/twoFingerTap",Void.class),
  TAP_WITH_OPTIONS("POST" , "/session/:sessionId/uiaElement/:reference/tapWithOptions",Void.class),
  DRAG_INSIDE_WITH_OPTIONS("POST" , "/session/:sessionId/uiaElement/:reference/dragInsideWithOptions",Void.class),
  FLICK_INSIDE_WITH_OPTIONS("POST" , "/session/:sessionId/uiaElement/:reference/flickInsideWithOption",Void.class),
  SCROLL_TO_VISIBLE("POST" , "/session/:sessionId/uiaElement/:reference/scrollToVisible",Void.class),
  ROTATE_WITH_OPTIONS("POST" , "/session/:sessionId/uiaElement/:reference/rotateWithOptions",Void.class),
  
  
  // UIAElementArray
  GET("GET" , "/session/:sessionId/uiaElementArray/:reference",UIAElement.class),
  FIRST_WITH_NAME("GET" , "/session/:sessionId/uiaElementArray/:reference/firstWithName",UIAElement.class),
  FIRST_WITH_PREDICATE("GET" , "/session/:sessionId/uiaElementArray/:reference/firstWithPredicate",UIAElement.class),
  FIRST_WITH_VALUE_FOR_KEY("GET" , "/session/:sessionId/uiaElementArray/:reference/firstWithValueForKey",UIAElement.class),
  ARRAY_WITH_NAME("GET" , "/session/:sessionId/uiaElementArray/:reference/withName",UIAElementArray.class),
  ARRAY_WITH_PREDICATE("GET" , "/session/:sessionId/uiaElementArray/:reference/withPredicate",UIAElementArray.class),
  ARRAY_WITH_VALUE_FOR_KEY("GET" , "/session/:sessionId/uiaElementArray/:reference/withValueForKey",UIAElementArray.class),
  
  // UIANavigationBar
  LEFT_BUTTON("GET","/session/:sessionId/uiaElement/:reference/leftButton",UIAButton.class),
  RIGHT_BUTTON("GET","/session/:sessionId/uiaElement/:reference/rightButton",UIAButton.class),
  
  // UIATextField and UIATextView
  SET_VALUE("POST","/session/:sessionId/uiaElement/:reference/setValue",Void.class), 
  
  //UIATableView
  TABLE_GROUPS("GET","/session/:sessionId/uiaElement/:reference/getGroups",UIAElementArray.class), 
  TABLE_CELLS("GET","/session/:sessionId/uiaElement/:reference/getCells",UIAElementArray.class),
  TABLE_VISIBLE_CELLS("GET","/session/:sessionId/uiaElement/:reference/getVisibleCells",UIAElementArray.class),
  
  STATUS("GET","/status",JSONObject.class);
  
  private final String method;
  private final String path;
  private final Class<?> returnType;
  
  
  private WebDriverLikeCommand(String method,String path,Class<?> returnType){
    this.method = method;
    this.path = path;
    this.returnType = returnType;
  }
  
  public String path(){
    return path;
  }
  public String method(){
    return method;
  }
  
  public Class<?> returnType(){
    return returnType;
  }

  public static WebDriverLikeCommand getCommand(String method, String path) {
    for (WebDriverLikeCommand command : values()) {
      if (command.isGenericFormOf(method, path)) {
        return command;
      }
    }
    throw new IOSAutomationException("cannot find command for " + method + ", " + path);
  }

  private boolean isGenericFormOf(String method, String path) {
    String genericPath = this.path;
    String genericMethod = this.method;
    if (!genericMethod.equals(method)) {
      return false;
    }
    String[] genericPieces = genericPath.split("/");
    String[] pieces = path.split("/");
    if (genericPieces.length != pieces.length) {
      return false;
    } else {
      for (int i = 0; i < pieces.length; i++) {
        String genericPiece = genericPieces[i];
        if (genericPiece.startsWith(":")) {
          continue;
        } else {
          if (!genericPiece.equals(pieces[i])) {
            return false;
          }
        }
      }
      return true;
    }
  }

  public int getIndex(String variable) {
    String[] pieces = path.split("/");
    for (int i = 0; i < pieces.length; i++) {
      String piece = pieces[i];
      if (piece.startsWith(":") && piece.equals(variable)) {
        return i;
      }
    }
    throw new IOSAutomationException("cannot find the variable " + variable + " in " + path);
  }
}
