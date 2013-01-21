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

package org.uiautomation.ios.server;

import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.command.BaseNativeCommandHandler;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;
import org.uiautomation.ios.server.command.Handler;
import org.uiautomation.ios.server.command.NotImplementedNativeHandler;
import org.uiautomation.ios.server.command.NotImplementedWebHandler;
import org.uiautomation.ios.server.command.uiautomation.*;
import org.uiautomation.ios.server.command.uiautomation.ClearNHandler;
import org.uiautomation.ios.server.command.uiautomation.DefaultUIAScriptNHandler;
import org.uiautomation.ios.server.command.uiautomation.ExecuteScriptNHandler;
import org.uiautomation.ios.server.command.uiautomation.FindElementNHandler;
import org.uiautomation.ios.server.command.uiautomation.FindElementsRoot;
import org.uiautomation.ios.server.command.uiautomation.GetAttributeNHandler;
import org.uiautomation.ios.server.command.uiautomation.GetCapabilitiesNHandler;
import org.uiautomation.ios.server.command.uiautomation.GetConfigurationNHandler;
import org.uiautomation.ios.server.command.uiautomation.GetCurrentContextNHandler;
import org.uiautomation.ios.server.command.uiautomation.GetElementSizeNHandler;
import org.uiautomation.ios.server.command.uiautomation.GetOrientationNHandler;
import org.uiautomation.ios.server.command.uiautomation.GetSessionsNHandler;
import org.uiautomation.ios.server.command.uiautomation.GetTimeoutNHandler;
import org.uiautomation.ios.server.command.uiautomation.GetWindowHandlesNHandler;
import org.uiautomation.ios.server.command.uiautomation.LogElementTreeNHandler;
import org.uiautomation.ios.server.command.uiautomation.NewSessionNHandler;
import org.uiautomation.ios.server.command.uiautomation.SendKeysNHandler;
import org.uiautomation.ios.server.command.uiautomation.ServerStatusNHandler;
import org.uiautomation.ios.server.command.uiautomation.SetConfigurationNHandler;
import org.uiautomation.ios.server.command.uiautomation.SetCurrentContextNHandler;
import org.uiautomation.ios.server.command.uiautomation.SetImplicitWaitTimeoutNHandler;
import org.uiautomation.ios.server.command.uiautomation.SetLocationNHandler;
import org.uiautomation.ios.server.command.uiautomation.SetOrientationNHandler;
import org.uiautomation.ios.server.command.uiautomation.SetTimeoutNHandler;
import org.uiautomation.ios.server.command.uiautomation.SetValueNHandler;
import org.uiautomation.ios.server.command.uiautomation.StopSessionNHandler;
import org.uiautomation.ios.server.command.uiautomation.TakeScreenshotNHandler;
import org.uiautomation.ios.server.command.web.BackHandler;
import org.uiautomation.ios.server.command.web.ClearHandler;
import org.uiautomation.ios.server.command.web.ClickHandler;
import org.uiautomation.ios.server.command.web.ExecuteScriptHandler;
import org.uiautomation.ios.server.command.web.FindElementHandler;
import org.uiautomation.ios.server.command.web.FindElementsHandler;
import org.uiautomation.ios.server.command.web.ForwardHandler;
import org.uiautomation.ios.server.command.web.GetAttributeHandler;
import org.uiautomation.ios.server.command.web.GetHandler;
import org.uiautomation.ios.server.command.web.GetPageSourceHandler;
import org.uiautomation.ios.server.command.web.GetTagNameHandler;
import org.uiautomation.ios.server.command.web.GetTextHandler;
import org.uiautomation.ios.server.command.web.GetTitleHandler;
import org.uiautomation.ios.server.command.web.GetURL;
import org.uiautomation.ios.server.command.web.IsDisplayedHanlder;
import org.uiautomation.ios.server.command.web.IsEqualHandler;
import org.uiautomation.ios.server.command.web.IsSelectedHandler;
import org.uiautomation.ios.server.command.web.RefreshHandler;
import org.uiautomation.ios.server.command.web.SetFrameHandler;
import org.uiautomation.ios.server.command.web.SetImplicitWaitTimeoutHandler;
import org.uiautomation.ios.server.command.web.SetTimeoutHandler;
import org.uiautomation.ios.server.command.web.SetValueHandler;
import org.uiautomation.ios.server.command.web.SubmitHandler;

import java.lang.reflect.Constructor;
import java.util.Iterator;

public enum CommandMapping {

  STATUS(ServerStatusNHandler.class),
  NEW_SESSION(NewSessionNHandler.class),
  GET_SESSION(GetCapabilitiesNHandler.class),
  SESSIONS(GetSessionsNHandler.class),
  DELETE_SESSION(StopSessionNHandler.class),
  SET_TIMEOUT(SetTimeoutNHandler.class, SetTimeoutHandler.class),
  IMPLICIT_WAIT(SetImplicitWaitTimeoutNHandler.class, SetImplicitWaitTimeoutHandler.class),
  GET_TIMEOUT(GetTimeoutNHandler.class),

