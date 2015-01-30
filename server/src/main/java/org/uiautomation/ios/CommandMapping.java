/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
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

package org.uiautomation.ios;

import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.command.BaseNativeCommandHandler;
import org.uiautomation.ios.command.BaseWebCommandHandler;
import org.uiautomation.ios.command.Handler;
import org.uiautomation.ios.command.NotImplementedNativeHandler;
import org.uiautomation.ios.command.NotImplementedWebHandler;
import org.uiautomation.ios.command.uiautomation.*;
import org.uiautomation.ios.command.web.*;
import org.uiautomation.ios.server.command.ExecuteHostHandler;

import java.lang.reflect.Constructor;
import java.util.Iterator;

public enum CommandMapping {

  STATUS(ServerStatusNHandler.class),
  NEW_SESSION(NewSessionNHandler.class),
  GET_SESSION(GetCapabilitiesNHandler.class),
  SESSIONS(GetSessionsNHandler.class),
  DELETE_SESSION(QuitSessionNHandler.class),
  SET_TIMEOUT(SetTimeoutNHandler.class, SetTimeoutHandler.class),
  SET_SCRIPT_TIMEOUT(SetScriptTimeoutNHandler.class, SetScriptTimeoutHandler.class),
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
  EXECUTE_SCRIPT(ExecuteScriptNHandler.class, ExecuteScriptHandler.class),
  EXECUTE_ASYNC_SCRIPT(ExecuteAsyncScriptNHandler.class, ExecuteAsyncScriptHandler.class),
  EQUAL(NotImplementedNativeHandler.class, IsEqualHandler.class),
  SOURCE(LogElementTreeNHandler.class, GetPageSourceHandler.class),
  TREE(LogElementTreeNHandler.class),
  TREE_ROOT(LogElementTreeNHandler.class),
  GET_COOKIE(NotImplementedNativeHandler.class, GetCookiesHandler.class),
  DELETE_COOKIE(NotImplementedNativeHandler.class, DeleteAllCookiesHandler.class),
  DELETE_COOKIE_BY_NAME(NotImplementedNativeHandler.class, DeleteCookieByNameHandler.class),
  TARGET_RECT(".rect()"),
  TARGET_TAP(".tap({x::x,y::y})"),
  SET_ORIENTATION(SetOrientationNHandler.class),
  GET_ORIENTATION(GetOrientationNHandler.class),
  DRAG_FROM_TO_FOR_DURATION(DragFromToForDurationNHandler.class),
  PINCH_CLOSE_FROM_TO_FOR_DURATION(PinchCloseNHandler.class, NotImplementedWebHandler.class),
  PINCH_OPEN_FROM_TO_FOR_DURATION(PinchOpenNHandler.class, NotImplementedWebHandler.class),



  WINDOW_SIZE(GetScreenSizeNHandler.class, GetPageSizeHandler.class),
  GET_SCREENRECT(GetScreenSizeNHandler.class),

  SCREENSHOT(TakeScreenshotNHandler.class),

  SELECTED((String) null, IsSelectedHandler.class),

  // UIAApplication
  KEYBOARD(".keyboard()"),
  DEACTIVATE_APP(DeactivateAppHandler.class),

  RECT(GetElementSizeNHandler.class, NotImplementedWebHandler.class),

  ELEMENT_ROOT(FindElementNHandler.class, FindElementHandler.class),
  ELEMENTS_ROOT(FindElementsRoot.class, FindElementsHandler.class),

  ELEMENT(FindElementNHandler.class, FindElementHandler.class),
  ELEMENTS(FindElementsRoot.class, FindElementsHandler.class),

  DISPLAYED(IsVisibleNHandler.class, IsDisplayedHandler.class),
  ENABLED(IsEnabledNHandler.class, IsEnabledHandler.class),
  LOCATION(null, null, GetLocationHandler.class),
  ELEMENT_SIZE(null, GetElementSizeNHandler.class, GetElementSizeHandler.class),

  LOG(NotImplementedNativeHandler.class, LogHandler.class),
  LOG_TYPES(NotImplementedNativeHandler.class, LogTypesHandler.class),

