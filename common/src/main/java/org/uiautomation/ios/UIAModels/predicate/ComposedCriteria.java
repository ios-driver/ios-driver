package org.uiautomation.ios.UIAModels.predicate;

import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

  public JSONObject getJSONRepresentation() throws JSONException {
    JSONObject res = new JSONObject();
    JSONArray or = new JSONArray();
    for (Criteria c : criterias) {
      or.put(c.getJSONRepresentation());
    }
    res.put(type.toString(), or);
    return res;
  }

  
}
