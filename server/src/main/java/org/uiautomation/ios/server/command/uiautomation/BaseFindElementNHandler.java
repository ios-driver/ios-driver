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
    this.reference = request.hasVariable(":reference") ? request.getVariableValue(":reference") : "1";
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
    String xpath = getRequest().getPayload().optString("value");
    XPathWithL10N l10ned = new XPathWithL10N(xpath);
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
