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
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.predicate.AbstractCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.LabelCriteria;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.UIAModels.predicate.ValueCriteria;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.application.IOSRunningApplication;
import org.uiautomation.ios.server.application.ServerSideL10NDecorator;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.utils.XPath2Engine;

public abstract class BaseFindElementNHandler extends UIAScriptHandler {

  private final boolean xpathMode;
  private final String reference;

  public BaseFindElementNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);
    this.xpathMode = getRequest().getPayload().has("using")
                     && "xpath".equals(getRequest().getPayload().optString("using"));
    this.reference =
        request.hasVariable(":reference") ? request.getVariableValue(":reference") : "1";
  }

  protected boolean isXPathMode() {
    return xpathMode;
  }

  protected String getReference() {
    return reference;
  }

  protected XPath2Engine getParser() {
    if (!xpathMode) {
      throw new WebDriverException("Bug. parser only apply to xpath mode.");
    }
    return XPath2Engine.getXpath2Engine(getSession().getDualDriver().getNativeDriver());
  }

  protected String getXpath() {
    if (!xpathMode) {
      throw new WebDriverException("Bug. parser only apply to xpath mode.");
    }
    String original = getRequest().getPayload().optString("value");
    return getAUT().applyL10N(original);
  }



  protected Criteria getCriteria() {
    if (xpathMode) {
      throw new WebDriverException("Bug. Criteria do not apply for xpath mode.");
    }
    ServerSideL10NDecorator decorator = new ServerSideL10NDecorator(getAUT());
    JSONObject json;
    try {
      json = getCriteria(getRequest().getPayload());
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }
    Criteria decorated = AbstractCriteria.parse(json, decorator);
    return decorated;

  }

  /**
   * create the criteria for the request. If the request follows the webdriver protocol, maps it to
   * a criteria ios-driver understands.
   */
  private JSONObject getCriteria(JSONObject payload) throws JSONException {
    if (payload.has("criteria")) {
      JSONObject json = payload.getJSONObject("criteria");
      return json;
    } else if (payload.has("using")) {
      return getCriteriaFromWebDriverSelector(payload);
    } else {
      throw new InvalidSelectorException("wrong format for the findElement command " + payload);
    }
  }

  /**
   * handles the mapping from the webdriver using to a criteria.
   */
  private JSONObject getCriteriaFromWebDriverSelector(JSONObject payload) throws JSONException {
    String using = payload.getString("using");
    String value = payload.getString("value");
    if ("tag name".equals(using) || "class name".equals(using)) {
      try {
        Package p = UIAElement.class.getPackage();
        Criteria c = new TypeCriteria(Class.forName(p.getName() + "." + value));
        return c.stringify();
      } catch (ClassNotFoundException e) {
        throw new InvalidSelectorException(value + " is not a recognized type.");
      }
      //  http://developer.apple.com/library/ios/#documentation/uikit/reference/UIAccessibilityIdentification_Protocol/Introduction/Introduction.html
    } else if ("name".equals(using) || "id".equals(using)) {
      Criteria c = new NameCriteria(getAUT().applyL10N(value));
      return c.stringify();
    } else if ("link text".equals(using) || "partial link text".equals(using)) {
      return createGenericCriteria(using, value);
    } else {
      throw new InvalidSelectorException(
          using + "is not a valid selector for the native part of ios-driver.");
    }
  }

  protected abstract <T> T find();

  protected <T> T findByXpathWithImplicitWait() {
    long now = System.currentTimeMillis();
    long deadline = now + SetImplicitWaitTimeoutNHandler.TIMEOUT;
    do {
      try {
        return find();
      } catch (NoSuchElementException ignore) {
      }
    }
    while (SetImplicitWaitTimeoutNHandler.TIMEOUT != 0 && System.currentTimeMillis() < deadline);
    return find();
  }

  private JSONObject createGenericCriteria(String using, String value) {
    String prop = value.split("=")[0];
    String val = value.split("=")[1];
    val = getAUT().applyL10N(val);

    MatchingStrategy strategy = MatchingStrategy.exact;

    // for partial matching, remove the quotes.
    if ("partial link text".equals(using)) {
      val = val.substring(1, val.length() - 1);
      strategy = MatchingStrategy.regex;
    }

    if ("name".equals(prop)) {
      return new NameCriteria(val, strategy).stringify();
    } else if ("value".equals(prop)) {
      return new ValueCriteria(val, strategy).stringify();
    } else if ("label".equals(prop)) {
      return new LabelCriteria(val, strategy).stringify();
    } else {
      throw new InvalidSelectorException(
          prop
          + "is not a valid selector for the native part of ios-driver.name | value | label");
    }
  }
}
