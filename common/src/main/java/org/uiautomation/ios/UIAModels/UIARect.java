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
package org.uiautomation.ios.UIAModels;

import org.json.JSONException;
import org.json.JSONObject;

public class UIARect {

  private int x;
  private int y;
  private int height;
  private int width;

  public UIARect(int x, int y, int height, int width) {
    this.x = x;
    this.y = y;
    this.height = height;
    this.width = width;

  }

  public UIARect(JSONObject o) throws JSONException {
    JSONObject origin = o.getJSONObject("origin");
    x = origin.getInt("x");
    y = origin.getInt("y");

    JSONObject size = o.getJSONObject("size");
    height = size.getInt("height");
    width = size.getInt("width");
  }

  public String toString() {
    return "origin(x=" + x + ",y=" + y + "),size(height=" + height + ",width=" + width + ")";
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

}
