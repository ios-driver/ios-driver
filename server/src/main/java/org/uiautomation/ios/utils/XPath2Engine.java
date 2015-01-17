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

package org.uiautomation.ios.utils;

import com.google.common.collect.ImmutableMap;

import net.sf.saxon.Configuration;
import net.sf.saxon.dom.DOMNodeList;
import net.sf.saxon.lib.NamespaceConstant;

import net.sf.saxon.xpath.XPathFactoryImpl;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class XPath2Engine {

  private final XPath xpath2;
  private final Node document;
  private final String xml;

  public static XPath2Engine getXpath2Engine(RemoteIOSDriver driver) {
    JSONObject logElementTree = driver.logElementTree(null, false);
    JSONObject tree = logElementTree.optJSONObject("tree");
    try {
      String locale = driver.getCapabilities().getLocale();
      locale = (locale != null) ? locale.replace('_', '-') : null;
      return new XPath2Engine(new JSONToXMLConverter(tree).asXML(), locale);
    } catch (Exception e) {
      throw new WebDriverException(e);
    }
  }

  public XPath2Engine(String xml, String locale) throws Exception {
    this.xml = xml;
    String objectModel = NamespaceConstant.OBJECT_MODEL_SAXON;
    System.setProperty("javax.xml.xpath.XPathFactory:" + objectModel,
                       "net.sf.saxon.xpath.XPathFactoryImpl");
    XPathFactoryImpl xpathFactory = (XPathFactoryImpl) XPathFactory.newInstance(objectModel);
    // For certain locales 'th-TH', certain characters have redundant representation. So a code point match may return
    // false for a different representation of the same character. Eg  ํา can be represented as a single unicode point
    // (0xe33) or as 2 unicode points  ํ (0xe4d) า (0xe32). A code point matching algorithm isEqual(0xe33, 0xe4d0xe32)
    // will return false. We need a matching algorithm which does canonical equivalence. Java has a class called
    // Collator (RuleBasedCollator) which can handle such cases. We can tune saxon parser to configure our
    // RuleBasedCollator. I am using both full decomposition and locale. For thai it seems, just the locale is
    // sufficient in JDK 8 but we need both in JDK 7.
    //
    // Full decomposition may slow down string comparisons. So just avoiding it in en-US. If required we may avoid it in
    // most locales (I believe).
     if (locale != null && !locale.contentEquals("en-US")) {
      Configuration configuration = Configuration.newConfiguration();
      configuration.getCollationMap().setDefaultCollationName("http://saxon.sf.net/collation?decomposition=full&lang="
          + locale);
      xpathFactory.setConfiguration(configuration);
    }

    /*XPathEvaluator xpath2 = new XPathEvaluator();
    net.sf.saxon.Configuration c = new Configuration();
    c.getCharacterSetFactory().setCharacterSetImplementation(encoding, charSet)*/
    xpath2 = xpathFactory.newXPath();
    DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

    DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
    //document = docBuilder.parse(new File("/Users/freynaud/Documents/workspace/ios-driver/server/zh.xml"));

    document = docBuilder.parse(IOUtils.toInputStream(xml, "UTF-8"));
  }

  public List<Map<String, String>> findElementsByXpath(String xpath) {
    try {
      return findElementsByXpath(xpath, document);
    } catch (XPathExpressionException e) {
      throw new WebDriverException(e);
    }
  }

  public Map<String, String> findElementByXpath(String xpath) {
    try {
      return findElementByXpath(xpath, document);
    } catch (XPathExpressionException e) {
      throw new InvalidSelectorException("wrong xpath " + xpath, e);
    }
  }

  public Map<String, String> findElementByXpath(String xpath, String reference) {
    try {
      return findElementByXpath(xpath, getNode(reference));
    } catch (XPathExpressionException e) {
      throw new InvalidSelectorException("wrong xpath " + xpath, e);
    }

  }

  public List<Map<String, String>> findElementsByXpath(String xpath, String reference) {
    try {
      return findElementsByXpath(xpath, getNode(reference));
    } catch (XPathExpressionException e) {
      throw new InvalidSelectorException("wrong xpath " + xpath, e);
    }
  }

  private List<Map<String, String>> findElementsByXpath(String xpath, Node from)
      throws XPathExpressionException {
    XPathExpression expr = xpath2.compile(xpath);
    DOMNodeList elements;

    elements = (DOMNodeList) expr.evaluate(document, XPathConstants.NODESET);

    List<Map<String, String>> res = new ArrayList<Map<String, String>>();
    for (int i = 0; i < elements.getLength(); i++) {
      Element el = (Element) elements.item(i);
      String reference = el.getAttribute("ref");
      String type = el.getNodeName();
      res.add(ImmutableMap.of("ELEMENT", reference, "type", type));
    }

    return res;
  }

  public static void main(String[] args)
      throws UnsupportedEncodingException, XPathExpressionException, JSONException {
    final
    String
        intlMountainZH =
        "{\"ref\":1,\"name\":\"山\",\"value\":null,\"children\":[{\"ref\":7,\"name\":null,\"value\":null,\"children\":[{\"ref\":8,\"name\":\"山 Detail\",\"value\":null,\"children\":[{\"ref\":9,\"name\":null,\"value\":null,\"children\":[{\"ref\":10,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":0},\"size\":{\"height\":3,\"width\":320}},\"type\":\"UIAImage\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":20,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAImage\"},{\"ref\":11,\"name\":\"山\",\"value\":null,\"label\":\"山\",\"rect\":{\"origin\":{\"y\":27,\"x\":5},\"size\":{\"height\":30,\"width\":48}},\"type\":\"UIAButton\"},{\"ref\":12,\"name\":\"山 Detail\",\"value\":\"山 Detail\",\"label\":\"山 Detail\",\"rect\":{\"origin\":{\"y\":29,\"x\":119},\"size\":{\"height\":27,\"width\":82}},\"type\":\"UIAStaticText\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":20,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIANavigationBar\"},{\"ref\":13,\"name\":\"山 1 是8,848米高。它第一次攀登了在29 May 1953。\",\"value\":\"山 1 是8,848米高。它第一次攀登了在29 May 1953。\",\"label\":\"山 1 是8,848米高。它第一次攀登了在29 May 1953。\",\"rect\":{\"origin\":{\"y\":168,\"x\":34},\"size\":{\"height\":164,\"width\":251}},\"type\":\"UIAStaticText\"},{\"ref\":14,\"name\":null,\"value\":null,\"children\":[{\"ref\":15,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":477,\"x\":0},\"size\":{\"height\":3,\"width\":320}},\"type\":\"UIAImage\"},{\"ref\":16,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":480,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAImage\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":480,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAToolbar\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":0},\"size\":{\"height\":480,\"width\":320}},\"type\":\"UIAWindow\"},{\"ref\":17,\"name\":null,\"value\":null,\"children\":[{\"ref\":18,\"name\":null,\"value\":null,\"children\":[{\"ref\":19,\"name\":\"使用三指向下滑過螢幕來顯示通知中心。, 點兩下來捲動到頁首\",\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":4},\"size\":{\"height\":20,\"width\":52}},\"type\":\"UIAElement\"},{\"ref\":20,\"name\":\"Wi-Fi 訊號強度 3 格(共 3 格)\",\"value\":null,\"label\":\"Wi-Fi 訊號強度 3 格(共 3 格)\",\"rect\":{\"origin\":{\"y\":0,\"x\":61},\"size\":{\"height\":20,\"width\":20}},\"type\":\"UIAElement\"},{\"ref\":21,\"name\":\"14:17\",\"value\":null,\"label\":\"14:17\",\"rect\":{\"origin\":{\"y\":0,\"x\":142},\"size\":{\"height\":20,\"width\":36}},\"type\":\"UIAElement\"},{\"ref\":22,\"name\":\"電池電力 100%\",\"value\":null,\"label\":\"電池電力 100%\",\"rect\":{\"origin\":{\"y\":0,\"x\":296},\"size\":{\"height\":20,\"width\":21}},\"type\":\"UIAElement\"},{\"ref\":23,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":0},\"size\":{\"height\":3,\"width\":3}},\"type\":\"UIAImage\"},{\"ref\":24,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":317},\"size\":{\"height\":3,\"width\":3}},\"type\":\"UIAImage\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":0},\"size\":{\"height\":20,\"width\":320}},\"type\":\"UIAStatusBar\"},{\"ref\":25,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":477,\"x\":0},\"size\":{\"height\":3,\"width\":3}},\"type\":\"UIAImage\"},{\"ref\":26,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":477,\"x\":317},\"size\":{\"height\":3,\"width\":3}},\"type\":\"UIAImage\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":0},\"size\":{\"height\":480,\"width\":320}},\"type\":\"UIAWindow\"}],\"label\":\"山\",\"rect\":{\"origin\":{\"y\":20,\"x\":0},\"size\":{\"height\":460,\"width\":320}},\"type\":\"UIAApplication\"}";
    String xml = new JSONToXMLConverter(new JSONObject(intlMountainZH)).asXML();

    InputStream is = new ByteArrayInputStream(xml.getBytes("UTF8"));
    XPath xpath = XPathFactory.newInstance().newXPath();
    String xpathExpression = "/UIAApplication";
    InputSource inputSource = new InputSource(is);
    String handle = xpath.evaluate(xpathExpression, inputSource);
  }

  private Map<String, String> findElementByXpath(String xpath, Node from)
      throws XPathExpressionException {
    XPathExpression expr = xpath2.compile(xpath);
    Element el = (Element) expr.evaluate(document, XPathConstants.NODE);
    if (el == null) {
      throw new NoSuchElementException("No element for xpath " + xpath);
    }

    checkIfElementIsAccessible(el);
    String reference = el.getAttribute("ref");
    String type = el.getNodeName();
    return ImmutableMap.of("ELEMENT", reference, "type", type);
  }

  private void checkIfElementIsAccessible(Element el) throws XPathExpressionException {
    XPathExpression expr = xpath2.compile("//UIAAlert | //UIAActionSheet ");
    Element alert = (Element) expr.evaluate(document, XPathConstants.NODE);
    if (alert == null) {
      // no alert, no problem
      return;
    }
    // alert detected. The xpath should only return what's in the alert.
    Node parent = el;
    while (parent != null) {
      if (parent.getNodeName().equals("UIAAlert") || parent.getNodeName()
          .equals("UIAActionSheet")) {
        return;
      }
      parent = parent.getParentNode();
    }
    throw new UnhandledAlertException("cannot select this element. There is an alert.");
  }

  private Node getNode(String reference) throws XPathExpressionException {
    String xpath = "//*[@ref=" + reference + "]";
    XPathExpression expr = xpath2.compile(xpath);
    Node n = (Node) expr.evaluate(document, XPathConstants.NODE);
    if (n == null) {
      throw new NoSuchElementException("Element with ref " + reference + " doesn't exist.");
    }
    return n;
  }

}
