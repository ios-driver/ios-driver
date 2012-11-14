package org.uiautomation.ios.server.command.web;

import org.uiautomation.ios.exceptions.IOSAutomationException;

public class ToCSSSelectorConvertor {

  
  
  public static String convertToCSSSelector(String type,String value){
    if ("css selector".equals(type)){
      return value;
    }
    if ("id".equals(type)){
      //return "#" + value; // doesn't work for id starting with an int.
      return "[id='"+value+"']";
    }
    if ("tag name".equals(type)){
      return value;
    }
    if ("class name".equals(type)){
      return "."+value;
    }
    if ("class name".equals(type)){
      return "."+value;
    }
    if ("name".equals(type)){
      return "[name='"+value+"']";
    }
    throw new IOSAutomationException("NI , selector type "+type);
  }
}
