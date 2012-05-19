package org.uiautomation.ios.ide.views;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.exceptions.IOSAutomationException;

public class JSONView implements View {

  private JSONObject object;
  private JSONArray array;

  public JSONView(JSONObject o) {
    this.object = o;
  }

  public JSONView(JSONArray a) {
    this.array = a;
  }


  private String getContent() throws JSONException {
    if (object == null && array == null) {
      throw new IOSAutomationException(
          "json view needs to have either jsonobject or array. Cannot be null");
    }
    int indent = 2;
    return object != null ? object.toString(indent) : array.toString(indent);
  }

  @Override
  public void render(HttpServletResponse response) throws Exception {
    response.setContentType("application/x-javascript");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(200);
    response.getWriter().print(getContent());
  }

}
