/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
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
package org.uiautomation.ios.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.IOSServerManager;
import org.uiautomation.ios.utils.JSTemplate;

import java.util.Map;

public class FindElementNHandler extends BaseFindElementNHandler {

  private static final JSTemplate template = new JSTemplate(
      "var root = UIAutomation.cache.get('%:reference$s');" +
      "var result = root.element(%:depth$d,%:criteria$s);" +
      "UIAutomation.createJSONResponse('%:sessionId$s',0,result);",
      "sessionId", "reference", "depth", "criteria");

  public FindElementNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);

    // xpath query are done in the server, by pulling the complete tree as xml,
    // and parsing that rather that use instruments for the finding.
    if (isXPathMode()) {
      setJS("I will not be executed.");
    } else {
      setJS("");
    }
  }

  @Override
  public Response handle() throws Exception {
    if (!isXPathMode()) {
      setJS(getJSForFindElementUsingInstruments());
      return super.handle();
    } else {
      return createResponse(findElementParsingLocalXMLTree());
    }
  }

  @Override
  protected <T> T find() {
    String xpath = getXpath();
    return (T) getParser().findElementByXpath(xpath, getReference());
  }

  private Map<String, String> findElementParsingLocalXMLTree() {
    return findByXpathWithImplicitWait();
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

  private String getJSForFindElementUsingInstruments() {
    int depth = getRequest().getPayload().optInt("depth", -1);
    return template.generate(getRequest().getSession(), getReference(), depth, getCriteria().stringify().toString());
  }
}
