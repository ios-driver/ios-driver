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
package org.uiautomation.ios.server.command.web;

import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.WebDriverException;

public class ToCSSSelectorConverter {

  public static String convertToCSSSelector(String type, String value) {
    if ("css selector".equals(type)) {
      return value;
    }
    if ("id".equals(type)) {
      // return "#" + value; // doesn't work for id starting with an int.
      return "[id='" + value + "']";
    }
    if ("tag name".equals(type)) {
      return value;
    }
    if ("class name".equals(type)) {
      // detect composite class name
      if (isCompoundName(value)) {
        throw new InvalidSelectorException("Compound class names aren't allowed");
      }
      return "." + value;
    }
    if ("class name".equals(type)) {
      return "." + value;
    }
    if ("name".equals(type)) {
      return "[name='" + value + "']";
    }
    throw new WebDriverException("NI , selector type " + type);
  }

  private static boolean isCompoundName(String value) {
    return value != null && value.split(" ").length != 1;
  }
}
