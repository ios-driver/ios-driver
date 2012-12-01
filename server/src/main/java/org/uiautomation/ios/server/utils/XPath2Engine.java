package org.uiautomation.ios.server.utils;

import java.io.StringReader;
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

import net.sf.saxon.dom.DOMNodeList;
import net.sf.saxon.lib.NamespaceConstant;

import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.google.common.collect.ImmutableMap;

public class XPath2Engine {

  private final XPath xpath2;
  private final Node document;

  public XPath2Engine(String xml) throws Exception {
    String objectModel = NamespaceConstant.OBJECT_MODEL_SAXON;
    System.setProperty("javax.xml.xpath.XPathFactory:" + objectModel, "net.sf.saxon.xpath.XPathFactoryImpl");

    XPathFactory xpathFactory = XPathFactory.newInstance(objectModel);
    xpath2 = xpathFactory.newXPath();
    DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

    DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
    document = docBuilder.parse(new InputSource(new StringReader(xml)));
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

  private List<Map<String, String>> findElementsByXpath(String xpath, Node from) throws XPathExpressionException {
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

  private Map<String, String> findElementByXpath(String xpath, Node from) throws XPathExpressionException {
    XPathExpression expr = xpath2.compile(xpath);
    Element el = (Element) expr.evaluate(document, XPathConstants.NODE);
    if (el == null) {
      throw new NoSuchElementException("No element for xpath " + xpath);
    }
    String reference = el.getAttribute("ref");
    String type = el.getNodeName();
    return ImmutableMap.of("ELEMENT", reference, "type", type);
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
