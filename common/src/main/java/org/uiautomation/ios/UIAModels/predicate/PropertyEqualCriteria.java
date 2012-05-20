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

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.exceptions.IOSAutomationException;

public abstract class PropertyEqualCriteria implements Criteria {

  private final String propertyName;
  private String value;
  private MatchingStrategy strategy;
  private Map<String, String> clientSideL10N = null;


  public PropertyEqualCriteria(String propertyName, String value) {
    this(propertyName, value, MatchingStrategy.exact);
  }

  public PropertyEqualCriteria(String propertyName, String value, MatchingStrategy strategy) {
    this(propertyName, value, strategy, null);
  }

  public PropertyEqualCriteria(String propertyName, String value, MatchingStrategy strategy,
      Map<String, String> contentByKey) {
    this.propertyName = propertyName;
    this.value = value;
    this.strategy = strategy;
    this.clientSideL10N = contentByKey;

    if (strategy == MatchingStrategy.clientL10N) {
      localize(MatchingStrategy.exact);
    }
  }

  public JSONObject getJSONRepresentation() throws JSONException {
    JSONObject res = new JSONObject();
    res.put("method", propertyName);
    res.put("expected", value);
    res.put("strategy", strategy);
    return res;
  }

  private void localize(MatchingStrategy newStrategy) {
    value = localizeString(value);
    strategy = newStrategy;
  }

  private String localizeString(String value) {
    if (clientSideL10N == null) {
      throw new IOSAutomationException("you need to provide client side content to use " + strategy);
    }
    String res = clientSideL10N.get(value);
    if (res == null) {
      throw new IOSAutomationException("no client side content provided for " + value);
    } else {
      return res;
    }
  }

  public String getExpected() {
    return value;
  }

  public MatchingStrategy getMatchingStrategy() {
    return strategy;
  }


}
