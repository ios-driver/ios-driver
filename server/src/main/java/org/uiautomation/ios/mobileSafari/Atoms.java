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
package org.uiautomation.ios.mobileSafari;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebDriverException;

/**
 * in selenium/javascript/atoms.
 * 
 * Edit build.desc find the function in dom.js , for instance isShown edit
 * selenium/javascript/atoms/build.desc add
 * 
 * js_fragment(name="isVisible", module="bot.dom", function="bot.dom.isShown",
 * deps=["//javascript/atoms:all_js"])
 * 
 * selenium ./go //javascript/atoms:isVisible
 */
public class Atoms {

  private static String getText;
  private static String isDisplayed;
  private static String click;
  private static String back;
  private static String forward;
  private static String submit;
  private static String refresh;
  private static String findByXpath;
  private static String findsByXpath;
  private static String type;
  private static String attribute;
  private static String clear;
  private static String isSelected;
  private static String stringify;
  private static String getLocationInView;
  private static String getSize;

  static {
    try {
      getText = load("atoms/getVisibleText.js");
      isDisplayed = load("atoms/isVisible.js");
      click = load("atoms/click.js");
      back = load("atoms/back.js");
      forward = load("atoms/forward.js");
      submit = load("atoms/submit.js");
      refresh = load("atoms/refresh.js");
      findByXpath = load("atoms/findByXpath.js");
      findsByXpath = load("atoms/findsByXpath.js");
      type = load("atoms/type.js");
      attribute = load("atoms/getAttribute.js");
      clear = load("atoms/clear.js");
      isSelected = load("atoms/isSelected.js");
      stringify = load("atoms/stringify.js");
      getLocationInView = load("atoms/getLocationInView.js");
      getSize = load("atoms/getSize.js");
    } catch (Exception e) {
      throw new RuntimeException("Cannot load atoms");
    }
  }

  public static String getLocationInView() {
    return getLocationInView;
  }

  public static String getSize() {
    return getSize;
  }

  public static String getText() {
    return getText;
  }

  public static String stringify() {
    return stringify;
  }

  public static String isSelected() {
    return isSelected;
  }

  public static String type() {
    return type;
  }

  public static String isDisplayed() {
    return isDisplayed;
  }

  public static String click() {
    return click;
  }

  public static String back() {
    return back;
  }

  public static String forward() {
    return forward;
  }

  public static String findByXpath() {
    return findByXpath;
  }

  public static String findsByXpath() {
    return findsByXpath;
  }

  private static String load(String resource) throws IOException {
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
    if (is == null) {
      throw new WebDriverException("cannot load : " + resource);
    }
    StringWriter writer = new StringWriter();
    IOUtils.copy(is, writer, "UTF-8");
    String content = writer.toString();
    return content;
  }

  public static String submit() {
    return submit;
  }

  public static String refresh() {
    return refresh;
  }

  public static String getAttribute() {
    return attribute;
  }

  public static String clear() {
    return clear;
  }

}
