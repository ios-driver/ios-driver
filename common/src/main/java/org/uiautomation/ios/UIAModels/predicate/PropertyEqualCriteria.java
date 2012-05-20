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

public abstract class PropertyEqualCriteria implements Criteria {

  private final String propertyName;
  private final String value;
  private final MatchingStrategy strategy;


  public PropertyEqualCriteria(String propertyName, String value) {
    this(propertyName, value, MatchingStrategy.exact);
  }

  public PropertyEqualCriteria(String propertyName, String value, MatchingStrategy strategy) {
    this.propertyName = propertyName;
    this.value = value;
    this.strategy = strategy;
  }

  public JSONObject getJSONRepresentation() throws JSONException {
    JSONObject res = new JSONObject();
    res.put(propertyName, value);
    res.put("strategy", strategy);
    return res;
  }


}


enum MatchingStrategy {
  exact, regex, clientL10N, serverL10N;
}
