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

public class OrCriteria extends DecorableCriteria {

  private final List<Criteria> criterias;


  public OrCriteria(Criteria... criterias) {
    this.criterias = Arrays.asList(criterias);
  }

  public OrCriteria(List<Criteria> criterias) {
    this.criterias = criterias;
  }



  public JSONObject getJSONRepresentation() throws JSONException {
    JSONObject res = new JSONObject();
    JSONArray or = new JSONArray();
    for (Criteria c : criterias) {
      or.put(c.getJSONRepresentation());
    }
    res.put("OR", or);
    return res;
  }


}
