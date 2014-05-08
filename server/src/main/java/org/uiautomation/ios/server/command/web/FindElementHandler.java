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
package org.uiautomation.ios.server.command.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;
import org.uiautomation.ios.wkrdp.RemoteExceptionException;
import org.uiautomation.ios.wkrdp.model.RemoteWebElement;

public class FindElementHandler extends BaseWebCommandHandler {

  public FindElementHandler(IOSServerManager driver, WebDriverLikeRequest request) {
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
      } catch (InvalidSelectorException e) {
        // no recovery here.
        throw e;
      } catch (NoSuchElementException e) {
        //ignore and try again.
      } catch (RemoteExceptionException e2) {
        // looking on the root element, but document became invalid.
        // Something (alert during onload ) might have prevented the page
        // refresh.
        if (!getRequest().hasVariable(":reference")) {
          getWebDriver().getContext().newContext();
        }

      }
    } while (System.currentTimeMillis() < deadline);

    if (rwe == null) {
      throw new NoSuchElementException(
          "No element found for " + getRequest().getPayload() + " after waiting for " + implicitWait
          + " ms.");
    } else {
      JSONObject res = new JSONObject();
      res.put("ELEMENT", rwe.getReference());
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
      String reference = getRequest().getVariableValue(":reference");
      element = getWebDriver().createElement(reference);
    } else {
      element = getWebDriver().getDocument();
    }
    RemoteWebElement rwe;
    if ("link text".equals(type)) {
      rwe = element.findElementByLinkText(value, false);
    } else if ("partial link text".equals(type)) {
      rwe = element.findElementByLinkText(value, true);
    } else if ("xpath".equals(type)) {
      rwe = element.findElementByXpath(value);
    } else {
      String cssSelector = ToCSSSelectorConverter.convertToCSSSelector(type, value);
      rwe = element.findElementByCSSSelector(cssSelector);
    }
    return rwe;
  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
