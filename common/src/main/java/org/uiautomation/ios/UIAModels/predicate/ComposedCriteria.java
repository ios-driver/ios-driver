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

import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;

public abstract class ComposedCriteria extends DecorableCriteria {

  private final List<Criteria> criterias;
  private final CompositionType type;


  public ComposedCriteria(CompositionType t, Criteria... criterias) {
    this.criterias = Arrays.asList(criterias);
    this.type = t;
  }

  public ComposedCriteria(CompositionType t, List<Criteria> criterias) {
    this.criterias = criterias;
    this.type = t;
  }

  public JSONObject stringify()  {
    JSONObject res = new JSONObject();
    JSONArray or = new JSONArray();
    for (Criteria c : criterias) {
      or.put(c.stringify());
    }
    try {
      res.put(type.toString(), or);
    }catch (JSONException e) {
      throw new WebDriverException(e);
    }
   
    return res;
  }

  
}
