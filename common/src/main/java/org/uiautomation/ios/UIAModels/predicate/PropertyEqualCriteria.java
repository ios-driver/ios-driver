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

public abstract class PropertyEqualCriteria extends DecorableCriteria {

  private final String propertyName;
  private String value;

  private L10NStrategy l10nstrategy;
  private MatchingStrategy matchingStrategy;

  public PropertyEqualCriteria(String propertyName, String value) {
    this(propertyName, value, L10NStrategy.none, MatchingStrategy.exact);
  }

  public PropertyEqualCriteria(String propertyName, String value, L10NStrategy l10nStrategy,
      MatchingStrategy matchingStrategy) {
    this.propertyName = propertyName;
    this.value = value;
    this.l10nstrategy = l10nStrategy;
    this.matchingStrategy = matchingStrategy;
  }

  public JSONObject stringify() {
    JSONObject res = new JSONObject();
    try {
      res.put("method", propertyName);
      res.put("expected", value);
      res.put("l10n", l10nstrategy);
      res.put("matching", matchingStrategy);
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
    return res;
  }

  public L10NStrategy getL10nstrategy() {
    return l10nstrategy;
  }

  public void setL10nstrategy(L10NStrategy l10nstrategy) {
    this.l10nstrategy = l10nstrategy;
  }

  public MatchingStrategy getMatchingStrategy() {
    return matchingStrategy;
  }

  public void setMatchingStrategy(MatchingStrategy matchingStrategy) {
    this.matchingStrategy = matchingStrategy;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

}
