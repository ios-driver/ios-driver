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
package org.uiautomation.ios.UIAModels.predicate;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;

public class LocationCriteria extends DecorableCriteria {

  private final int x;
  private final int y;

  /**
   * for an element with top left corner at 0,0 , and bottom right corner at
   * 10,10 : 0,0 belongs to the element 10,10 belongs to the next one.
   * 
   * @param x
   * @param y
   */
  public LocationCriteria(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public JSONObject stringify() {
    JSONObject res = new JSONObject();
    try {
      res.put("x", x);
      res.put("y", y);
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
    return res;
  }

}
