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

import java.lang.reflect.Constructor;

import org.json.JSONObject;
import org.uiautomation.ios.exceptions.InvalidCriteriaException;

public abstract class AbstractCriteria implements Criteria {



  public static <T> T parse(JSONObject serialized) throws Exception {
    int nbKeys = serialized.length();
    switch (nbKeys) {
      case KEYS_IN_COMPOSED_CRITERIA:
        String key = (String) serialized.keys().next();
        CompositionType type = CompositionType.valueOf(key);
        return null;// type.getAssociatedClass();
      case KEYS_IN_PROPERTY_CRITERIA:
        String method = serialized.getString("method");
        String tmp =
            method.substring(0, 1).toUpperCase() + method.toLowerCase().substring(1) + "Criteria";
        String clazz = AbstractCriteria.class.getPackage().getName() + "." + tmp;
        return (T)buildPropertyBaseCriteria(serialized,
            (Class<? extends PropertyEqualCriteria>) Class.forName(clazz));
      default:
        throw new InvalidCriteriaException("can't find the type : " + serialized.toString());
    }

  }

  private static final int KEYS_IN_COMPOSED_CRITERIA = 1;
  private static final int KEYS_IN_PROPERTY_CRITERIA = 3;



  private static PropertyEqualCriteria buildPropertyBaseCriteria(JSONObject serialized,
      Class<? extends PropertyEqualCriteria> clazz) throws Exception {
    String expected = serialized.getString("expected");
    String strategy = serialized.getString("strategy");

    Object[] args = new Object[] {expected, MatchingStrategy.valueOf(strategy)};
    Class<?>[] argsClass = new Class[] {String.class, MatchingStrategy.class};

    Constructor<?> c = clazz.getConstructor(argsClass);
    PropertyEqualCriteria crit = (PropertyEqualCriteria) c.newInstance(args);
    return crit;
  }
}
