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

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.html5.Location;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAElementArray;
import org.uiautomation.ios.UIAModels.UIAKeyboard;
import org.uiautomation.ios.UIAModels.UIARect;

import java.util.Set;


// TODO freynaud remove the findElements related ones.
public enum WebDriverLikeCommand {
  STATUS("GET", "/status", JSONObject.class),
  NEW_SESSION("POST", "/session", String.class),
  SESSIONS("GET", "/sessions", JSONArray.class),
  GET_SESSION("GET", "/session/:sessionId", JSONObject.class),
  DELETE_SESSION("DELETE", "/session/:sessionId", null),
  SET_TIMEOUT("POST", "/session/:sessionId/timeouts", Void.class),
  //POST /session/:sessionId/timeouts/async_script
  IMPLICIT_WAIT("POST", "/session/:sessionId/timeouts/implicit_wait", Void.class),
  GET_WINDOW_HANDLE("GET", "/session/:sessionId/window_handle", String.class),
  WINDOW_HANDLES("GET", "/session/:sessionId/window_handles", Set.class),
  CURRENT_URL("GET", "/session/:sessionId/url", String.class),
  URL("POST", "/session/:sessionId/url", Void.class),
  FORWARD("POST", "/session/:sessionId/forward", Void.class),
  BACK("POST", "/session/:sessionId/back", Void.class),
  REFRESH("POST", "/session/:sessionId/refresh", Void.class),
  EXECUTE_SCRIPT("POST", "/session/:sessionId/execute", Object.class),
  // POST /session/:sessionId/execute_async
  SCREENSHOT("GET", "/session/:sessionId/screenshot", Void.class),
  //GET /session/:sessionId/ime/available_engines
  //GET /session/:sessionId/ime/active_engine
  //GET /session/:sessionId/ime/activated
  //POST /session/:sessionId/ime/deactivate
  //POST /session/:sessionId/ime/activate
  FRAME("POST", "/session/:sessionId/frame", Void.class),
  WINDOW("POST", "/session/:sessionId/window", Void.class),
  // DELETE  /session/:sessionId/window
  // POST /session/:sessionId/window/:windowHandle/size
  // GET /session/:sessionId/window/:windowHandle/size
  // POST /session/:sessionId/window/:windowHandle/position
  // GET /session/:sessionId/window/:windowHandle/position
  // POST /session/:sessionId/window/:windowHandle/maximize

  //GET /session/:sessionId/cookie
  //POST /session/:sessionId/cookie
  //DELETE /session/:sessionId/cookie
  // DELETE /session/:sessionId/cookie/:name
  TREE_ROOT("GET", "/session/:sessionId/source", JSONObject.class),
  TITLE("GET", "/session/:sessionId/title", String.class),
  ELEMENT_ROOT("POST", "/session/:sessionId/element", UIAElement.class),
  ELEMENTS_ROOT("POST", "/session/:sessionId/elements", UIAElementArray.class),
  // POST /session/:sessionId/element/active
  // GET /session/:sessionId/element/:id
  ELEMENT("POST", "/session/:sessionId/element/:reference/element", UIAElement.class),
  ELEMENTS("POST", "/session/:sessionId/element/:reference/elements", UIAElementArray.class),
  CLICK("POST", "/session/:sessionId/element/:reference/click", Void.class),
  SUBMIT("POST", "/session/:sessionId/element/:reference/submit", Void.class),
  TEXT("GET", "/session/:sessionId/element/:reference/text", String.class),
  SET_VALUE("POST", "/session/:sessionId/element/:reference/value", Void.class),
  SEND_KEYS("POST", "/session/:sessionId/keys", Void.class),
  TAG_NAME("GET", "/session/:sessionId/element/:reference/name", String.class),
  CLEAR("POST", "/session/:sessionId/element/:reference/clear", Void.class),
  SELECTED("GET", "/session/:sessionId/element/:reference/selected", Boolean.class),
  // GET /session/:sessionId/element/:id/enabled
  ATTRIBUTE("GET", "/session/:sessionId/element/:reference/attribute/:name", String.class),
  EQUAL("GET", "/session/:sessionId/element/:reference/equals/:other", Boolean.class),
  DISPLAYED("GET", "/session/:sessionId/element/:reference/displayed", Boolean.class),
  // GET /session/:sessionId/element/:id/location
  // GET /session/:sessionId/element/:id/location_in_view
  // GET /session/:sessionId/element/:id/size
  // GET /session/:sessionId/element/:id/css/:propertyName
  SET_ORIENTATION("POST", "/session/:sessionId/orientation", Void.class),
  GET_ORIENTATION("GET", "/session/:sessionId/orientation", Orientation.class),
  GET_ALERT_TEXT("GET", "/session/:sessionId/alert_text", JSONObject.class),
  SET_ALERT_TEXT("POST", "/session/:sessionId/alert_text", Void.class),
  ACCEPT_ALERT("POST", "/session/:sessionId/accept_alert", Void.class),
  DISMISS_ALERT("POST", "/session/:sessionId/dismiss_alert", Void.class),

