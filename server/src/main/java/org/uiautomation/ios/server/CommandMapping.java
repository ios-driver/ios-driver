package org.uiautomation.ios.server;

import java.lang.reflect.Constructor;
import java.util.Iterator;


import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.predicate.AbstractCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.CriteriaDecorator;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.application.ServerSideL10NDecorator;
import org.uiautomation.ios.server.command.Handler;
import org.uiautomation.ios.server.command.impl.CustomUIAScriptHandler;
import org.uiautomation.ios.server.command.impl.DefaultUIAScriptHandler;
import org.uiautomation.ios.server.command.impl.LocalTarget;
import org.uiautomation.ios.server.command.impl.PostTargetScreenshotWithName;
import org.uiautomation.ios.server.command.impl.ServerStatus;
import org.uiautomation.ios.server.command.impl.session.NewSession;
import org.uiautomation.ios.server.command.impl.session.StopSession;
import org.uiautomation.ios.server.instruments.SessionsManager;

public enum CommandMapping {

  NEW_SESSION(NewSession.class,null),
  GET_SESSION(CustomUIAScriptHandler.class,"complex..."),
  DELETE_SESSION(StopSession.class,null),
  SET_TIMEOUT(CustomUIAScriptHandler.class,""),
  GET_TIMEOUT(CustomUIAScriptHandler.class,""),
  
  // UIATarget
  LOCAL_TARGET(LocalTarget.class,".localTarget()"),
  HOST(null,null),
  TREE(DefaultUIAScriptHandler.class,".tree(:lastScreenshotPath)"),
  MODEL(DefaultUIAScriptHandler.class,".model()"),
  TARGET_RECT(DefaultUIAScriptHandler.class,".rect()"),
  TARGET_TAP(CustomUIAScriptHandler.class,"complex..."),
  TARGET_NAME(DefaultUIAScriptHandler.class,".name()"),
  SYSTEM_NAME(DefaultUIAScriptHandler.class,".systemName()"),
  SYSTEM_VERSION(DefaultUIAScriptHandler.class,".systemVersion()"),
  
  SCREENSHOT_WITH_NAME(PostTargetScreenshotWithName.class,".captureScreenWithName('tmpScreenshot')"),
  
  FONT_MOST_APP(DefaultUIAScriptHandler.class,".frontMostApp()"),
  
 
  // UIAApplication
  MAIN_WINDOW(DefaultUIAScriptHandler.class,".mainWindow()"),
  WINDOWS(DefaultUIAScriptHandler.class,".windows()"),
  KEYBOARD(DefaultUIAScriptHandler.class,".keyboard2()"), 
  KEYBOARD_KEYS(DefaultUIAScriptHandler.class,".keys()"), 
  KEYBOARD_BUTTONS (DefaultUIAScriptHandler.class,".buttons()"),
  TYPE_STRING(DefaultUIAScriptHandler.class,".typeString(:string)"),
  BUNDLE_ID(DefaultUIAScriptHandler.class,".bundleID()"),
  VERSION(DefaultUIAScriptHandler.class,".version()"),
  BUNDLE_VERSION(DefaultUIAScriptHandler.class,".bundleVersion()"),
   
    // UIAHost
   PERFORM_TASK_WITH_PATH_ARGUMENTS_TIMEOUT(null,null),
  
 
  // UIAElement
  HIT_POINT(DefaultUIAScriptHandler.class,null),
  RECT(DefaultUIAScriptHandler.class,".rect()"),
  
  PARENT(DefaultUIAScriptHandler.class,null),
  ELEMENT(DefaultUIAScriptHandler.class,".element(:depth,:criteria)"),
  ELEMENTS(DefaultUIAScriptHandler.class,".elements2(:depth,:criteria)"),
  ANCESTRY(DefaultUIAScriptHandler.class,null),

