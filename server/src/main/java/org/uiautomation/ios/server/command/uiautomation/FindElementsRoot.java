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

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;

public class FindElementsRoot extends BaseFindElementNHandler {

  private static final String jsTemplate = "var root = UIAutomation.cache.get(':reference');"
      + "var result = root.elements2(:depth,:criteria);" 
      + "UIAutomation.createJSONResponse(':sessionId',0,result);";

  public FindElementsRoot(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);

    // xpath query are done in the server, by pulling the complete tree as xml,
    // and parsing that rather that use instruments for the finding.
    if (isXPathMode()) {
      setJS("I will not be executed.");
    } else {
      setJS(getJSForFindElementsUsingInstruments());
    }
  }

  @Override
  public Response handle() throws Exception {
    if (!isXPathMode()) {
      return super.handle();
    } else {
      return createResponse(findElementsParsingLocalXMLTree());
    }
  }

  private List<Map<String, String>> findElementsParsingLocalXMLTree() {
    String xpath = getRequest().getPayload().optString("value");
    return getParser().findElementsByXpath(xpath, getReference());
  }

  private String getJSForFindElementsUsingInstruments() {
    int depth = getRequest().getPayload().optInt("depth", -1);
      String js = jsTemplate
          .replace(":sessionId", getRequest().getSession())
          .replace(":depth", "" + depth)
          .replace(":reference", getReference())
          .replace(":criteria", getCriteria().stringify().toString());
     return js;
  }
  
  

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