  // POST /session/:sessionId/moveto
  // POST /session/:sessionId/click
  // POST /session/:sessionId/buttondown
  // POST /session/:sessionId/buttonup
  // POST /session/:sessionId/doubleclick
  // POST /session/:sessionId/touch/click
  // POST /session/:sessionId/touch/down
  // POST /session/:sessionId/touch/up
  // POST session/:sessionId/touch/move
  // POST session/:sessionId/touch/scroll
  // POST session/:sessionId/touch/scroll ( different params )
  // POST session/:sessionId/touch/doubleclick
  // POST session/:sessionId/touch/longclick
  // POST session/:sessionId/touch/flick
  // POST session/:sessionId/touch/flick ( different params )

  GET_LOCATION("GET", "/session/:sessionId/location", Void.class),
  SET_LOCATION("POST", "/session/:sessionId/location", Location.class),

  // GET /session/:sessionId/local_storage
  // POST /session/:sessionId/local_storage
  // DELETE /session/:sessionId/local_storage
  // GET /session/:sessionId/local_storage/key/:key
  // DELETE /session/:sessionId/local_storage/key/:key
  // GET /session/:sessionId/local_storage/size

  // GET /session/:sessionId/session_storage
  // POST /session/:sessionId/session_storage
  // DELETE /session/:sessionId/session_storage
  // GET /session/:sessionId/session_storage/key/:key
  // DELETE /session/:sessionId/session_storage/key/:key
  // GET /session/:sessionId/session_storage/size

  // POST /session/:sessionId/log
  // GET /session/:sessionId/log

  /*
  * Additional command, not part of the JSON Wire Protocol.
   */
  CONFIGURE("POST", "/session/:sessionId/configure/command/:command", Void.class),
  GET_CONFIGURATION("GET", "/session/:sessionId/configure/command/:command", Object.class),
  GET_TIMEOUT("GET", "/session/:sessionId/timeouts", Integer.class),
  TREE("GET", "/session/:sessionId/element/:reference/source", String.class),
  RECT("GET", "/session/:sessionId/element/:reference/rect", UIARect.class),
  KEYBOARD("GET", "/session/:sessionId/uiaApplication/:reference/keyboard", UIAKeyboard.class),

  // TODO freynaud : move and tap.
  TARGET_TAP("GET", "/session/:sessionId/tap/:reference", Void.class),
  TARGET_RECT("GET", "/session/:sessionId/uiaTarget/:reference/rect", UIARect.class),

  ALERT_CANCEL_BUTTON("GET", "/session/:sessionId/element/:reference/cancel", UIAButton.class),
  ALERT_DEFAULT_BUTTON("GET", "/session/:sessionId/element/:reference/default", UIAButton.class)