  CONFIGURE(SetConfigurationNHandler.class),
  GET_CONFIGURATION(GetConfigurationNHandler.class),

  WINDOW_HANDLES(GetWindowHandlesNHandler.class),
  WINDOW(SetCurrentContextNHandler.class),
  FRAME(NotImplementedNativeHandler.class, SetFrameHandler.class),
  GET_WINDOW_HANDLE(GetCurrentContextNHandler.class),
  TITLE(null, null, GetTitleHandler.class),
  URL(NotImplementedNativeHandler.class, GetHandler.class),
  CURRENT_URL(NotImplementedNativeHandler.class, GetURL.class),
  BACK(NotImplementedNativeHandler.class, BackHandler.class),
  FORWARD(NotImplementedNativeHandler.class, ForwardHandler.class),
  REFRESH(NotImplementedNativeHandler.class, RefreshHandler.class),
  SUBMIT(NotImplementedNativeHandler.class, SubmitHandler.class),
  TAG_NAME(".type()", DefaultUIAScriptNHandler.class, GetTagNameHandler.class),
  SEND_KEYS(SendKeysNHandler.class),
  EXECUTE_SCRIPT(ExecuteScriptNHandler.class, ExecuteScriptHandler.class),
  EQUAL(NotImplementedNativeHandler.class, IsEqualHandler.class),
  // UIATarget
  TREE(LogElementTreeNHandler.class),
  TREE_ROOT(LogElementTreeNHandler.class, GetPageSourceHandler.class),

  TARGET_RECT(".rect()"),
  TARGET_TAP(".tap({x::x,y::y})"),
  SET_ORIENTATION(SetOrientationNHandler.class),
  GET_ORIENTATION(GetOrientationNHandler.class),

  SCREENSHOT(TakeScreenshotNHandler.class),

  //FONT_MOST_APP(".frontMostApp()"),
  SELECTED((String) null, IsSelectedHandler.class),

  // UIAApplication
  //MAIN_WINDOW(".mainWindow()"),
  //WINDOWS(".windows()"),
  KEYBOARD(".keyboard()"),
  //KEYBOARD_KEYS(".keys()"),
  //KEYBOARD_BUTTONS(".buttons()"),
  //TYPE_STRING(".typeString(:string)"),
  // UIAHost
  //PERFORM_TASK_WITH_PATH_ARGUMENTS_TIMEOUT(null),

  // UIAElement
  //HIT_POINT(NotImplementedNativeHandler.class, NotImplementedWebHandler.class),
  RECT(GetElementSizeNHandler.class, NotImplementedWebHandler.class),

  //PARENT(NotImplementedNativeHandler.class, NotImplementedWebHandler.class),

  ELEMENT_ROOT(FindElementNHandler.class, FindElementHandler.class),
  ELEMENTS_ROOT(FindElementsRoot.class, FindElementsHandler.class),

  ELEMENT(FindElementNHandler.class, FindElementHandler.class),
  ELEMENTS(FindElementsRoot.class, FindElementsHandler.class),

  //ELEMENT(".element(:depth,:criteria)"),
  //ELEMENTS(".elements2(:depth,:criteria)"),
  //ANCESTRY(NotImplementedNativeHandler.class, NotImplementedWebHandler.class),

  DISPLAYED(".isVisible()", DefaultUIAScriptNHandler.class, IsDisplayedHanlder.class),
  //IS_STALE(".isStale()"),

  //LABEL(".label()"),
  //NAME(".name()"),
  //VALUE(".value()"),
  ATTRIBUTE(GetAttributeNHandler.class, GetAttributeHandler.class),
  TEXT(null, null, GetTextHandler.class),
  //WITH_NAME(".withName(:name)"),
  //WITH_PREDICATE(".withPredicate(PredicateString predicateString)"),
  //WITH_VALUE_FOR_KEY(".withValueForKey(Object value,String key)"),

  CLICK(".tap()", ClickHandler.class),

  //GET_LOCATION(GetLocationNHandler.class),
  SET_LOCATION(SetLocationNHandler.class),

  //TOUCH_AND_HOLD(".touchAndHold(:duration)"),
  //DOUBLE_TAP(".doubleTap()"),
  //TWO_FINGER_TAP(".twoFingerTap()"),
  //TAP_WITH_OPTIONS(""),
  //DRAG_INSIDE_WITH_OPTIONS(""),
  //FLICK_INSIDE_WITH_OPTIONS(""),
  //SCROLL_TO_VISIBLE(".scrollToVisible()"),
  //ROTATE_WITH_OPTIONS(NotImplementedNativeHandler.class, NotImplementedWebHandler.class),
  //PINCH_CLOSE(PinchCloseNHandler.class, NotImplementedWebHandler.class),

  // UIAElementArray
  //GET(".toArray()[:index]"),
  //FIRST_WITH_NAME(".firstWithName(:name)"),
  //FIRST_WITH_PREDICATE(".firstWithPredicate()"),
  //FIRST_WITH_VALUE_FOR_KEY(NotImplementedHandler.class),
  //ARRAY_WITH_NAME(".withName(:name)"),
  //ARRAY_WITH_PREDICATE(NotImplementedHandler.class),
  //ARRAY_WITH_VALUE_FOR_KEY(NotImplementedHandler.class),

