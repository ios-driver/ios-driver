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

import java.lang.reflect.Constructor;
import java.util.Iterator;

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
import org.uiautomation.ios.server.command.uiautomation.AttributeCommand;
import org.uiautomation.ios.server.command.uiautomation.ConfigurationGetter;
import org.uiautomation.ios.server.command.uiautomation.ConfigurationSetter;
import org.uiautomation.ios.server.command.uiautomation.DefaultUIAScriptHandler;
import org.uiautomation.ios.server.command.uiautomation.FindElementRoot;
import org.uiautomation.ios.server.command.uiautomation.FindElementsRoot;
import org.uiautomation.ios.server.command.uiautomation.GetCapabilitiesCommandHandler;
import org.uiautomation.ios.server.command.uiautomation.GetCurrentContext;
import org.uiautomation.ios.server.command.uiautomation.GetElementSizeHandler;
import org.uiautomation.ios.server.command.uiautomation.GetSessions;
import org.uiautomation.ios.server.command.uiautomation.GetTimeoutCommandHandler;
import org.uiautomation.ios.server.command.uiautomation.GetWindowHandlesCommandHandler;
import org.uiautomation.ios.server.command.uiautomation.LocalTarget;
import org.uiautomation.ios.server.command.uiautomation.LogElementTree;
import org.uiautomation.ios.server.command.uiautomation.NewSession;
import org.uiautomation.ios.server.command.uiautomation.PinchCloseHandler;
import org.uiautomation.ios.server.command.uiautomation.ServerStatus;
import org.uiautomation.ios.server.command.uiautomation.SetCurrentContext;
import org.uiautomation.ios.server.command.uiautomation.SetImplicitWaitTimeout;
import org.uiautomation.ios.server.command.uiautomation.SetTimeoutCommandHandler;
import org.uiautomation.ios.server.command.uiautomation.SetValueNativeHandler;
import org.uiautomation.ios.server.command.uiautomation.StopSession;
import org.uiautomation.ios.server.command.uiautomation.TakeScreenshot;
import org.uiautomation.ios.server.command.web.BackHandler;
import org.uiautomation.ios.server.command.web.ClearHandler;
import org.uiautomation.ios.server.command.web.ClickHandler;
import org.uiautomation.ios.server.command.web.ExecuteScriptHandler;
import org.uiautomation.ios.server.command.web.FindElementHandler;
import org.uiautomation.ios.server.command.web.FindElementsHandler;
import org.uiautomation.ios.server.command.web.ForwardHandler;
import org.uiautomation.ios.server.command.web.GetAttributeHandler;
import org.uiautomation.ios.server.command.web.GetHandler;
import org.uiautomation.ios.server.command.web.GetTagNameHandler;
import org.uiautomation.ios.server.command.web.GetTextHandler;
import org.uiautomation.ios.server.command.web.GetTitleHandler;
import org.uiautomation.ios.server.command.web.GetURL;
import org.uiautomation.ios.server.command.web.IsDisplayedHanlder;
import org.uiautomation.ios.server.command.web.IsEqualHandler;
import org.uiautomation.ios.server.command.web.IsSelectedHandler;
import org.uiautomation.ios.server.command.web.RefreshHandler;
import org.uiautomation.ios.server.command.web.SetFrameHandler;
import org.uiautomation.ios.server.command.web.SetTimeoutHandler;
import org.uiautomation.ios.server.command.web.SetValueHandler;
import org.uiautomation.ios.server.command.web.SubmitHandler;

public enum CommandMapping {

  NEW_SESSION(NewSession.class),
  GET_SESSION(GetCapabilitiesCommandHandler.class),
  SESSIONS(GetSessions.class),
  DELETE_SESSION(StopSession.class),
  SET_TIMEOUT(SetTimeoutCommandHandler.class,SetTimeoutHandler.class),
  IMPLICIT_WAIT(SetImplicitWaitTimeout.class,NotImplementedWebHandler.class),
  GET_TIMEOUT(GetTimeoutCommandHandler.class),
  