  // UIATarget
  /*LOCAL_TARGET("GET", "/session/:sessionId/localTarget", UIATarget.class),

  HOST("GET", "/session/:sessionId/uiaTarget/:reference/host", UIHost.class),

  TARGET_TAP("GET", "/session/:sessionId/tap/:reference", Void.class),


  // UIAHost
  PERFORM_TASK_WITH_PATH_ARGUMENTS_TIMEOUT("POST", "/session/:sessionId/host/:reference", null),

  FONT_MOST_APP("GET", "/session/:sessionId/uiaTarget/:reference/fontMostApp",
                UIAApplication.class),

  // UIAApplication
  MAIN_WINDOW("GET", "/session/:sessionId/uiaApplication/:reference/mainWindow", UIAWindow.class),
  WINDOWS("GET", "/session/:sessionId/uiaTarget/:reference/windows", UIAElementArray.class),

  // UIAElement
  HIT_POINT("GET", "/session/:sessionId/uiaElement/:reference/hitPoint", UIAPoint.class),


  PARENT("GET", "/session/:sessionId/uiaElement/:reference/parent", UIAElement.class),

  ANCESTRY("GET", "/session/:sessionId/uiaElement/:reference/ancestry", UIAElement.class),
  ACTIVITY_INDICATORS("GET", "/session/:sessionId/uiaElement/:reference/activityIndicator",
                      UIAElementArray.class),
  BUTTONS("GET", "/session/:sessionId/uiaElement/:reference/buttons", UIAElementArray.class),
  IMAGES("GET", "/session/:sessionId/uiaElement/:reference/images", UIAElementArray.class),
  LINKS("GET", "/session/:sessionId/uiaElement/:reference/links", UIAElementArray.class),
  NAVIGATION_BAR("GET", "/session/:sessionId/uiaElement/:reference/navigationBar",
                 UIAElement.class),
  NAVIGATION_BARS("GET", "/session/:sessionId/uiaElement/:reference/navigationBars",
                  UIAElementArray.class),

  //UIAKeyboard
  KEYBOARD("GET", "/session/:sessionId/uiaApplication/:reference/keyboard", UIAKeyboard.class),
  KEYBOARD_BUTTONS("GET", "/session/:sessionId/uiaKeyboard/:reference/buttons",
                   UIAElementArray.class),
  KEYBOARD_KEYS("GET", "/session/:sessionId/uiaKeyboard/:reference/keys", UIAElementArray.class),
  TYPE_STRING("POST", "/session/:sessionId/uiaKeyboard/:reference", Void.class),


  IS_STALE("GET", "/session/:sessionId/uiaElement/:reference/isStale", Boolean.class),

  //LABEL("GET" , "/session/:sessionId/uiaElement/:reference/label",String.class),
  //NAME("GET" , "/session/:sessionId/uiaElement/:reference/name",String.class),
  //VALUE("GET" , "/session/:sessionId/uiaElement/:reference/value",String.class),

  WITH_NAME("GET", "/session/:sessionId/uiaElement/:reference/withName", UIAElement.class),
  WITH_PREDICATE("GET", "/session/:sessionId/uiaElement/:reference/withPredicate",
                 UIAElement.class),
  WITH_VALUE_FOR_KEY("GET", "/session/:sessionId/uiaElement/:reference/withValueForKey",
                     UIAElement.class),


  TOUCH_AND_HOLD("POST", "/session/:sessionId/uiaElement/:reference/touchAndHold", Void.class),
  DOUBLE_TAP("POST", "/session/:sessionId/uiaElement/:reference/doubleTap", Void.class),
  TWO_FINGER_TAP("POST", "/session/:sessionId/uiaElement/:reference/twoFingerTap", Void.class),
  TAP_WITH_OPTIONS("POST", "/session/:sessionId/uiaElement/:reference/tapWithOptions", Void.class),
  DRAG_INSIDE_WITH_OPTIONS("POST",
                           "/session/:sessionId/uiaElement/:reference/dragInsideWithOptions",
                           Void.class),
  FLICK_INSIDE_WITH_OPTIONS("POST",
                            "/session/:sessionId/uiaElement/:reference/flickInsideWithOption",
                            Void.class),
  SCROLL_TO_VISIBLE("POST", "/session/:sessionId/uiaElement/:reference/scrollToVisible",
                    Void.class),
  ROTATE_WITH_OPTIONS("POST", "/session/:sessionId/uiaElement/:reference/rotateWithOptions",
                      Void.class),
  PINCH_CLOSE("POST", "/session/:sessionId/pinchClose", Void.class),


  // UIAElementArray
  GET("GET", "/session/:sessionId/uiaElementArray/:reference", UIAElement.class),
  FIRST_WITH_NAME("GET", "/session/:sessionId/uiaElementArray/:reference/firstWithName",
                  UIAElement.class),
  FIRST_WITH_PREDICATE("GET", "/session/:sessionId/uiaElementArray/:reference/firstWithPredicate",
                       UIAElement.class),
  FIRST_WITH_VALUE_FOR_KEY("GET",
                           "/session/:sessionId/uiaElementArray/:reference/firstWithValueForKey",
                           UIAElement.class),
  ARRAY_WITH_NAME("GET", "/session/:sessionId/uiaElementArray/:reference/withName",
                  UIAElementArray.class),
  ARRAY_WITH_PREDICATE("GET", "/session/:sessionId/uiaElementArray/:reference/withPredicate",
                       UIAElementArray.class),
  ARRAY_WITH_VALUE_FOR_KEY("GET", "/session/:sessionId/uiaElementArray/:reference/withValueForKey",
                           UIAElementArray.class),

  // UIANavigationBar
  LEFT_BUTTON("GET", "/session/:sessionId/uiaElement/:reference/leftButton", UIAButton.class),
  RIGHT_BUTTON("GET", "/session/:sessionId/uiaElement/:reference/rightButton", UIAButton.class),

  // UIATextField and UIATextView

  //UIATableView
  TABLE_GROUPS("GET", "/session/:sessionId/uiaElement/:reference/getGroups", UIAElementArray.class),
  TABLE_CELLS("GET", "/session/:sessionId/uiaElement/:reference/getCells", UIAElementArray.class),
  TABLE_VISIBLE_CELLS("GET", "/session/:sessionId/uiaElement/:reference/getVisibleCells",
                      UIAElementArray.class),


  ,*/;


  private final String method;
  private final String path;
  private final Class<?> returnType;


  private WebDriverLikeCommand(String method, String path, Class<?> returnType) {
    this.method = method;
    this.path = path;
    this.returnType = returnType;
  }

  public String path() {
    return path;
  }

  public String method() {
    return method;
  }

  public Class<?> returnType() {
    return returnType;
  }

  public static WebDriverLikeCommand getCommand(String method, String path) {
    for (WebDriverLikeCommand command : values()) {
      if (command.isGenericFormOf(method, path)) {
        return command;
      }
    }
    throw new WebDriverException("cannot find command for " + method + ", " + path);
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
    throw new WebDriverException("cannot find the variable " + variable + " in " + path);
  }

  public boolean isSessionLess() {
    return !path.contains(":sessionId");
  }
}
