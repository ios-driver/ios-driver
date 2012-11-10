package org.uiautomation.ios.UIAModels.predicate;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationCriteria extends DecorableCriteria {

  private final int x;
  private final int y;

  /**
   * for an element with top left corner at 0,0 , and bottom right corner at 10,10 :
   * 0,0 belongs to the element
   * 10,10 belongs to the next one.
   * @param x
   * @param y
   */
  public LocationCriteria(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public JSONObject getJSONRepresentation() throws JSONException {
    JSONObject res = new JSONObject();
    res.put("x", x);
    res.put("y", y);
    return res;
  }



}