  IS_VISIBLE(DefaultUIAScriptHandler.class,".isVisible()"),
  IS_STALE(DefaultUIAScriptHandler.class,".isStale()"),
  
  
  LABEL(DefaultUIAScriptHandler.class,".label()"),
  NAME(DefaultUIAScriptHandler.class,".name()"),
  VALUE(DefaultUIAScriptHandler.class,".value()"),
  WITH_NAME(DefaultUIAScriptHandler.class,".withName(:name)"),
  WITH_PREDICATE(DefaultUIAScriptHandler.class,".withPredicate(PredicateString predicateString)"),
  WITH_VALUE_FOR_KEY(DefaultUIAScriptHandler.class,".withValueForKey(Object value,String key)"),
  
  
  TAP(DefaultUIAScriptHandler.class,".tap2()"),
  TOUCH_AND_HOLD(DefaultUIAScriptHandler.class,".touchAndHold(:duration)"),
  DOUBLE_TAP(DefaultUIAScriptHandler.class,".doubleTap()"),
  TWO_FINGER_TAP(DefaultUIAScriptHandler.class,".twoFingerTap()"),
  TAP_WITH_OPTIONS(DefaultUIAScriptHandler.class,null),
  DRAG_INSIDE_WITH_OPTIONS(DefaultUIAScriptHandler.class,null),
  FLICK_INSIDE_WITH_OPTIONS(DefaultUIAScriptHandler.class,null),
  SCROLL_TO_VISIBLE(DefaultUIAScriptHandler.class,".scrollToVisible()"),
  ROTATE_WITH_OPTIONS(DefaultUIAScriptHandler.class,null),
  
  // UIAElementArray
  GET(DefaultUIAScriptHandler.class,".toArray()[:index]"),
  FIRST_WITH_NAME(DefaultUIAScriptHandler.class,".firstWithName(:name)"),
  FIRST_WITH_PREDICATE(DefaultUIAScriptHandler.class,".firstWithPredicate()"),
  FIRST_WITH_VALUE_FOR_KEY(DefaultUIAScriptHandler.class,null),
  ARRAY_WITH_NAME(DefaultUIAScriptHandler.class,".withName(:name)"),
  ARRAY_WITH_PREDICATE(DefaultUIAScriptHandler.class,null),
  ARRAY_WITH_VALUE_FOR_KEY(DefaultUIAScriptHandler.class,null),

  //UIANavigationBar
  LEFT_BUTTON(DefaultUIAScriptHandler.class,".leftButton()"),
  RIGHT_BUTTON(DefaultUIAScriptHandler.class,".rightButton()"),
  
  // UIATextField
  SET_VALUE(DefaultUIAScriptHandler.class,".setValue(:value)"),
  
  //UIATableView
  TABLE_GROUPS(DefaultUIAScriptHandler.class,".groups()"),
  TABLE_CELLS(DefaultUIAScriptHandler.class,".cells()"),
  TABLE_VISIBLE_CELLS(DefaultUIAScriptHandler.class,".visibleCells()"),
  
  STATUS(ServerStatus.class,"");
  
  private WebDriverLikeCommand command;
  private final Class<? extends Handler> handlerClass;
  private final String jsMethod;
 
  
  
  private CommandMapping(Class<? extends Handler> handlerClass,String jsMethod){
    this.command =WebDriverLikeCommand.valueOf(this.name());
    this.handlerClass = handlerClass;
    this.jsMethod = jsMethod;
  }
  
  public static CommandMapping get(WebDriverLikeCommand wdlc) {
    for (CommandMapping cm : values()) {
      if (cm.command == wdlc) {
        return cm;
      }
    }
    throw new RuntimeException("not mapped : " + wdlc);
  }
  
  public String jsMethod(JSONObject payload,IOSApplication aut) {
    if (jsMethod == null) {
      throw new RuntimeException("JS method missing from mapping.");
    }

    if (payload != null) {
      payload = serverSidePayloadChanges(payload,aut);
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

 
  private CriteriaDecorator getDecorator(IOSApplication aut){
    ServerSideL10NDecorator decorator = new ServerSideL10NDecorator(aut);
    return decorator;
  }

  private JSONObject serverSidePayloadChanges(JSONObject payload, IOSApplication aut) {
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
  }

  public Handler createHandler(SessionsManager instruments,WebDriverLikeRequest request) throws Exception{
   
    Class<?> clazz = handlerClass;
    if (clazz == null) {
      throw new RuntimeException("handler NI");
    }

    Object[] args = new Object[] {instruments, request};
    Class<?>[] argsClass = new Class[] {SessionsManager.class, WebDriverLikeRequest.class};

    Constructor<?> c = clazz.getConstructor(argsClass);
    Object handler = c.newInstance(args);
    return (Handler) handler;
    
  }

  
}