  //UIANavigationBar
  //LEFT_BUTTON(".leftButton()"),
  //RIGHT_BUTTON(".rightButton()"),

  // UIATextField
  SET_VALUE(SetValueNHandler.class, SetValueHandler.class),
  CLEAR(ClearNHandler.class, ClearHandler.class),

  //UIATableView
  //TABLE_GROUPS(".groups()"),
  //TABLE_CELLS(".cells()"),
  //TABLE_VISIBLE_CELLS(".visibleCells()"),
  GET_ALERT_TEXT(GetAlertTextNHandler.class),
  ACCEPT_ALERT(NotImplementedNativeHandler.class, AcceptAlertHandler.class),
  DISMISS_ALERT(NotImplementedNativeHandler.class, DismissAlertHandler.class),
  SET_ALERT_TEXT(NotImplementedNativeHandler.class, SetAlertTextHandler.class),
  ALERT_CANCEL_BUTTON(".cancelButton()"),
  ALERT_DEFAULT_BUTTON(".defaultButton()"),;


  private WebDriverLikeCommand command;
  private final Class<? extends BaseNativeCommandHandler> nativeHandlerClass;
  private final Class<? extends BaseWebCommandHandler> webHandlerClass;
  private final String nativeJSMethod;

  private CommandMapping(String jsMethod, Class<? extends BaseWebCommandHandler> webHandlerClass) {
    this.command = WebDriverLikeCommand.valueOf(this.name());
    this.nativeHandlerClass = DefaultUIAScriptNHandler.class;
    this.nativeJSMethod = jsMethod;
    this.webHandlerClass = webHandlerClass;
  }


  private CommandMapping(String jsMethod) {
    this.command = WebDriverLikeCommand.valueOf(this.name());
    this.nativeHandlerClass = DefaultUIAScriptNHandler.class;
    this.nativeJSMethod = jsMethod;
    this.webHandlerClass = null;
  }


  private CommandMapping(String nativeJSMethod,
                         Class<? extends BaseNativeCommandHandler> nativeHandlerClass,
                         Class<? extends BaseWebCommandHandler> webHandlerClass) {
    this.command = WebDriverLikeCommand.valueOf(this.name());
    this.nativeHandlerClass = nativeHandlerClass;
    this.webHandlerClass = webHandlerClass;
    this.nativeJSMethod = nativeJSMethod;
  }

  private CommandMapping(Class<? extends BaseNativeCommandHandler> nativeHandlerClass,
                         Class<? extends BaseWebCommandHandler> webHandlerClass) {
    this.command = WebDriverLikeCommand.valueOf(this.name());
    this.nativeHandlerClass = nativeHandlerClass;
    this.webHandlerClass = webHandlerClass;
    this.nativeJSMethod = null;

  }

  private CommandMapping(Class<? extends BaseNativeCommandHandler> handlerClass) {
    this.command = WebDriverLikeCommand.valueOf(this.name());
    this.nativeHandlerClass = handlerClass;
    this.webHandlerClass = null;
    this.nativeJSMethod = null;
  }


  public static CommandMapping get(WebDriverLikeCommand wdlc) {
    for (CommandMapping cm : values()) {
      if (cm.command == wdlc) {
        return cm;
      }
    }
    throw new RuntimeException("not mapped : " + wdlc);
  }

  public String jsMethod(JSONObject payload, IOSApplication aut) {
    if (payload != null) {
      String res = nativeJSMethod;
      Iterator<String> iter = payload.keys();
      while (iter.hasNext()) {
        String key = iter.next();
        Object value = payload.opt(key);
        res = res.replace(":" + key, value.toString());
      }
      return res;
    } else {
      return nativeJSMethod;
    }
  }


  public Handler createHandler(IOSDriver driver, WebDriverLikeRequest request) throws Exception {
    boolean isNative = isNative(driver, request);
    Class<?> clazz;

    if (isNative) {
      clazz = nativeHandlerClass;
    } else {
      clazz = webHandlerClass != null ? webHandlerClass : nativeHandlerClass;
    }

    if (clazz == null) {
      throw new RuntimeException("handler NI");
    }

    Object[] args = new Object[]{driver, request};
    Class<?>[] argsClass = new Class[]{IOSDriver.class, WebDriverLikeRequest.class};

    Constructor<?> c = clazz.getConstructor(argsClass);
    Handler handler = (Handler) c.newInstance(args);
    return handler;

  }

  private boolean isNative(IOSDriver driver, WebDriverLikeRequest request) {
    // if there a force flag in the request.
    if (request.getPayload().has("native")) {
      return request.getPayload().optBoolean("native");
    }

    // else, get it from the current mode.
    boolean isNative = true;
    WebDriverLikeCommand command = request.getGenericCommand();

    if (!command.isSessionLess()) {
      ServerSideSession sss = driver.getSession(request.getSession());
      isNative = sss.getMode() == WorkingMode.Native;
    }
    return isNative;
  }

}
