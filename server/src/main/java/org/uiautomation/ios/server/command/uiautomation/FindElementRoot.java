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
package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.predicate.AbstractCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.ServerSideSession;
import org.uiautomation.ios.server.application.ServerSideL10NDecorator;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class FindElementRoot extends UIAScriptHandler {

  private static final String jsTemplate = "var root = UIAutomation.cache.get(':reference');"
      + "var result = root.element(:depth,:criteria);" + "UIAutomation.createJSONResponse(':sessionId',0,result);";

  public FindElementRoot(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);

    String reference = "1";
    if (request.hasVariable(":reference")) {
      reference = request.getVariableValue(":reference");
    }

    ServerSideSession session = driver.getSession(request.getSession());
    
    

    JSONObject payload = request.getPayload();

    // xpath query are done in the server, but pulling the complete tree as xml,
    // and parsing that rather that use instrument for the finding.
    if (payload.has("using") && "xpath".equals(payload.optString("using"))) {
      findElementParsingLocalXMLTree(reference);
    } else {
      findElementUsingInstruments(reference);
    }

  
  }

  private void findElementParsingLocalXMLTree(String reference) {
    JSONObject logElementTree = getSession().getNativeDriver().logElementTree(null, false);
    System.out.println(logElementTree.toString());
    
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
  
  private void findElementUsingInstruments(String reference){
    Criteria decorated = null;
    try {
      int depth = getRequest().getPayload().optInt("depth", -1);

      try {
        ServerSideL10NDecorator decorator = new ServerSideL10NDecorator(getSession().getApplication());
        JSONObject json = getCriteria(getRequest().getPayload());
        decorated = AbstractCriteria.parse(json, decorator);
        getRequest().getPayload().put("criteria", decorated.stringify().toString());
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      String js = jsTemplate
          .replace(":sessionId", getRequest().getSession())
          .replace(":depth", "" + depth)
          .replace(":reference", reference)
          .replace(":criteria", decorated.stringify().toString());
      setJS(js);
    } catch (Exception e) {
      throw new WebDriverException("error parsing the payload", e);
    }
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
          Criteria c = new TypeCriteria(Class.forName(p.getName() + "." + value));
          return c.stringify();
        } catch (ClassNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
          return null;
        }
      } else {
        throw new RuntimeException("NI :" + payload);
      }
    } else {
      throw new WebDriverException("wrong format for the findElement command " + payload);
    }

  }
}