  CONFIGURE(ConfigurationSetter.class),
  GET_CONFIGURATION(ConfigurationGetter.class),
  
  WINDOW_HANDLES(GetWindowHandlesCommandHandler.class),
  WINDOW(SetCurrentContext.class,NotImplementedWebHandler.class),
  FRAME(NotImplementedNativeHandler.class,SetFrameHandler.class),
  GET_WINDOW_HANDLE(GetCurrentContext.class),
  TITLE(null,null,GetTitleHandler.class),
  URL(NotImplementedNativeHandler.class,GetHandler.class),
  CURRENT_URL(NotImplementedNativeHandler.class,GetURL.class),
  BACK(NotImplementedNativeHandler.class,BackHandler.class),
  FORWARD(NotImplementedNativeHandler.class,ForwardHandler.class),
  REFRESH(NotImplementedNativeHandler.class,RefreshHandler.class),
  SUBMIT(NotImplementedNativeHandler.class,SubmitHandler.class),
  TAG_NAME(".type()", DefaultUIAScriptHandler.class,GetTagNameHandler.class),

  EXECUTE_SCRIPT(NotImplementedNativeHandler.class,ExecuteScriptHandler.class),
  EQUAL(NotImplementedNativeHandler.class,IsEqualHandler.class),
  // UIATarget
  LOCAL_TARGET(LocalTarget.class),
  //HOST(NotImplementedHandler.class),
  TREE(LogElementTree.class),
  TREE_ROOT(LogElementTree.class),
  
  TARGET_RECT(".rect()"),
  TARGET_TAP(".tap({x::x,y::y})"),
  SET_ORIENTATION(".setDeviceOrientation(:orientation)"),
  
  SCREENSHOT(TakeScreenshot.class),
  
  FONT_MOST_APP(".frontMostApp()"),
  SELECTED((String)null,IsSelectedHandler.class),
 
  // UIAApplication
  MAIN_WINDOW(".mainWindow()"),
  WINDOWS(".windows()"),
  KEYBOARD(".keyboard2()"), 
  KEYBOARD_KEYS(".keys()"), 
  KEYBOARD_BUTTONS (".buttons()"),
  TYPE_STRING(".typeString(:string)"),
    // UIAHost
   //PERFORM_TASK_WITH_PATH_ARGUMENTS_TIMEOUT(null),
  
 
  // UIAElement
  HIT_POINT(NotImplementedNativeHandler.class,NotImplementedWebHandler.class),
  RECT(GetElementSizeHandler.class,NotImplementedWebHandler.class),
  
  PARENT(NotImplementedNativeHandler.class,NotImplementedWebHandler.class),
  

  ELEMENT_ROOT(FindElementRoot.class,FindElementHandler.class),
  ELEMENTS_ROOT(FindElementsRoot.class,FindElementsHandler.class),
  
  ELEMENT(FindElementRoot.class,FindElementHandler.class),
  ELEMENTS(FindElementsRoot.class,FindElementsHandler.class),
  
  //ELEMENT(".element(:depth,:criteria)"),
  //ELEMENTS(".elements2(:depth,:criteria)"),
  ANCESTRY(NotImplementedNativeHandler.class,NotImplementedWebHandler.class),

  DISPLAYED(".isVisible()",DefaultUIAScriptHandler.class,IsDisplayedHanlder.class),
  IS_STALE(".isStale()"),
  
  
  //LABEL(".label()"),
  //NAME(".name()"),
  //VALUE(".value()"),
  ATTRIBUTE(AttributeCommand.class,GetAttributeHandler.class),
  TEXT(null,null,GetTextHandler.class),
  //WITH_NAME(".withName(:name)"),
  //WITH_PREDICATE(".withPredicate(PredicateString predicateString)"),
  //WITH_VALUE_FOR_KEY(".withValueForKey(Object value,String key)"),
  
  
  CLICK(".tap2()",ClickHandler.class),
  TOUCH_AND_HOLD(".touchAndHold(:duration)"),
  DOUBLE_TAP(".doubleTap()"),
  TWO_FINGER_TAP(".twoFingerTap()"),
  TAP_WITH_OPTIONS(""),
  DRAG_INSIDE_WITH_OPTIONS(""),
  FLICK_INSIDE_WITH_OPTIONS(""),
  SCROLL_TO_VISIBLE(".scrollToVisible()"),
  ROTATE_WITH_OPTIONS(NotImplementedNativeHandler.class,NotImplementedWebHandler.class),
  PINCH_CLOSE(PinchCloseHandler.class,NotImplementedWebHandler.class),
  
