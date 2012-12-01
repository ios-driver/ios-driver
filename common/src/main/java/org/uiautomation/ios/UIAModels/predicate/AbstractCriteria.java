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
import org.json.JSONObject;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.WebDriverException;

public abstract class AbstractCriteria implements Criteria {

  public static <T extends Criteria> T parse(JSONObject serialized) throws Exception {
    return parse(serialized, null);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Criteria> T parse(JSONObject serialized, CriteriaDecorator decorator) {
    try {
      int nbKeys = serialized.length();
      switch (nbKeys) {
      case KEYS_IN_EMPTY_CRITERIA:
        return (T) new EmptyCriteria();
      case KEYS_IN_COMPOSED_CRITERIA:
        String key = (String) serialized.keys().next();
        CompositionType type = CompositionType.valueOf(key);
        return (T) buildComposedCriteria(serialized, type, decorator);
      case KEYS_IN_LOCATION_CRITERIA:
        int x = serialized.getInt("x");
        int y = serialized.getInt("y");
        return (T) buildLocationCriteria(serialized, x, y, decorator);
      case KEYS_IN_PROPERTY_CRITERIA:
        String method = serialized.getString("method");
        String tmp = method.substring(0, 1).toUpperCase() + method.toLowerCase().substring(1) + "Criteria";
        String clazz = AbstractCriteria.class.getPackage().getName() + "." + tmp;
        Class<? extends PropertyEqualCriteria> c = (Class<? extends PropertyEqualCriteria>) Class.forName(clazz);
        return (T) buildPropertyBaseCriteria(serialized, c, decorator);
      default:
        throw new InvalidSelectorException("can't find the type : " + serialized.toString());
      }
    } catch (Exception e) {
      throw new WebDriverException(e);
    }

  }

  private static final int KEYS_IN_EMPTY_CRITERIA = 0;
  private static final int KEYS_IN_COMPOSED_CRITERIA = 1;
  private static final int KEYS_IN_PROPERTY_CRITERIA = 4;
  private static final int KEYS_IN_LOCATION_CRITERIA = 2;

  private static LocationCriteria buildLocationCriteria(JSONObject serialized, int x, int y, CriteriaDecorator decorator) {
    LocationCriteria res = new LocationCriteria(x, y);
    res.addDecorator(decorator);
    return res;
  }

  private static ComposedCriteria buildComposedCriteria(JSONObject serialized, CompositionType type,
      CriteriaDecorator decorator) throws Exception {
    JSONArray array = serialized.getJSONArray(type.toString());
    if (type == CompositionType.NOT && array.length() != 1) {
      throw new InvalidSelectorException("negative criteria apply to 1 criteria only " + serialized);
    }
    List<Criteria> criterias = new ArrayList<Criteria>();

    for (int i = 0; i < array.length(); i++) {
      JSONObject c = array.getJSONObject(i);
      Criteria crit = parse(c, decorator);
      criterias.add(crit);
    }

    Object[] args = new Object[] { criterias };
    Class<?>[] argsClass = new Class[] { List.class };

    Constructor<?> c = type.getAssociatedClass().getConstructor(argsClass);
    ComposedCriteria crit = (ComposedCriteria) c.newInstance(args);
    crit.addDecorator(decorator);
    crit.decorate();
    return crit;
  }

  private static PropertyEqualCriteria buildPropertyBaseCriteria(JSONObject serialized,
      Class<? extends PropertyEqualCriteria> clazz, CriteriaDecorator decorator) throws Exception {
    String expected = serialized.getString("expected");
    String matching = serialized.getString("matching");
    String l10n = serialized.getString("l10n");

    Object[] args = new Object[] { expected, L10NStrategy.valueOf(l10n), MatchingStrategy.valueOf(matching) };
    Class<?>[] argsClass = new Class[] { String.class, L10NStrategy.class, MatchingStrategy.class };

    Constructor<?> c = clazz.getConstructor(argsClass);
    PropertyEqualCriteria crit = (PropertyEqualCriteria) c.newInstance(args);
    crit.addDecorator(decorator);
    crit.decorate();
    return crit;
  }
}
