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
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.predicate.AbstractCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.CriteriaDecorator;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.application.ServerSideL10NDecorator;
import org.uiautomation.ios.server.command.Handler;
import org.uiautomation.ios.server.command.NotImplementedHandler;
import org.uiautomation.ios.server.command.impl.AttributeCommand;
import org.uiautomation.ios.server.command.impl.DefaultUIAScriptHandler;
import org.uiautomation.ios.server.command.impl.FindElementRoot;
import org.uiautomation.ios.server.command.impl.FindElementsRoot;
import org.uiautomation.ios.server.command.impl.GetCapabilitiesCommandHandler;
import org.uiautomation.ios.server.command.impl.GetSessions;
import org.uiautomation.ios.server.command.impl.GetTimeoutCommandHandler;
import org.uiautomation.ios.server.command.impl.LocalTarget;
import org.uiautomation.ios.server.command.impl.LogElementTree;
import org.uiautomation.ios.server.command.impl.NewSession;
import org.uiautomation.ios.server.command.impl.ServerStatus;
import org.uiautomation.ios.server.command.impl.SetTimeoutCommandHandler;
import org.uiautomation.ios.server.command.impl.StopSession;
import org.uiautomation.ios.server.command.impl.TakeScreenshot;

public enum CommandMapping {

  NEW_SESSION(NewSession.class),
  GET_SESSION(GetCapabilitiesCommandHandler.class),
  SESSIONS(GetSessions.class),
  DELETE_SESSION(StopSession.class),
  SET_TIMEOUT(SetTimeoutCommandHandler.class),
  GET_TIMEOUT(GetTimeoutCommandHandler.class),
  
  // UIATarget
  LOCAL_TARGET(LocalTarget.class),
  HOST(NotImplementedHandler.class),
  TREE(LogElementTree.class),
  TREE_ROOT(LogElementTree.class),
  
  TARGET_RECT(".rect()"),
  TARGET_TAP(".tap({x::x,y::y})"),
  SET_ORIENTATION(".setDeviceOrientation(:orientation)"),
  
  SCREENSHOT(TakeScreenshot.class),
  
  FONT_MOST_APP(".frontMostApp()"),
  
 
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
  HIT_POINT(NotImplementedHandler.class),
  RECT(".rect()"),
  
  PARENT(NotImplementedHandler.class),
  

  ELEMENT_ROOT(FindElementRoot.class),
  ELEMENTS_ROOT(FindElementsRoot.class),
  
  ELEMENT(FindElementRoot.class),
  ELEMENTS(FindElementsRoot.class),
  
  //ELEMENT(".element(:depth,:criteria)"),
  //ELEMENTS(".elements2(:depth,:criteria)"),
  ANCESTRY(NotImplementedHandler.class),

  IS_VISIBLE(".isVisible()"),
  IS_STALE(".isStale()"),
  
  
  //LABEL(".label()"),
  //NAME(".name()"),
  //VALUE(".value()"),
  ATTRIBUTE(AttributeCommand.class),
  //WITH_NAME(".withName(:name)"),
  //WITH_PREDICATE(".withPredicate(PredicateString predicateString)"),
  //WITH_VALUE_FOR_KEY(".withValueForKey(Object value,String key)"),
  
  
  TAP(".tap2()"),
  TOUCH_AND_HOLD(".touchAndHold(:duration)"),
  DOUBLE_TAP(".doubleTap()"),
  TWO_FINGER_TAP(".twoFingerTap()"),
  TAP_WITH_OPTIONS(""),
  DRAG_INSIDE_WITH_OPTIONS(""),
  FLICK_INSIDE_WITH_OPTIONS(""),
  SCROLL_TO_VISIBLE(".scrollToVisible()"),
  ROTATE_WITH_OPTIONS(NotImplementedHandler.class),
  
  // UIAElementArray
  GET(".toArray()[:index]"),
  FIRST_WITH_NAME(".firstWithName(:name)"),
  FIRST_WITH_PREDICATE(".firstWithPredicate()"),
  FIRST_WITH_VALUE_FOR_KEY(NotImplementedHandler.class),
  ARRAY_WITH_NAME(".withName(:name)"),
  ARRAY_WITH_PREDICATE(NotImplementedHandler.class),
  ARRAY_WITH_VALUE_FOR_KEY(NotImplementedHandler.class),

  //UIANavigationBar
  LEFT_BUTTON(".leftButton()"),
  RIGHT_BUTTON(".rightButton()"),
  
  // UIATextField
  SET_VALUE(".setValue(:value)"),
  
  //UIATableView
  TABLE_GROUPS(".groups()"),
  TABLE_CELLS(".cells()"),
  TABLE_VISIBLE_CELLS(".visibleCells()"),
  
  ALERT_CANCEL_BUTTON(".cancelButton2()"),
  ALERT_DEFAULT_BUTTON(".defaultButton2()"),
  
  STATUS(ServerStatus.class);
  
  
  private WebDriverLikeCommand command;
  private final Class<? extends Handler> handlerClass;
  private final String jsMethod;
 
  private CommandMapping(String jsMethod){
    this.command =WebDriverLikeCommand.valueOf(this.name());
    this.handlerClass = DefaultUIAScriptHandler.class;
    this.jsMethod = jsMethod;
  }
  
  private CommandMapping(Class<? extends Handler> handlerClass,String jsMethod){
    this.command =WebDriverLikeCommand.valueOf(this.name());
    this.handlerClass = handlerClass;
    this.jsMethod = jsMethod;
  }
  
  private CommandMapping(Class<? extends Handler> handlerClass){
    this.command =WebDriverLikeCommand.valueOf(this.name());
    this.handlerClass = handlerClass;
    this.jsMethod = null;
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
      String res = jsMethod;
      Iterator<String> iter = payload.keys();
      while (iter.hasNext()) {
        String key = iter.next();
        Object value = payload.opt(key);
        res = res.replace(":" + key, value.toString());
      }
      return res;
    } else {
      return jsMethod;
    }
  }

 
 /* private CriteriaDecorator getDecorator(IOSApplication aut){
    ServerSideL10NDecorator decorator = new ServerSideL10NDecorator(aut);
    return decorator;
  }*/

  /*private JSONObject serverSidePayloadChanges(JSONObject payload, IOSApplication aut) {
    if (payload.has("criteria")){
      try {
        JSONObject json = payload.getJSONObject("criteria");
        Criteria decorated = AbstractCriteria.parse(json,getDecorator(aut));
        payload.put("criteria", decorated.getJSONRepresentation().toString());
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return payload;
  }*/

  public Handler createHandler(IOSDriver driver,WebDriverLikeRequest request) throws Exception{
   
    Class<?> clazz = handlerClass;
    if (clazz == null) {
      throw new RuntimeException("handler NI");
    }

    Object[] args = new Object[] {driver, request};
    Class<?>[] argsClass = new Class[] {IOSDriver.class, WebDriverLikeRequest.class};

    Constructor<?> c = clazz.getConstructor(argsClass);
    Handler handler = (Handler) c.newInstance(args);
    return  handler;
    
  }

  
}
