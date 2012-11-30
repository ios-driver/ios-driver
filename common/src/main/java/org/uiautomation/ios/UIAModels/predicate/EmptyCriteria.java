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

import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;

/**
 * matches all
 * 
 */
public class EmptyCriteria extends DecorableCriteria {

  public JSONObject stringify(){
    JSONObject res = new JSONObject();
    return res;
  }

  @Override
  public void addDecorator(CriteriaDecorator decorator) {
    throw new WebDriverException("NI");

  }

}