  // UIAElementArray
  GET(".toArray()[:index]"),
  FIRST_WITH_NAME(".firstWithName(:name)"),
  FIRST_WITH_PREDICATE(".firstWithPredicate()"),
  //FIRST_WITH_VALUE_FOR_KEY(NotImplementedHandler.class),
  //ARRAY_WITH_NAME(".withName(:name)"),
  //ARRAY_WITH_PREDICATE(NotImplementedHandler.class),
  //ARRAY_WITH_VALUE_FOR_KEY(NotImplementedHandler.class),

  //UIANavigationBar
  LEFT_BUTTON(".leftButton()"),
  RIGHT_BUTTON(".rightButton()"),
  
  // UIATextField
  SET_VALUE(SetValueNativeHandler.class,SetValueHandler.class),
  CLEAR(NotImplementedNativeHandler.class,ClearHandler.class),
  
  //UIATableView
  TABLE_GROUPS(".groups()"),
  TABLE_CELLS(".cells()"),
  TABLE_VISIBLE_CELLS(".visibleCells()"),
  
  ALERT_CANCEL_BUTTON(".cancelButton2()"),
  ALERT_DEFAULT_BUTTON(".defaultButton2()"),
  
  STATUS(ServerStatus.class);


  private WebDriverLikeCommand command;
  private final Class<? extends BaseNativeCommandHandler> nativeHandlerClass;
  private final Class<? extends BaseWebCommandHandler> webHandlerClass;
  private final String nativeJSMethod;

  private CommandMapping(String jsMethod, Class<? extends BaseWebCommandHandler> webHandlerClass) {
    this.command = WebDriverLikeCommand.valueOf(this.name());
    this.nativeHandlerClass = DefaultUIAScriptHandler.class;
    this.nativeJSMethod = jsMethod;
    this.webHandlerClass = webHandlerClass;
  }
  
  
  private CommandMapping(String jsMethod) {
    this.command = WebDriverLikeCommand.valueOf(this.name());
    this.nativeHandlerClass = DefaultUIAScriptHandler.class;
    this.nativeJSMethod = jsMethod;
    this.webHandlerClass = null;
  }
  
 
  private CommandMapping(String nativeJSMethod,Class<? extends BaseNativeCommandHandler> nativeHandlerClass, 
      Class<? extends BaseWebCommandHandler> webHandlerClass) {
    this.command = WebDriverLikeCommand.valueOf(this.name());
    this.nativeHandlerClass = nativeHandlerClass;
    this.webHandlerClass = webHandlerClass;
    this.nativeJSMethod = nativeJSMethod;
  }

  private CommandMapping(Class<? extends BaseNativeCommandHandler> nativeHandlerClass, Class<? extends BaseWebCommandHandler> webHandlerClass) {
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
    boolean isNative = true;
    WebDriverLikeCommand command = request.getGenericCommand();
    
    if (!command.isSessionLess()) {
      ServerSideSession sss = driver.getSession(request.getSession());
      isNative = sss.getMode() == WorkingMode.Native;
    }

    Class<?> clazz;

    if (isNative) {
      clazz = nativeHandlerClass;
    } else {
      clazz = webHandlerClass != null ? webHandlerClass : nativeHandlerClass;
    }


    if (clazz == null) {
      throw new RuntimeException("handler NI");
    }

    Object[] args = new Object[] {driver, request};
    Class<?>[] argsClass = new Class[] {IOSDriver.class, WebDriverLikeRequest.class};

    Constructor<?> c = clazz.getConstructor(argsClass);
    Handler handler = (Handler) c.newInstance(args);
    return handler;

  }


}
