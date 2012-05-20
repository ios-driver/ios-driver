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
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.exceptions.InvalidCriteriaException;

public abstract class AbstractCriteria implements Criteria {



  @SuppressWarnings("unchecked")
  public static <T extends Criteria> T parse(JSONObject serialized) throws Exception {
    int nbKeys = serialized.length();
    switch (nbKeys) {
      case KEYS_IN_COMPOSED_CRITERIA:
        String key = (String) serialized.keys().next();
        CompositionType type = CompositionType.valueOf(key);
        return (T) buildComposedCriteria(serialized, type);
      case KEYS_IN_PROPERTY_CRITERIA:
        String method = serialized.getString("method");
        String tmp =
            method.substring(0, 1).toUpperCase() + method.toLowerCase().substring(1) + "Criteria";
        String clazz = AbstractCriteria.class.getPackage().getName() + "." + tmp;
        Class<? extends PropertyEqualCriteria> c =
            (Class<? extends PropertyEqualCriteria>) Class.forName(clazz);
        return (T) buildPropertyBaseCriteria(serialized, c);
      default:
        throw new InvalidCriteriaException("can't find the type : " + serialized.toString());
    }

  }

  private static final int KEYS_IN_COMPOSED_CRITERIA = 1;
  private static final int KEYS_IN_PROPERTY_CRITERIA = 3;


  private static ComposedCriteria buildComposedCriteria(JSONObject serialized, CompositionType type)
      throws Exception {
    JSONArray array = serialized.getJSONArray(type.toString());
    if (type == CompositionType.NOT && array.length() != 1) {
      throw new InvalidCriteriaException("negative criteria apply to 1 criteria only " + serialized);
    }
    List<Criteria> criterias = new ArrayList<Criteria>();

    for (int i = 0; i < array.length(); i++) {
      JSONObject c = array.getJSONObject(i);
      Criteria crit = parse(c);
      criterias.add(crit);
    }

    Object[] args = new Object[] {criterias};
    Class<?>[] argsClass = new Class[] {List.class};

    Constructor<?> c = type.getAssociatedClass().getConstructor(argsClass);
    ComposedCriteria crit = (ComposedCriteria) c.newInstance(args);
    return crit;
  }

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
