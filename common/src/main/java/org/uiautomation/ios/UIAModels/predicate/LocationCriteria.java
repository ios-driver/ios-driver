package org.uiautomation.ios.UIAModels.predicate;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;

public class LocationCriteria extends DecorableCriteria {

  private final int x;
  private final int y;

  /**
   * for an element with top left corner at 0,0 , and bottom right corner at
   * 10,10 : 0,0 belongs to the element 10,10 belongs to the next one.
   * 
   * @param x
   * @param y
   */
  public LocationCriteria(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public JSONObject stringify() {
    JSONObject res = new JSONObject();
    try {
      res.put("x", x);
      res.put("y", y);
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
    return res;
  }

}