  SCROLL(ScrollNHandler.class),
  LONG_TAP(LongTapNHandler.class),
  TAP(TapNHandler.class),
  DOUBLE_TAP(DoubleTapNHandler.class),
  ATTRIBUTE(GetAttributeNHandler.class, GetAttributeHandler.class),
  TEXT(null, null, GetTextHandler.class),
  CSS(NotImplementedNativeHandler.class, CssPropertyHandler.class),

  CLICK(".tap()", ClickHandler.class),

  SET_LOCATION(SetLocationNHandler.class),

  NATIVE_TOUCH_AND_HOLD(TouchAndHoldNHandler.class),
  NATIVE_DOUBLE_TAP(".doubleTap()"),
  TWO_FINGER_TAP(".twoFingerTap()"),
  FLICK_INSIDE_WITH_OPTIONS(FlickInsideWithOptionsNHandler.class),
  ELEMENT_SCROLL(ElementScrollNHandler.class),

  //TouchScreen
  FLICK(FlickNHandler.class),
  TOUCH_DOWN(TouchDownHandler.class),
  TOUCH_UP(TouchUpHandler.class),
  TOUCH_MOVE(TouchMoveHandler.class),

  // UIATextField
  SET_VALUE(SetValueNHandler.class, SetValueHandler.class),
  SEND_KEYS(SendKeysNHandler.class),

  CLEAR(ClearNHandler.class, ClearHandler.class),

  //UIATableView
  TABLE_GROUPS(".groups()"),
  TABLE_CELLS(".cells()"),
  TABLE_VISIBLE_CELLS(".visibleCells()"),
  GET_ALERT_TEXT(GetAlertTextNHandler.class),
  ACCEPT_ALERT(AcceptAlertHandler.class),
  DISMISS_ALERT(DismissAlertHandler.class),
  SET_ALERT_TEXT(SetAlertTextHandler.class),
  ALERT_CANCEL_BUTTON(".cancelButton()"),
  ALERT_DEFAULT_BUTTON(".defaultButton()"),

  //UIAPicker
  PICKER_WHEELS(".wheels()"),

  //UIAPickerWheels
  PICKER_WHEEL_VALUES(".values()"),
  PICKER_WHEEL_SET_VALUE(SetPickerWheelValueNHandler.class),

  //UIASwitch
  NATIVE_SWITCH_SET_VALUE(SetSwitchValueNHandler.class),

  //UIASlider
  SLIDER_DRAG_TO_VALUE(SetSliderPosNHandler.class);


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

  public String jsMethod(JSONObject payload) {
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


  public Handler createHandler(IOSServerManager driver, WebDriverLikeRequest request)
      throws Exception {
    if (nativeHandlerClass == ExecuteScriptNHandler.class) {
      // TEMP: redirect "host: " scripts to ExecuteHostHandler
      String script = request.getPayload().getString("script");
      if (script != null && script.startsWith("host: ")) {
         return new ExecuteHostHandler(driver, request);
      }
    }

    boolean isNative = isNative(driver, request);
    Class<?> clazz;

    if (isNative) {
      clazz = nativeHandlerClass;
    } else {
      clazz = webHandlerClass != null ? webHandlerClass : nativeHandlerClass;
    }

    if (clazz == null) {
      throw new RuntimeException("handler NYI: " + name());
    }

    Object[] args = new Object[]{driver, request};
    Class<?>[] argsClass = new Class[]{IOSServerManager.class, WebDriverLikeRequest.class};

    Constructor<?> c = clazz.getConstructor(argsClass);
    Handler handler = (Handler) c.newInstance(args);
    return handler;

  }

  private boolean isNative(IOSServerManager driver, WebDriverLikeRequest request) {
    // if there a force flag in the request.
    if (request.getPayload().has("native")) {
      return request.getPayload().optBoolean("native");
    }

    // else, get it from the current mode.
    boolean isNative = true;
    WebDriverLikeCommand command = request.getGenericCommand();

    if (!command.isSessionLess()) {
      ServerSideSession sss = driver.getSession(request.getSession());
      isNative = sss.getDualDriver().getWorkingMode() == WorkingMode.Native;
    }
    return isNative;
  }

}
