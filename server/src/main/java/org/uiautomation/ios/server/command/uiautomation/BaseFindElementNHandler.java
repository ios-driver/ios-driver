package org.uiautomation.ios.server.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.InvalidSelectorException;
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
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.application.LanguageDictionary;
import org.uiautomation.ios.server.application.ServerSideL10NDecorator;
import org.uiautomation.ios.server.command.UIAScriptHandler;
import org.uiautomation.ios.server.utils.JSONToXMLConvertor;
import org.uiautomation.ios.server.utils.XPath2Engine;
import org.uiautomation.ios.server.utils.XPathWithL10N;

public abstract class BaseFindElementNHandler extends UIAScriptHandler {

  private final boolean xpathMode;
  private final String reference;

  public BaseFindElementNHandler(IOSDriver driver, WebDriverLikeRequest request) {
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
    JSONObject logElementTree = getSession().getNativeDriver().logElementTree(null, false);
    JSONObject tree = logElementTree.optJSONObject("tree");
    try {
      return new XPath2Engine(new JSONToXMLConvertor(tree).asXML());
    } catch (Exception e) {
      throw new WebDriverException(e);
    }

  }

  protected String getXpath() {
    if (!xpathMode) {
      throw new WebDriverException("Bug. parser only apply to xpath mode.");
    }
    String original = getRequest().getPayload().optString("value");
    return getL10NValue(original);
  }


  /**
   * replaces l10n('xyz') by the localized version of it.
   */
  private String getL10NValue(String original) {

    XPathWithL10N l10ned = new XPathWithL10N(original);

    IOSApplication app = getDriver().getSession(getRequest().getSession()).getApplication();
    for (String key : l10ned.getKeysToL10N()) {
      LanguageDictionary dict = app.getDictionary(app.getCurrentLanguage());
      String value = dict.getContentForKey(key);
      value = LanguageDictionary.getRegexPattern(value);
      l10ned.setTranslation(key, value);
    }
    return l10ned.getXPath();
  }

  protected Criteria getCriteria() {
    if (xpathMode) {
      throw new WebDriverException("Bug. Criteria do not apply for xpath mode.");
    }
    ServerSideL10NDecorator decorator = new ServerSideL10NDecorator(getSession().getApplication());
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
    } else if ("name".equals(using)) {
      Criteria c = new NameCriteria(getL10NValue(value));
      return c.stringify();
    } else if ("link text".equals(using) || "partial link text".equals(using)) {
      return createGenericCriteria(using, value);
    } else {
      throw new InvalidSelectorException(
          using + "is not a valid selector for the native part of ios-driver.");
    }
  }

  private JSONObject createGenericCriteria(String using, String value) {
    String prop = value.split("=")[0];
    String val = value.split("=")[1];
    val = getL10NValue(val);

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
