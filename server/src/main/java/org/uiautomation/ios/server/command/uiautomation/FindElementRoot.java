package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.predicate.AbstractCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.application.ServerSideL10NDecorator;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class FindElementRoot extends UIAScriptHandler {

  private static final String jsTemplate = "var root = UIAutomation.cache.get(':reference');"
      + "var result = root.element(:depth,:criteria);" + "UIAutomation.createJSONResponse(':sessionId',0,result);";

  public FindElementRoot(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);

    String reference = "0";
    if (request.hasVariable(":reference")) {
      reference = request.getVariableValue(":reference");
    }

    ServerSideSession session = driver.getSession(request.getSession());
    ServerSideL10NDecorator decorator = new ServerSideL10NDecorator(session.getApplication());
    Criteria decorated = null;
    try {
      int depth = request.getPayload().optInt("depth",-1);

      try {
        JSONObject json = getCriteria(request.getPayload());
        decorated = AbstractCriteria.parse(json, decorator);
        request.getPayload().put("criteria", decorated.getJSONRepresentation().toString());
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      String js = jsTemplate.replace(":sessionId", request.getSession()).replace(":depth", "" + depth)
          .replace(":reference", reference).replace(":criteria", decorated.getJSONRepresentation().toString());
      setJS(js);
    } catch (Exception e) {
      throw new IOSAutomationException("error parsing the payload", e);
    }
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

  private JSONObject getCriteria(JSONObject payload) throws JSONException {
    if (payload.has("criteria")) {
      JSONObject json = payload.getJSONObject("criteria");
      return json;
    } else if (payload.has("using")) {
      String using = payload.getString("using");
      String value = payload.getString("value");
      if ("tag name".equals(using)) {
        try {
          Package p = UIAElement.class.getPackage();
          Criteria c = new TypeCriteria(Class.forName(p.getName()+"."+value));
          return c.getJSONRepresentation();
        } catch (ClassNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
          return null;
        }
      } else {
        throw new RuntimeException("NI :" + payload);
      }
    } else {
      throw new IOSAutomationException("wrong format for the findElement command " + payload);
    }

  }
}
