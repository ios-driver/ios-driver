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
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.BaseWebCommandHandler;
import org.uiautomation.ios.webInspector.DOM.RemoteWebElement;

import java.util.List;

public class SetFrameHandler extends BaseWebCommandHandler {

  public SetFrameHandler(IOSDriver driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  // NoSuchWindow - If the currently selected window has been closed.
  // NoSuchFrame - If the frame specified by id cannot be found.
  @Override
  public Response handle() throws Exception {
    Object p = getRequest().getPayload().get("id");

    if (JSONObject.NULL.equals(p)) {
      getSession().getRemoteWebDriver().getContext().setCurrentFrame(null, null, null);
    } else {
      RemoteWebElement iframe;
      if (p instanceof String) {
        iframe = getIframe((String) p);
      } else if (p instanceof Integer) {
        iframe = getIframe((Integer) p);
      } else if (p instanceof JSONObject) {
        String id = ((JSONObject) p).getString("ELEMENT");
        iframe = getSession().getRemoteWebDriver().createElement(id);
      } else {
        throw new UnsupportedCommandException("not supported : frame selection by " + p.getClass());
      }

      RemoteWebElement document = iframe.getContentDocument();
      RemoteWebElement window = iframe.getContentWindow();
      getSession().getRemoteWebDriver().getContext().setCurrentFrame(iframe, document, window);
    }

    Response res = new Response();
    res.setSessionId(getSession().getSessionId());
    res.setStatus(0);
    res.setValue(new JSONObject());
    return res;
  }

  private RemoteWebElement getIframe(Integer index) throws Exception {
    List<RemoteWebElement> iframes = getSession().getRemoteWebDriver().findElementsByCssSelector(
        "iframe,frame");
    try {
      return iframes.get(index);
    } catch (IndexOutOfBoundsException i) {
      throw new NoSuchFrameException(
          "detected " + iframes.size() + " frames. Cannot get index = " + index);
    }
  }

  private RemoteWebElement getIframe(String id) throws Exception {
    RemoteWebElement currentDocument = getSession().getRemoteWebDriver().getDocument();

    String
        selector =
        "iframe[name='" + id + "'],iframe[id='" + id + "'],frame[name='" + id + "'],frame[id='" + id
        + "']";
    try {
      RemoteWebElement frame = currentDocument.findElementByCSSSelector(selector);
      return frame;
    } catch (NoSuchElementException e) {
      throw new NoSuchFrameException(e.getMessage(), e);
    }

    // string|number|null|WebElement JSON Object

  }

  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
