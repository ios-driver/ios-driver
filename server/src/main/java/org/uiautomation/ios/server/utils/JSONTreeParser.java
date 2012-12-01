package org.uiautomation.ios.server.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.InvalidXPathException;
import org.dom4j.Node;
import org.dom4j.XPathException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;

import com.google.common.collect.ImmutableMap;

public class JSONTreeParser {

  private final Document xml;

  public JSONTreeParser(JSONObject tree) {
    xml = buildXMLDoc(tree);
  }

  public List<Map<String, String>> findElementsByXpath(String xpath) {
    return findElementsByXpath(xpath, xml);
  }

  public Map<String, String> findElementByXpath(String xpath) {
    return findElementByXpath(xpath, xml);
  }
  
  public Map<String, String> findElementByXpath(String xpath,String reference) {
    System.out.println(xml.asXML());
    return findElementByXpath(xpath, getNode(reference));
  }
  
  public List<Map<String, String>> findElementsByXpath(String xpath, String reference) {
    return findElementsByXpath(xpath, getNode(reference));
  }

  private List<Map<String, String>> findElementsByXpath(String xpath, Node from) {
    List<Element> elements;
    try {
      elements = (List<Element>) xml.selectNodes(xpath);
    } catch (InvalidXPathException e) {
      throw new InvalidSelectorException(e.getMessage());
    } catch (XPathException e) {
      throw new InvalidSelectorException(e.getMessage());
    }
    List<Map<String, String>> res = new ArrayList<Map<String, String>>();
    for (Element el : elements) {
      String reference = el.attributeValue("ref");
      String type = el.getName();
      res.add(ImmutableMap.of("ELEMENT", reference, "type", type));

    }
    return res;
  }

  private Map<String, String> findElementByXpath(String xpath, Node from) {

    Element element = null;
    try {
      element = (Element) from.selectSingleNode(xpath);
    } catch (InvalidXPathException e) {
      throw new InvalidSelectorException(e.getMessage());
    } catch (XPathException e) {
      throw new InvalidSelectorException(e.getMessage());
    }

    if (element == null) {
      throw new NoSuchElementException("no element matching xpath=" + xpath);
    }
    String reference = element.attributeValue("ref");
    String type = element.getName();
    return ImmutableMap.of("ELEMENT", reference, "type", type);
  }

  private Node getNode(String reference) {
    String xpath = "//*[@ref=" + reference + "]";
    Node n = xml.selectSingleNode(xpath);
    if (n == null) {
      throw new NoSuchElementException("Element with ref " + reference + " doesn't exist.");
    }
    return n;
  }

  private Document buildXMLDoc(JSONObject tree) {
    Document document = DocumentHelper.createDocument();
    Element root = document.addElement("root");
    buildXMLNode(tree, root);
    return document;

  }

  private static void buildXMLNode(JSONObject from, Element parent) {
    if (from == null) {
      return;
    }
    Element node = parent.addElement(from.optString("type"))
        .addAttribute("name", from.optString("name"))
        .addAttribute("label", from.optString("label"))
        .addAttribute("value", from.optString("value"))
        .addAttribute("ref", from.optString("ref"));
    
    JSONArray array = from.optJSONArray("children");
    if (array != null) {
      for (int i = 0; i < array.length(); i++) {
        JSONObject n = array.optJSONObject(i);
        buildXMLNode(n, node);
      }
    }
  }
}
