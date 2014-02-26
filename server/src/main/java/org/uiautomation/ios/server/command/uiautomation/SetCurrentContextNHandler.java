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

package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.communication.Path;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.CommandMapping;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.BaseNativeCommandHandler;
import org.uiautomation.ios.utils.IOSVersion;
import org.uiautomation.ios.wkrdp.message.WebkitPage;

import java.util.List;

public class SetCurrentContextNHandler extends BaseNativeCommandHandler {


  public SetCurrentContextNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
  }

  @Override
  public Response handle() throws Exception {
    String context = getRequest().getPayload().getString("name");
    final WorkingMode mode;
    if (context.startsWith(WorkingMode.Web.toString())) {
      mode = WorkingMode.Web;
    } else {
      mode = WorkingMode.valueOf(context);
    }
    getIOSDualDriver().setMode(mode);

    if (context.startsWith(WorkingMode.Web + "_")) {
      if (getWebDriver().getWindowHandles().isEmpty()) {
        throw new NoSuchWindowException("Cannot find a web view in the current app.");
      }
      if (WorkingMode.Web.toString().equals(context)) {
        getSession().getDualDriver().setMode(WorkingMode.Web);
      } else {
        String pageId = context.replace(WorkingMode.Web + "_", "");

        // TODO freynaud. 2 windows doesn't mean 2 pages ...
        int delta = getWebDriver().getWindowHandleIndexDifference(pageId);
        if (delta != 0) {
          if (getSession().getApplication().isSafari()) {
            String sdk = getSession().getCapabilities().getSDKVersion();
            String version = new IOSVersion(sdk).getMajor();

            switch (version) {
              case "6":
                getNativeDriver().executeScript(
                    "new SafariPageNavigator().enter().goToWebView(" + delta + ");");
                break;
              case "7":
                List<WebkitPage> pages = getWebDriver().getPages();

                WebkitPage desired = null;
                for (WebkitPage page : pages) {
                  if (pageId.equals(String.format("%d", page.getPageId()))) {
                    desired = page;
                    break;
                  }
                }
                if (desired == null) {
                  throw new WebDriverException("unknown pageId:" + pageId);
                }
                String title = desired.getTitle();
                By locator = By.xpath("//UIAStaticText[@name='" + title + "']");
                getNativeDriver().executeScript(
                    "new SafariPageNavigator().enter();");

                WebElement element = getNativeDriver().findElement(locator);
                try {
                  getIOSDualDriver().setMode(WorkingMode.Native);
                  element.click();
                } finally {
                  getIOSDualDriver().setMode(WorkingMode.Web);
                }
                break;
              default:
                throw new WebDriverException("Cannot switch pages on version " + version);
            }
          } else {
            // TODO?
          }
        }
        getWebDriver().switchTo(pageId);
      }
    }
    // Set the current implicit wait timeout, if one has been set.
    // This is to persist the implicit wait timeout after switching to webview and back.
    if (SetImplicitWaitTimeoutNHandler.TIMEOUT != null) {
      // mocking a request to SET_TIMEOUT
      JSONObject payload = new JSONObject();
      payload.append("ms", SetImplicitWaitTimeoutNHandler.TIMEOUT);
      WebDriverLikeRequest
          wdlr =
          new WebDriverLikeRequest("POST", new Path(WebDriverLikeCommand.SET_TIMEOUT), payload);

      // set the timeout by 'handling' the request, we don't care about it's response.
      try {
        CommandMapping.SET_TIMEOUT.createHandler(getServer(), wdlr).handle();
      } catch (Exception e) {
        // dump out any exception, but ignore it as the primary concern is switching context
        // which has succeeded if we got here
//        e.printStackTrace();
      }
    }

    Response resp = new Response();
    resp.setSessionId(getSession().getSessionId());
    resp.setStatus(0);
    resp.setValue(new JSONObject());
    return resp;
  }


  @Override
  public JSONObject configurationDescription() throws JSONException {
    return noConfigDefined();
  }
}
