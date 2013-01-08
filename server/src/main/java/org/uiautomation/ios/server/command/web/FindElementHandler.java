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
package org.uiautomation.ios.server.command.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.mobileSafari.NodeId;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;
import org.uiautomation.ios.webInspector.DOM.RemoteExceptionException;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

public class FindElementHandler extends BaseWebCommandHandler {

  public FindElementHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    waitForPageToLoad();

    int implicitWait = (Integer) getConf("implicit_wait", 0);
    long deadline = System.currentTimeMillis() + implicitWait;
    RemoteWebElement rwe = null;
    do {
      try {
        rwe = findElement();
        break;
      } catch (NoSuchElementException e) {
        //ignore.
      } catch (RemoteExceptionException e2) {
        // ignore.
        // if the page is reloading, the previous nodeId won't be there anymore, resulting in a
        // RemoteExceptionException: Could not find node with given id.Keep looking.
      }
    } while (System.currentTimeMillis() < deadline);

    if (rwe == null) {
      throw new NoSuchElementException(
          "No element found for " + getRequest().getPayload() + " after waiting for " + implicitWait
          + " ms.");
    } else {
      JSONObject res = new JSONObject();
      res.put("ELEMENT", "" + rwe.getNodeId().getId());
      Response resp = new Response();
      resp.setSessionId(getSession().getSessionId());
      resp.setStatus(0);
      resp.setValue(res);
      return resp;
    }
  }


  private RemoteWebElement findElement() throws Exception {
    JSONObject payload = getRequest().getPayload();
    String type = payload.getString("using");
    String value = payload.getString("value");

    RemoteWebElement element = null;

    if (getRequest().hasVariable(":reference")) {
      int id = Integer.parseInt(getRequest().getVariableValue(":reference"));
      element = new RemoteWebElement(new NodeId(id), getSession());
    } else {
      element = getSession().getWebInspector().getDocument();
    }

    RemoteWebElement rwe;

    if ("link text".equals(type)) {
      rwe = element.findElementByLinkText(value, false);
    } else if ("partial link text".equals(type)) {
      rwe = element.findElementByLinkText(value, true);
    } else if ("xpath".equals(type)) {
      rwe = element.findElementByXpath(value);
    } else {
      String cssSelector = ToCSSSelectorConvertor.convertToCSSSelector(type, value);
      rwe = element.findElementByCSSSelector(cssSelector);
    }
    return rwe;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }

}
